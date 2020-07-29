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

fun <E> MockResponse.notfound(
    name: String? = null,
    code: Int = 404,
    result: ((DefinitionParameters) -> E)? = null
): Response<E> {
    return Response<E>(name, code, result)
}

annotation class Default