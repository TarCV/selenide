package com.codeborne.selenide.impl

import kotlin.jvm.JvmStatic


class Screenshot(val image: String?, val source: String?) {

    fun summary(): String {
        return if (image != null && source != null) {
            "\nScreenshot: ${image}\nPage source: {source}"
        } else if (source != null) {
            "\nPage source: ${source}"
        } else if (image != null) {
            "\nScreenshot: ${image}"
        } else {
            ""
        }
    }

    override fun toString(): String {
        return summary()
    }

    companion object {
        @JvmStatic
        fun none(): Screenshot {
            return Screenshot(null as String?, null)
        }
    }
}
