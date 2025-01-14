package com.codeborne.selenide.impl

import org.lighthousegames.logging.logging
import com.codeborne.selenide.DownloadsFolder
import org.openqa.selenium.WebDriver
import org.slf4j.LoggerFactory

@okio.ExperimentalFileSystem
internal class UnusedWebdriversCleanupThread(
    private val allWebDriverThreads: MutableCollection<Thread>,
    private val threadWebDriver: MutableMap<Long, WebDriver>,
    private val threadProxyServer: MutableMap<Long, Any>,
    private val threadDownloadsFolder: MutableMap<Long, DownloadsFolder>
) : Thread() {
    override fun run() {
        while (true) {
            closeUnusedWebdrivers()
            try {
                sleep(100)
            } catch (e: InterruptedException) {
                currentThread().interrupt()
                break
            }
        }
    }

    private fun closeUnusedWebdrivers() {
        for (thread in allWebDriverThreads) {
            if (!thread.isAlive) {
                log.info {"Thread ${thread.id} is dead. Let's close webdriver ${threadWebDriver[thread.id]}" }
                closeWebDriver(thread)
            }
        }
    }

    private fun closeWebDriver(thread: Thread) {
        allWebDriverThreads.remove(thread)
        val driver = threadWebDriver.remove(thread.id)
        if (driver == null) {
            log.info { "No webdriver found for thread: ${thread.id} - nothing to close" }
        } else {
            driver.quit()
        }
/*
TODO:        val proxy = threadProxyServer.remove(thread.id)
        proxy?.shutdown()
*/
        threadDownloadsFolder.remove(thread.id)
    }

    companion object {
        private val log = logging(UnusedWebdriversCleanupThread::class.simpleName)
    }

    init {
        isDaemon = true
        name = "Webdrivers killer thread"
    }
}
