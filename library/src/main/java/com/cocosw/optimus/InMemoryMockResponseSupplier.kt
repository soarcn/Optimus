package com.cocosw.optimus

internal class InMemoryMockResponseSupplier(private val map: HashMap<String, String> = HashMap()) : MockResponseSupplier() {

    override fun save(key: String, value: String) {
        map[key] = value
    }

    override fun load(key: String): String? {
        return map[key]
    }
}
