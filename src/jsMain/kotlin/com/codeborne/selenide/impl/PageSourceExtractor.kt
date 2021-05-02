package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import okio.ExperimentalFileSystem
import okio.Path
import org.openqa.selenium.WebDriver

interface PageSourceExtractor {
    @ExperimentalFileSystem
    suspend fun extract(config: Config, driver: WebDriver, fileName: String): Path
}
