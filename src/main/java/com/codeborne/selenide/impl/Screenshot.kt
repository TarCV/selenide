package com.codeborne.selenide.impl

import javax.annotation.CheckReturnValue

class Screenshot(@get:CheckReturnValue val image: String?, @get:CheckReturnValue val source: String?) {

    fun summary(): String {
        return if (image != null && source != null) {
            String.format("%nScreenshot: %s%nPage source: %s", image, source)
        } else if (source != null) {
            String.format("%nPage source: %s", source)
        } else if (image != null) {
            String.format("%nScreenshot: %s", image)
        } else {
            ""
        }
    }

    override fun toString(): String {
        return summary()
    }

    companion object {
        @JvmStatic
        @CheckReturnValue
        fun none(): Screenshot {
            return Screenshot(null as String?, null)
        }
    }
}
