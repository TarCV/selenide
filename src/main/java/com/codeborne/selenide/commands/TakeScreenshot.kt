package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.ScreenShotLaboratory
import com.codeborne.selenide.impl.WebElementSource
import java.io.File
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class TakeScreenshot : Command<File?> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>?): File? {
        return ScreenShotLaboratory.instance.takeScreenshot(locator.driver(), locator.webElement)
    }
}
