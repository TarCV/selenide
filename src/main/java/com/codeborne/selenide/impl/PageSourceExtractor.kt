package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import org.openqa.selenium.WebDriver
import java.io.File
import javax.annotation.CheckReturnValue

interface PageSourceExtractor {
    @CheckReturnValue
    fun extract(config: Config, driver: WebDriver, fileName: String): File
}
