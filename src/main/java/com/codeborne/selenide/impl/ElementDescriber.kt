package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
interface ElementDescriber {
    @CheckReturnValue
    fun fully(driver: Driver, element: WebElement?): String

    @CheckReturnValue
    fun briefly(driver: Driver, element: WebElement): String

    @CheckReturnValue
    fun selector(selector: By?): String
}
