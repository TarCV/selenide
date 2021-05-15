package integration

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selectors.byText
import com.codeborne.selenide.SelenideConfig
import com.codeborne.selenide.SelenideDriver
import com.codeborne.selenide.SelenideElement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.support.FindBy
import java.io.FileNotFoundException

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class SelenideDriverITest : ITest() {
    private lateinit var browser1: SelenideDriver
    private lateinit var browser2: SelenideDriver
    @BeforeEach
    fun setUp() {
        driver().close()
        browser1 = SelenideDriver(SelenideConfig().browser(browser).baseUrl(getBaseUrl()))
        browser2 = SelenideDriver(SelenideConfig().browser(browser).baseUrl(getBaseUrl()))
    }

    @AfterEach
    fun tearDown() {
        browser1.close()
        browser2.close()
    }

    @Test
    fun canUseTwoBrowsersInSameThread() = runBlockingTest {
        browser1.open("/page_with_images.html" + browser1.config().browser())
        browser2.open("/page_with_selects_without_jquery.html" + browser2.config().browser())
        browser1.find("#valid-image img").shouldBe(Condition.visible)
        browser2.find("#password").shouldBe(Condition.visible)
        Assertions.assertThat(browser1.title()).isEqualTo("Test::images")
        Assertions.assertThat(browser2.title()).isEqualTo("Test page :: with selects, but without JQuery")
    }

/*    @Test
    @Throws(FileNotFoundException::class)
    fun canDownloadFilesInDifferentBrowsersViaDifferentProxies() = runBlockingTest {
        browser1!!.open("/page_with_uploads.html" + browser1!!.config().browser())
        browser2!!.open("/page_with_uploads.html" + browser2!!.config().browser())
        val file1: Path = browser1!!.`$`(byText("Download me")).download()
        val file2: Path = browser2!!.`$`(byText("Download file with cyrillic name")).download()
        assertThat(file1.getName()).isEqualTo("hello_world.txt")
        assertThat(file2.getName()).isEqualTo("файл-с-русским-названием.txt")
    }

    @Test
    fun canCreatePageObjects() = runBlockingTest {
        val page1: Page1 = browser1.open("/page_with_images.html" + browser1!!.config().browser(), Page1::class.java)
        val page2: Page2 =
            browser2.open("/page_with_selects_without_jquery.html" + browser2!!.config().browser(), Page2::class.java)
        page1.img!!.shouldBe(Condition.visible)
        page2.password!!.shouldBe(Condition.visible)
    }*/

    private class Page1 {
        @FindBy(css = "#valid-image img")
        var img: SelenideElement? = null
    }

    private class Page2 {
        @FindBy(id = "password")
        var password: SelenideElement? = null
    }
}
