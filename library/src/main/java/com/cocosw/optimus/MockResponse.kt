package com.cocosw.optimus

interface MockResponse

class Response<E>(
    val name: String? = null,
    val code: Int = 200,
    val result: ((DefinitionParameters) -> E)?
)

fun <E> MockResponse.success(
    name: String? = null,
    code: Int = 200,
    result: (DefinitionParameters) -> E
): Response<E> {
    return Response<E>(name, code, result)
}

fun <E> MockResponse.error(
    name: String? = null,
    code: Int = 400,
    result: ((DefinitionParameters) -> E)? = null
): Response<E> {
    return Response<E>(name, code, result)
}

fun MockResponse.error(
    name: String? = null,
    code: Int = 400
): Response<Nothing> {
    return Response<Nothing>(name, code, null)
}

annotation class Default
