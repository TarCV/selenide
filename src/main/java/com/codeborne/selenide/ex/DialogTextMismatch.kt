package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class DialogTextMismatch(driver: Driver, actualText: String?, expectedText: String?) : UIAssertionError(
  driver, String.format(
        "Dialog text mismatch" +
                "%nActual: %s" +
                "%nExpected: %s", actualText, expectedText
    )
)
