package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Cleanup

class InvalidStateException : UIAssertionError {
    constructor(driver: Driver, cause: Throwable) : super(
      driver,
        "Invalid element state: " + Cleanup.of.webdriverExceptionMessage(cause.message),
        cause
    ) {
    }

    constructor(driver: Driver, message: String) : super(driver, "Invalid element state: $message")
}
