package support

actual object System {
    actual fun getProperty(key: String, defaultValue: String): String = java.lang.System.getProperty(key, defaultValue)
    actual fun getProperty(key: String): String? = java.lang.System.getProperty(key)
    actual fun nanoTime(): Long = java.lang.System.nanoTime()
    actual fun currentTimeMillis(): Long = java.lang.System.currentTimeMillis()
    actual fun currentThreadId(): Long = Thread.currentThread().id
    actual fun getenv(key: String): String? = java.lang.System.getenv(key)
}
