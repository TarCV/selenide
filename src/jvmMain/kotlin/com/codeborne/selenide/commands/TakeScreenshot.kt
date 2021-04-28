package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import java.io.Path

class TakeScreenshot : Command<Path?> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): Path? {
        return ScreenShotLaboratory.instance.takeScreenshot(locator.driver(), locator.getWebElement())
    }
}
