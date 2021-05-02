package org.openqa.selenium

actual interface JavascriptExecutor {
    actual fun <T> executeScript(code: String, vararg arg: Any): T
    actual fun <T> executeAsyncScript(code: String, vararg arg: Any): T
}
