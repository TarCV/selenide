package org.openqa.selenium

interface JavascriptExecutor {
    fun <T> executeScript(code: String, vararg arg: Any): T
    fun <T> executeAsyncScript(code: String, vararg arg: Any): T
}
