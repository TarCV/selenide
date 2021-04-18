package com.codeborne.selenide.impl

import java.util.function.Supplier
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
open class Alias(text: String) {
    init {
      require(text.isNotEmpty()) { "Empty alias not allowed" }
    }

    @get:CheckReturnValue
    open val text: String? = text

    @CheckReturnValue
    open fun getOrElse(defaultValue: Supplier<String>): String {
        return text ?: defaultValue.get() // TODO: java code just returned text value here
    }

    private class NoneAlias internal constructor() : Alias("-") {
        @CheckReturnValue
        override fun getOrElse(defaultValue: Supplier<String>): String {
            return defaultValue.get()
        }

        @get:CheckReturnValue
        override val text: String?
            get() {
              return null
            }
      }

    companion object {
        @JvmField
        val NONE: Alias = NoneAlias()
    }
}
