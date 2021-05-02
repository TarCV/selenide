package org.openqa.selenium

import org.openqa.selenium.Capabilities

actual interface HasCapabilities {
    actual val capabilities: org.openqa.selenium.Capabilities
}
