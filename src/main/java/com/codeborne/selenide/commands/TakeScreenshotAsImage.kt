package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.ScreenShotLaboratory
import com.codeborne.selenide.impl.WebElementSource
import java.awt.image.BufferedImage
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class TakeScreenshotAsImage : Command<BufferedImage?> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<Any>?): BufferedImage? {
        return ScreenShotLaboratory.getInstance().takeScreenshotAsImage(locator.driver(), locator.webElement)
    }
}
