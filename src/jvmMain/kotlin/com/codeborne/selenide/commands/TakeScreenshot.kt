package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import java.io.File

class TakeScreenshot : Command<File?> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): File? {
        return ScreenShotLaboratory.instance.takeScreenshot(locator.driver(), locator.getWebElement())
    }
}
