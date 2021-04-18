package org.openqa.selenium

open class MutableCapabilities: Capabilities() {
    fun merge(capabilities: Capabilities): Unit = TODO()
    open fun setCapability(key: String, value: Any): Unit = TODO()
    fun getCapability(key: String): Any = TODO()
}
