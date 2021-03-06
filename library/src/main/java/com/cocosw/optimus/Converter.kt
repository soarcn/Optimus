package com.cocosw.optimus

import com.google.gson.Gson
import com.squareup.moshi.Moshi
import okhttp3.MediaType
import okhttp3.ResponseBody

interface Converter {
    fun convert(obj: Any?): ResponseBody

    companion object {
        fun create(moshi: Moshi): Converter {
            return object : Converter {
                override fun convert(obj: Any?): ResponseBody {
                    return ResponseBody.create(
                        MediaType.parse("application/json"),
                        if (obj != null) moshi.adapter(obj.javaClass).toJson(obj) else ""
                    )
                }
            }
        }

        fun create(gson: Gson): Converter {
            return object : Converter {
                override fun convert(obj: Any?): ResponseBody {
                    return ResponseBody.create(
                        MediaType.parse("application/json"),
                        if (obj != null) gson.toJson(obj) else ""
                    )
                }
            }
        }

        internal fun default(): Converter {
            return object : Converter {
                override fun convert(obj: Any?): ResponseBody {
                    throw NotImplementedError("converter is required")
                }
            }
        }
    }
}
