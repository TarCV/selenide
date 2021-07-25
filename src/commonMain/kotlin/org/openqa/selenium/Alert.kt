package org.openqa.selenium

expect class Alert {
    val text: String
    fun accept(): Unit
    fun dismiss(): Unit
    fun sendKeys(keys: String): Unit
}
