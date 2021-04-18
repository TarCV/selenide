package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver

class ElementIsNotClickableException(driver: Driver, cause: Throwable?) : UIAssertionError(
  driver, "Element is not clickable", cause
)
