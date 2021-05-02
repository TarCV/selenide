package org.openqa.selenium

import org.openqa.selenium.WebDriver

actual interface WrapsDriver {
    actual val wrappedDriver: org.openqa.selenium.WebDriver
}
