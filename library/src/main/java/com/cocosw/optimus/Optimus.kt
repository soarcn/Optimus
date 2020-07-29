package com.cocosw.optimus

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.Calls
import retrofit2.mock.MockRetrofit
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.jvm.javaMethod


class Optimus(
    internal val retrofit: MockRetrofit,
    internal val supplier: MockResponseSupplier
) {
    internal var converter: Converter = object : Converter {
        override fun convert(obj: Any): ResponseBody {
            throw NotImplementedError()
        }
    }
    internal lateinit var response: Array<out MockService>

    // Single-interface proxy creation guarded by parameter safety.
    fun <T> create(service: Class<T>): T {
        val original = retrofit.retrofit().create(service)
        return Proxy.newProxyInstance(service.classLoader,
            arrayOf<Class<*>>(service),OptimusHandler(this, original,retrofit.create(service),getMockService(service))) as T
    }

    internal fun getMockService(service: Class<*>): MockService {
        try {
            return response.first { it.clazz == service }
        } catch (e:NoSuchElementException) {
            throw IllegalStateException("Can't find "+service.name)
        }
    }

    var bypass = false

    class Builder(private val supplier: MockResponseSupplier) {
        private var retrofit: MockRetrofit? = null
        private var response: Array<out MockService> = emptyArray()
        private var converterFactory: Converter?=null

        fun response(vararg responses: MockService): Builder {
            this.response = responses
            return this
        }

        fun mockRetrofit(mockRetrofit: MockRetrofit): Builder {
            retrofit = mockRetrofit
            return this
        }

        fun retrofit(retrofit: Retrofit): Builder {
            this.retrofit = MockRetrofit.Builder(retrofit).build()
            return this
        }

        fun converter(converterFactory: Converter): Builder {
            this.converterFactory = converterFactory
            return this
        }

        fun build(): Optimus {
            if (retrofit == null) {
                throw IllegalStateException("retrofit or mockRetrofit are required.")
            } else {
                val out = Optimus(retrofit!!,supplier)
                out.response = response
                converterFactory?.let { out.converter = it }
                return out
            }
        }

    }
}

class OptimusHandler<T>(
    private val optimus: Optimus,
    private val original: T,
    private val delegate: BehaviorDelegate<T>,
    private val mockService: MockService
) : InvocationHandler {
    private val TAG = "Optimus"

    override fun invoke(obj: Any, method: Method, args: Array<out Any>?): Any? {
        return if (optimus.bypass) {
            method.invoke(original,args)
        } else {
            val define = mockService.definitions.first { method == it.kFunction.javaMethod }
            val response = optimus.supplier.get(define.kClass,method)
            val value:Any = response.result?.invoke(DefinitionParameters(args))!!
            val returning = if (response.code >= 400) {
                Calls.response(Response.error<T>(response.code, optimus.converter.convert(value)))
            } else {
                Calls.response(value)
            }

            if (args==null || args.isEmpty())
                method.invoke(delegate.returning(returning))
            else {
                method.invoke(delegate.returning(returning),args)
            }
        }
    }
}

interface Converter {
    fun convert(obj:Any):ResponseBody
}
