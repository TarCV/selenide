package com.codeborne.selenide.impl

open class Clock {
    open fun timestamp(): Long {
        return System.currentTimeMillis()
    }
}
