package support.logging

actual class Level {
    actual companion object {
        actual val ALL: Level = allConstant
    }

    actual fun intValue(): Int = TODO()
}

private val allConstant = Level()
