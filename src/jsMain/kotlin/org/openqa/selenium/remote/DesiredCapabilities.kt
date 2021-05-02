package org.openqa.selenium.remote

import org.openqa.selenium.Capabilities
import org.openqa.selenium.MutableCapabilities

class DesiredCapabilities(capabilities: org.openqa.selenium.Capabilities): org.openqa.selenium.MutableCapabilities(capabilities) {
    constructor() : this(org.openqa.selenium.Capabilities()) // TODO
}
