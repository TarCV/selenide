package org.openqa.selenium

import org.openqa.selenium.remote.CapabilityType

open class MutableCapabilities(capabilities: Capabilities) : Capabilities() {
    constructor(): this(Capabilities()) // TODO

    fun merge(capabilities: Capabilities): Unit = TODO()
    open fun setCapability(key: String, value: Any?): Unit = TODO()
    open fun setCapability(key: CapabilityType, value: Any?): Unit = TODO()
    fun getCapability(key: String): Any = TODO()
}
