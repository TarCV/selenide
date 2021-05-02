package org.openqa.selenium

actual open class WebDriverException: Exception() {
    actual fun addInfo(key: String, value: String): Unit = TODO()
}
