package support

expect object System {
    fun getProperty(key: String, defaultValue: String): String
    fun getProperty(key: String): String?
    fun nanoTime(): Long
    fun currentTimeMillis(): Long
    fun currentThreadId(): Long
    fun getenv(key: String): String
}
