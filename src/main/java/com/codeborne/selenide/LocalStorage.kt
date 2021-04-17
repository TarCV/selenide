package com.codeborne.selenide

import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class LocalStorage internal constructor(driver: Driver) : JSStorage(driver, "localStorage")
