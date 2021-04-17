package com.codeborne.selenide.webdriver

import com.codeborne.selenide.Config
import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory

internal class BrowserResizer {
    fun adjustBrowserPosition(config: Config, driver: WebDriver) {
        if (config.browserPosition() != null) {
            log.info("Set browser position to {}", config.browserPosition())
            val coordinates = config.browserPosition()!!.split("x").toTypedArray()
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
        if (config.browserSize() != null && !config.startMaximized()) {
            log.info("Set browser size to {}", config.browserSize())
            val dimension = config.browserSize()!!.split("x").toTypedArray()
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
