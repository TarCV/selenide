package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Val : Command<Any?> {
    private val getValue: GetValue
    private val setValue: SetValue

    constructor() {
        getValue = GetValue()
        setValue = SetValue()
    }

    constructor(getValue: GetValue, setValue: SetValue) {
        this.getValue = getValue
        this.setValue = setValue
    }

    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any>): Any? {
        return if (args == null || args.isEmpty()) {
            getValue.execute(proxy, locator, Command.NO_ARGS)
        } else {
            setValue.execute(proxy, locator, args)
            proxy
        }
    }
}
