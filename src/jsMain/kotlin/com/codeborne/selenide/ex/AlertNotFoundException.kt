package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver

class AlertNotFoundException(driver: Driver, message: String?, cause: Throwable?) : UIAssertionError(
  driver, message, cause
)
