package org.openqa.selenium

actual interface TakesScreenshot {
    actual suspend fun <T : OutputType<T>> getScreenshotAs(type: OutputType<T>): T
}
