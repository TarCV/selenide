package com.codeborne.selenide.impl

import kotlin.jvm.JvmField


class Html {
    fun matches(text: String, regex: String): Boolean {
        return Regex(".*$regex.*", RegexOption.MULTILINE).matches(text)
    }

    fun contains(text: String, subtext: String): Boolean {
        return reduceSpaces(text.toLowerCase()).contains(reduceSpaces(subtext.toLowerCase()))
    }

    fun containsCaseSensitive(text: String, subtext: String): Boolean {
        return reduceSpaces(text).contains(reduceSpaces(subtext))
    }

    fun equals(text: String, subtext: String): Boolean {
        return reduceSpaces(text).equals(reduceSpaces(subtext.toLowerCase()), ignoreCase = true)
    }

    fun equalsCaseSensitive(text: String, subtext: String): Boolean {
        return reduceSpaces(text) == reduceSpaces(subtext)
    }

    fun reduceSpaces(text: String): String {
        return REGEX_SPACES.replace(text, " ").trim { it <= ' ' }
    }

    companion object {
        private val REGEX_SPACES = kotlin.text.Regex("[\\s\\n\\r\u00a0]+")

        @JvmField
        var text = Html()
    }
}
