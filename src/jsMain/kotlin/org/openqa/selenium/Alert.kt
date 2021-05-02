package org.openqa.selenium

actual class Alert {
    actual val text: String
        get() = TODO()

    actual fun accept(): Unit = TODO()
    actual fun dismiss(): Unit = TODO()
    actual fun sendKeys(keys: String): Unit = TODO()
}
