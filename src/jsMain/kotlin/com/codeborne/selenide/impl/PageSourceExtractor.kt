package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import org.openqa.selenium.WebDriver
import java.io.Path

interface PageSourceExtractor {
    suspend fun extract(config: Config, driver: WebDriver, fileName: String): Path
}
