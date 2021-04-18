package com.codeborne.selenide

class ClickOptions private constructor(
    private val clickMethod: ClickMethod,
    private val offsetX: Int,
    private val offsetY: Int
) {
    fun offsetX(): Int {
        return offsetX
    }

    fun offsetY(): Int {
        return offsetY
    }

    fun clickOption(): ClickMethod {
        return clickMethod
    }

    fun offsetX(offsetX: Int): ClickOptions {
        return ClickOptions(clickMethod, offsetX, offsetY)
    }

    fun offsetY(offsetY: Int): ClickOptions {
        return ClickOptions(clickMethod, offsetX, offsetY)
    }

    fun offset(offsetX: Int, offsetY: Int): ClickOptions {
        return ClickOptions(clickMethod, offsetX, offsetY)
    }

    override fun toString(): String {
        return if (offsetX == 0 && offsetY == 0) {
            "method: $clickMethod"
        } else {
            "method: $clickMethod, offsetX: $offsetX, offsetY: $offsetY"
        }
    }

    companion object {
        fun usingDefaultMethod(): ClickOptions {
            return ClickOptions(ClickMethod.DEFAULT, 0, 0)
        }

        fun usingJavaScript(): ClickOptions {
            return ClickOptions(ClickMethod.JS, 0, 0)
        }
    }
}
