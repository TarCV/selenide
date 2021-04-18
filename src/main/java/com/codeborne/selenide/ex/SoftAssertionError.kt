package com.codeborne.selenide.ex

import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class SoftAssertionError(message: String?) : AssertionError(message)
