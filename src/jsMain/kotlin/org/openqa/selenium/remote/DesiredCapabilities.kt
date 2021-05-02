package org.openqa.selenium.remote

import org.openqa.selenium.Capabilities
import org.openqa.selenium.MutableCapabilities

class DesiredCapabilities(capabilities: Capabilities): MutableCapabilities(capabilities) {
    constructor() : this(Capabilities()) // TODO
}
