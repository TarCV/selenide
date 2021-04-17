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
        return if (offsetX == 0 && offsetY == 0) String.format(
            "method: %s",
            clickMethod
        ) else String.format("method: %s, offsetX: %s, offsetY: %s", clickMethod, offsetX, offsetY)
    }

    companion object {
        @JvmStatic
        fun usingDefaultMethod(): ClickOptions {
            return ClickOptions(ClickMethod.DEFAULT, 0, 0)
        }

        @JvmStatic
        fun usingJavaScript(): ClickOptions {
            return ClickOptions(ClickMethod.JS, 0, 0)
        }
    }
}
