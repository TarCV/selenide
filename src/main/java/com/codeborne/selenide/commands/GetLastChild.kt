package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import javax.annotation.ParametersAreNonnullByDefault
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import org.openqa.selenium.By
import javax.annotation.CheckReturnValue

@ParametersAreNonnullByDefault
class GetLastChild : Command<SelenideElement> {
    private val find: Find

    constructor() {
        find = Find()
    }

    constructor(find: Find) {
        this.find = find
    }

    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): SelenideElement {
        return find.execute(proxy, locator, arrayOf(By.xpath("*[last()]"), 0))
    }
}
