package com.codeborne.selenide.impl

import com.benasher44.uuid.uuid4

open class Randomizer {
    open fun text(): String {
        return uuid4().toString()
    }
}
