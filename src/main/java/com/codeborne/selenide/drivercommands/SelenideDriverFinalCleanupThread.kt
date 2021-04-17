package com.codeborne.selenide.drivercommands

import com.codeborne.selenide.Config
import com.codeborne.selenide.proxy.SelenideProxyServer
import org.openqa.selenium.WebDriver
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class SelenideDriverFinalCleanupThread @JvmOverloads internal constructor(
    private val config: Config, private val driver: WebDriver, proxy: SelenideProxyServer?,
    closeDriverCommand: CloseDriverCommand = CloseDriverCommand()
) : Runnable {
    private val proxy: SelenideProxyServer?
    private val closeDriverCommand: CloseDriverCommand
    override fun run() {
        closeDriverCommand.close(config, driver, proxy)
    }

    init {
        this.proxy = proxy
        this.closeDriverCommand = closeDriverCommand
    }
}
