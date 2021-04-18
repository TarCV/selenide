package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Config
import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory

internal class BrowserResizer {
    fun adjustBrowserPosition(config: Config, driver: WebDriver) {
        config.browserPosition()?.let {
            log.info("Set browser position to {}", it)
            val coordinates = it.split("x").toTypedArray()
            val x = coordinates[0].toInt()
            val y = coordinates[1].toInt()
            val target = Point(x, y)
            val current = driver.manage().window().position
            if (current != target) {
              driver.manage().window().position = target
            }
        }
    }

    fun adjustBrowserSize(config: Config, driver: WebDriver) {
      val configBrowserSize = config.browserSize()
      if (configBrowserSize != null && !config.startMaximized()) {
            log.info("Set browser size to {}", configBrowserSize)
            val dimension = configBrowserSize.split("x").toTypedArray()
            val width = dimension[0].toInt()
            val height = dimension[1].toInt()
            driver.manage().window().size = Dimension(width, height)
        } else if (config.startMaximized()) {
            try {
                driver.manage().window().maximize()
            } catch (cannotMaximize: Exception) {
                log.warn("Cannot maximize {}: {}", driver.javaClass.simpleName, cannotMaximize)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BrowserResizer::class.java)
    }
}
