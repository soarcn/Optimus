package com.cocosw.optimus

class InMemoryMockResponseSupplier : MockResponseSupplier() {
    val map: HashMap<String, String> = HashMap()

    override fun save(key: String, value: String) {
        map[key] = value
    }

    override fun load(key: String): String? {
        return map[key]
    }

    fun reset() {
        map.clear()
    }
}
