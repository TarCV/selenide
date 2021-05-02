package support.logging

actual typealias Level = java.util.logging.Level
actual val Level.ALL: java.util.logging.Level
    get() = java.util.logging.Level.ALL
