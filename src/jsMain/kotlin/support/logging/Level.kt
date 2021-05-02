package support.logging

actual class Level

private val allConstant = Level()
actual val Level.ALL: Level
    get() = allConstant
