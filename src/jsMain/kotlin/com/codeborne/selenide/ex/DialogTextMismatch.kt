package com.codeborne.selenide.ex

import com.codeborne.selenide.Driver

class DialogTextMismatch(driver: Driver, actualText: String?, expectedText: String?) : UIAssertionError(
  driver,
        "Dialog text mismatch" +
                "\nActual: $actualText" +
                "\nExpected: $expectedText"
    )
)
