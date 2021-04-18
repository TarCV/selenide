package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ElementIsNotClickableException(driver: Driver, cause: Throwable?) : UIAssertionError(
  driver, "Element is not clickable", cause
)
