package support

actual object System {
    actual fun getProperty(key: String, defaultValue: String): String = TODO()
    actual fun getProperty(key: String): String? = TODO()
    actual fun nanoTime(): Long = TODO()
    actual fun currentTimeMillis(): Long = TODO()
    actual fun currentThreadId(): Long = TODO()
    actual fun getenv(key: String): String = TODO("Not yet implemented")
}
