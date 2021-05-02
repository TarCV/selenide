package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver

class WindowNotFoundException(driver: Driver, message: String?, cause: Throwable?) : UIAssertionError(
  driver, message, cause
)
