package org.openqa.selenium

import org.openqa.selenium.Capabilities
import org.openqa.selenium.remote.CapabilityType

open actual class MutableCapabilities actual constructor(capabilities: org.openqa.selenium.Capabilities) : org.openqa.selenium.Capabilities() {
    actual constructor(): this(org.openqa.selenium.Capabilities()) // TODO

    actual fun merge(capabilities: org.openqa.selenium.Capabilities): Unit = TODO()
    actual open fun setCapability(key: String, value: Any?): Unit = TODO()
    actual open fun setCapability(key: org.openqa.selenium.remote.CapabilityType, value: Any?): Unit = TODO()
    actual fun getCapability(key: String): Any = TODO()
}
