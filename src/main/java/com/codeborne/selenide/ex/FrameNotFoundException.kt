package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class FrameNotFoundException(driver: Driver, message: String?, cause: Throwable?) : UIAssertionError(
  driver, message, cause
)
