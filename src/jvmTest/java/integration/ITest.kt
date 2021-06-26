package integration

import com.codeborne.selenide.DriverStub
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideConfig
import com.codeborne.selenide.SelenideDriver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.SelenideTargetLocator
import com.codeborne.selenide.drivercommands.LazyDriver
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.By

@ExperimentalFileSystem
abstract class ITest : BaseIntegrationTest() {
    protected val longTimeout = System.getProperty("selenide.timeout", "4000").toLong()

    @BeforeEach
    fun resetShortTimeout() {
        getConfig().timeout(1)
    }

    protected fun setTimeout(timeoutMs: Long) {
        getConfig().timeout(timeoutMs)
    }

    protected inline fun withLongTimeout(test: () -> Unit) {
        setTimeout(longTimeout)
        try {
            test()
        } finally {
            resetShortTimeout()
        }
    }

    protected inline fun withFastSetValue(test: () -> Unit) {
        getConfig().fastSetValue(true)
        try {
            test()
        } finally {
            getConfig().fastSetValue(false)
        }
    }

    protected fun driver(): SelenideDriver {
        return driver.get()
    }

    protected fun `$`(locator: String?): SelenideElement {
        return driver().`$`(locator!!)
    }

    protected fun `$`(locator: String?, index: Int): SelenideElement {
        return driver().`$`(locator!!, index)
    }

    protected fun `$`(locator: By?): SelenideElement {
        return driver().`$`(locator!!)
    }

    protected fun `$`(locator: By?, index: Int): SelenideElement {
        return driver().`$`(locator!!, index)
    }

    protected fun `$x`(locator: String?): SelenideElement {
        return driver().`$x`(locator!!)
    }

    protected fun `$$`(locator: String?): ElementsCollection {
        return driver().`$$`(locator!!)
    }

    protected fun `$$`(locator: By?): ElementsCollection {
        return driver().`$$`(locator!!)
    }

    protected fun `$$x`(locator: String?): ElementsCollection {
        return driver().`$$x`(locator!!)
    }

    protected fun switchTo(): SelenideTargetLocator {
        return driver().switchTo()
    }

    protected suspend fun openFile(fileName: String) {
        driver().open(
            "/" + fileName + "?browser=" + browser +
                    "&timeout=" + driver().config().timeout()
        )
    }

    companion object {
        fun SelenideDriver(config: SelenideConfig): SelenideDriver {
            return SelenideDriver(
                config,
                LazyDriver(config, null, emptyList())
            )
        }

        private val config =
            ThreadLocal.withInitial { SelenideConfig().browser(browser).baseUrl(getBaseUrl()).timeout(1) }
        protected fun getConfig(): SelenideConfig = config.get()

        private val driver = ThreadLocal.withInitial { SelenideDriver(getConfig()) }
    }
}
