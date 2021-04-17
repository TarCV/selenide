package com.codeborne.selenide

import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class SessionStorage internal constructor(driver: Driver) : JSStorage(driver, "sessionStorage")
