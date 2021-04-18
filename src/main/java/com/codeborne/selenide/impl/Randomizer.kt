package com.codeborne.selenide.impl

import java.util.UUID
import javax.annotation.CheckReturnValue

open class Randomizer {
    @CheckReturnValue
    open fun text(): String {
        return UUID.randomUUID().toString()
    }
}
