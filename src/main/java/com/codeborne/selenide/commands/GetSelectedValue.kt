package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.WebElement
import java.io.IOException
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class GetSelectedValue : Command<String?> {
    private val getSelectedOption: Command<SelenideElement>

    constructor() {
        getSelectedOption = GetSelectedOption()
    }

    constructor(getSelectedOption: Command<SelenideElement>) {
        this.getSelectedOption = getSelectedOption
    }

    @CheckReturnValue
    @Throws(IOException::class)
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>?): String? {
        val option: WebElement? = getSelectedOption.execute(proxy, locator, args)
        return option?.getAttribute("value")
    }
}
