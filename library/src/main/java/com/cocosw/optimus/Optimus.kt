package com.cocosw.optimus

import android.content.SharedPreferences
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.Calls
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.TimeUnit
import kotlin.reflect.jvm.javaMethod

class Optimus internal constructor(
    internal val retrofit: MockRetrofit,
    internal val supplier: MockResponseSupplier,
    internal val converter: Converter
) {
    internal lateinit var networkBehavior: OptimusNetworkBehavior
    internal lateinit var response: Array<out MockService>

    // Single-interface proxy creation guarded by parameter safety.
    fun <T> create(service: Class<T>): T {
        retrofit.networkBehavior()

        val original = retrofit.retrofit().create(service)
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service),
            OptimusHandler(this, original, retrofit.create(service), getMockService(service))
        ) as T
    }

    private fun getMockService(service: Class<*>): MockService {
        try {
            return response.first { it.clazz == service }
        } catch (e: NoSuchElementException) {
            throw IllegalStateException("Can't find " + service.name)
        }
    }

    var bypass = false

    class Builder(private val supplier: MockResponseSupplier) {
        private lateinit var networkBehavior: OptimusNetworkBehavior
        private var retrofit: MockRetrofit? = null
        private var response: Array<out MockService> = emptyArray()
        private var converter: Converter = Converter.default()

        fun mockGraph(vararg responses: MockService): Builder {
            this.response = responses
            return this
        }

        fun mockRetrofit(mockRetrofit: MockRetrofit): Builder {
            if (retrofit != null) {
                throw IllegalStateException("redundant retrofit/mock retrofit instance")
            }
            this.networkBehavior = OptimusNetworkBehavior(null, mockRetrofit.networkBehavior())
            retrofit = mockRetrofit
            return this
        }

        fun retrofit(retrofit: Retrofit, sharedPreferences: SharedPreferences? = null): Builder {
            if (this.retrofit != null) {
                throw IllegalStateException("redundant retrofit/mock retrofit instance")
            }
            this.networkBehavior = OptimusNetworkBehavior(sharedPreferences)
            this.retrofit = MockRetrofit.Builder(retrofit)
                .networkBehavior(this.networkBehavior.networkBehavior).build()
            return this
        }

        fun converter(converterFactory: Converter): Builder {
            this.converter = converterFactory
            return this
        }

        fun build(): Optimus {
            if (response.isEmpty()) {
                throw IllegalStateException("mockGraph is missing.")
            }
            if (retrofit == null) {
                throw IllegalStateException("retrofit or mockRetrofit are required.")
            } else {
                val out = Optimus(retrofit!!, supplier, converter)
                out.response = response
                out.networkBehavior = networkBehavior
                return out
            }
        }
    }
}

internal class OptimusNetworkBehavior(
    private val sharedPreferences: SharedPreferences? = null,
    internal val networkBehavior: NetworkBehavior = NetworkBehavior.create()
) {
    private val delay = "_optimus_delay"
    private val variancePercent = "_optimus_variancePercent"
    private val failurePercent = "_optimus_failurePercent"
    private val errorPercent = "_optimus_errorPercent"
    private val error = "_optimus_error"

    init {
        sharedPreferences?.apply {
            networkBehavior.setDelay(this.getLong(delay, 0), TimeUnit.MILLISECONDS)
            networkBehavior.setVariancePercent(this.getInt(variancePercent, 0))
            networkBehavior.setFailurePercent(this.getInt(failurePercent, 0))
            networkBehavior.setErrorPercent(this.getInt(errorPercent, 0))
            networkBehavior.setErrorFactory {
                Response.error<Any>(
                    this.getInt(error, 404),
                    ResponseBody.create(null, ByteArray(0))
                )
            }
        }
    }

    fun setDelay(amount: Long, unit: TimeUnit) {
        networkBehavior.setDelay(amount, unit)
        sharedPreferences?.edit()?.run {
            this.putLong(delay, amount)
            commit()
        }
    }

    /** Set the plus-or-minus variance percentage of the network round trip delay.  */
    fun setVariancePercent(variancePercent: Int) {
        networkBehavior.setVariancePercent(variancePercent)
        sharedPreferences?.edit()?.run {
            this.putInt(this@OptimusNetworkBehavior.variancePercent, variancePercent)
            commit()
        }
    }

    /** Set the percentage of calls to [.calculateIsFailure] that return `true`.  */
    fun setFailurePercent(failurePercent: Int) {
        networkBehavior.setFailurePercent(failurePercent)
        sharedPreferences?.edit()?.run {
            this.putInt(this@OptimusNetworkBehavior.failurePercent, failurePercent)
            commit()
        }
    }

    fun setErrorCode(errorCode: Int) {
        sharedPreferences?.edit()?.run {
            this.putInt(error, errorCode)
            commit()
        }
    }

    fun getErrorCode(): Int? {
        return sharedPreferences?.getInt(error, 404)
    }

    /** Set the percentage of calls to [.calculateIsError] that return `true`.  */
    fun setErrorPercent(errorPercent: Int) {
        networkBehavior.setErrorPercent(errorPercent)
        sharedPreferences?.edit()?.run {
            this.putInt(this@OptimusNetworkBehavior.errorPercent, errorPercent)
            commit()
        }
    }
}

internal class OptimusHandler<T>(
    private val optimus: Optimus,
    private val original: T,
    private val delegate: BehaviorDelegate<T>,
    private val mockService: MockService
) : InvocationHandler {
    private val TAG = "Optimus"

    override fun invoke(obj: Any, method: Method, args: Array<out Any>?): Any? {
        return if (optimus.bypass) {
            if (args == null || args.isEmpty())
                method.invoke(original)
            else {
                method.invoke(original, *args)
            }
        } else {
            val define = try {
                mockService.definitions.first { method == it.kFunction.javaMethod }
            } catch (e: NoSuchElementException) {
                throw IllegalStateException("can't find definition for method " + method.name)
            }
            val response = optimus.supplier.get(define.kClass, method)
            val value: Any? = if (args != null) {
                response.result?.invoke(DefinitionParameters(*args))
            } else {
                response.result?.invoke(DefinitionParameters(args))
            }
            val returning = if (response.code >= 400) {
                Calls.response(
                    Response.error<T>(
                        response.code,
                        optimus.converter.convert(value)
                    )
                )
            } else {
                Calls.response(value)
            }

            if (args == null || args.isEmpty())
                method.invoke(delegate.returning(returning))
            else {
                method.invoke(delegate.returning(returning), *args)
            }
        }
    }
}
