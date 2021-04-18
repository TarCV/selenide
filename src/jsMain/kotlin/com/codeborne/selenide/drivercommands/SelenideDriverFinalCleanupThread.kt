package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.Config
import org.openqa.selenium.WebDriver

class SelenideDriverFinalCleanupThread internal constructor(
    private val config: Config, private val driver: WebDriver, proxy: /*SelenideProxyServer*/Nothing?,
    closeDriverCommand: CloseDriverCommand = CloseDriverCommand()
) {
    private val proxy: /*SelenideProxyServer*/Nothing?
    private val closeDriverCommand: CloseDriverCommand
    operator fun invoke() {
        closeDriverCommand.close(config, driver, proxy)
    }

    init {
        this.proxy = proxy
        this.closeDriverCommand = closeDriverCommand
    }
}
