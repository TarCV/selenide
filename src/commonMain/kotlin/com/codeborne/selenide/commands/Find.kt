package com.codeborne.selenide.commands

import com.codeborne.selenide.CommandSync
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource

class Find : CommandSync<SelenideElement> {
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>): SelenideElement {
      checkNotNull(args)
      return if (args.size == 1) {
          locator.find(proxy, args[0]!!, 0)
      } else {
          locator.find(
              proxy,
              args[0]!!,
              args[1] as Int
          )
      }
    }
}
