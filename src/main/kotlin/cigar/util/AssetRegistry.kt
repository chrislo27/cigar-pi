package cigar.util


object AssetRegistry {
    
    val map: MutableMap<String, Any> = mutableMapOf()
    
    inline operator fun <reified T> get(key: String): T {
        return map[key]!! as T
    }

    operator fun set(key: String, value: Any) {
        map[key] = value
    }
    
    fun dispose() {
        map.values.forEach { v ->
        }
        map.clear()
    }
}