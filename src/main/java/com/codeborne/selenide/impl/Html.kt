package com.codeborne.selenide.impl

import java.util.regex.Pattern
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Html {
    fun matches(text: String, regex: String): Boolean {
        return Pattern.compile(".*$regex.*", Pattern.DOTALL).matcher(text).matches()
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
        return REGEX_SPACES.matcher(text).replaceAll(" ").trim { it <= ' ' }
    }

    companion object {
        private val REGEX_SPACES = Pattern.compile("[\\s\\n\\r\u00a0]+")
        @JvmField
        var text = Html()
    }
}
