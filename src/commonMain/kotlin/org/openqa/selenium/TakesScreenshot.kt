package org.openqa.selenium

expect interface TakesScreenshot {
    suspend fun <T : OutputType<T>> getScreenshotAs(type: OutputType<T>): T
}
