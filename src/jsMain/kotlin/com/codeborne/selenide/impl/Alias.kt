package com.codeborne.selenide.impl


open class Alias(text: String) {
    init {
      require(text.isNotEmpty()) { "Empty alias not allowed" }
    }
    open val text: String? = text
    open fun getOrElse(defaultValue: () -> String): String {
        return text ?: defaultValue() // TODO: java code just returned text value here
    }

    private class NoneAlias internal constructor() : Alias("-") {
        override fun getOrElse(defaultValue: () -> String): String {
            return defaultValue()
        }
        override val text: String?
            get() {
              return null
            }
      }

    companion object {
        val NONE: Alias = NoneAlias()
    }
}
