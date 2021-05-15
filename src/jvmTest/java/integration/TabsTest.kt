package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.messageContains
import assertk.assertions.startsWith
import com.codeborne.selenide.Condition.Companion.or
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.Selectors.byText
import com.codeborne.selenide.ex.WindowNotFoundException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.util.Arrays
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class TabsTest : ITest() {
    @BeforeEach
    fun setUp() = runBlockingTest {
        setTimeout(1000)
        openFile("page_with_tabs.html")
    }

    @Test
    fun userCanBrowseTabs_webdriver_api() = runBlockingTest {
        val driver = driver().webDriver
        `$`(byText("Page1: uploads")).click()
        driver().Wait().until(ExpectedConditions.numberOfWindowsToBe(2))
        `$`("h1").shouldHave(text("Tabs"))
        val windowHandle = driver.windowHandle
        driver.switchTo().window(nextWindowHandle(driver))
        `$`("h1").shouldHave(text("Path uploads"))
        driver.switchTo().window(windowHandle)
        `$`("h1").shouldHave(text("Tabs"))
    }

    private fun nextWindowHandle(driver: WebDriver): String {
        val windowHandle = driver.windowHandle
        val windowHandles = driver.windowHandles
        windowHandles.remove(windowHandle)
        return windowHandles.iterator().next()
    }

    @Test
    fun canSwitchToWindowByTitle() {
        `$`(byText("Page2: alerts")).click()
        `$`(byText("Page1: uploads")).click()
        `$`(byText("Page3: jquery")).click()
        `$`("h1").shouldHave(text("Tabs"))
        switchTo().window("Test::alerts")
        `$`("h1").shouldHave(text("Page with alerts"))
        switchTo().window("Test::jquery")
        `$`("h1").shouldHave(text("Page with JQuery"))
        switchTo().window("Test::uploads")
        `$`("h1").shouldHave(text("Path uploads"))
        switchTo().window("Test::tabs")
        `$`("h1").shouldHave(text("Tabs"))
    }

    @Test
    fun canSwitchToWindowByIndex_chrome() = runBlockingTest {
        Assumptions.assumeTrue(browser().isChrome)
        `$`(byText("Page2: alerts")).click()
        `$`(byText("Page1: uploads")).click()
        `$`(byText("Page3: jquery")).click()
        `$`("h1").shouldHave(text("Tabs"))
        switchTo().window(1)
        `$`("h1").shouldHave(text("Page with JQuery"))
        switchTo().window(2)
        `$`("h1").shouldHave(text("Path uploads"))
        switchTo().window(3)
        `$`("h1").shouldHave(text("Page with alerts"))
        switchTo().window(0)
        `$`("h1").shouldHave(text("Tabs"))
    }

    @Test
    fun canSwitchToWindowByIndex_other_browsers_but_chrome() = runBlockingTest {
        Assumptions.assumeFalse(browser().isChrome)
        `$`(byText("Page2: alerts")).click()
        `$`(byText("Page1: uploads")).click()
        `$`(byText("Page3: jquery")).click()
        `$`("h1").shouldHave(text("Tabs"))
        val oneOfTitles =
            or("one of titles", text("Tabs"), text("Page with alerts"), text("Path uploads"), text("Page with JQuery"))
        switchTo().window(1)
        `$`("h1").shouldHave(oneOfTitles)
        val title1: String = `$`("h1").text
        switchTo().window(2)
        `$`("h1").shouldHave(oneOfTitles)
        val title2: String = `$`("h1").text
        switchTo().window(3)
        `$`("h1").shouldHave(oneOfTitles)
        val title3: String = `$`("h1").text
        switchTo().window(0)
        `$`("h1").shouldHave(oneOfTitles)
        val title0: String = `$`("h1").text
        Assertions.assertThat(Arrays.asList(title0, title1, title2, title3))
            .containsExactlyInAnyOrder("Tabs", "Page with alerts", "Path uploads", "Page with JQuery")
    }

    @Test
    fun canSwitchBetweenWindowsWithSameTitles() = runBlockingTest {
        `$`(byText("Page4: same title")).click()
        `$`("h1").shouldHave(text("Tabs"))
        switchTo().window("Test::tabs::title")
        `$`("body").shouldHave(
            or(
                "one of tabs with this title",
                text("Secret phrase 1"),
                text("Secret phrase 2"),
                text("Secret phrase 3")
            )
        )
        switchTo().window(0)
        `$`("h1").shouldHave(text("Tabs"))
        `$`(byText("Page5: same title")).click()
        switchTo().window("Test::tabs::title")
        `$`("body").shouldHave(
            or(
                "one of tabs with this title",
                text("Secret phrase 1"),
                text("Secret phrase 2"),
                text("Secret phrase 3")
            )
        )
        switchTo().window(0)
        `$`("h1").shouldHave(text("Tabs"))
    }

    @Test
    fun throwsNoSuchWindowExceptionWhenSwitchingToAbsentWindowByTitle() {
        Assertions.assertThat(driver().title())
            .isEqualTo("Test::tabs")
        assertThat { switchTo().window("absentWindow") }.isFailure().all {
            isInstanceOf(WindowNotFoundException::class.java)
            message().isNotNull().startsWith("No window found with name or handle or title: absentWindow")
            messageContains("Screenshot: file:")
            messageContains("Page source: file:")
            messageContains("Caused by: TimeoutException:")
        }
    }

    @Test
    fun throwsNoSuchWindowExceptionWhenSwitchingToAbsentWindowByIndex() = runBlockingTest {
        Assertions.assertThat(driver().title())
            .isEqualTo("Test::tabs")
        assertThat { switchTo().window(Int.MAX_VALUE) }.isFailure().all {
            isInstanceOf(WindowNotFoundException::class.java)
            message().isNotNull().startsWith("No window found with index: " + Int.MAX_VALUE)
            messageContains("Screenshot: file:")
            messageContains("Page source: file:")
            messageContains("Caused by: TimeoutException:")
        }
    }

    @AfterEach
    fun tearDown() {
        driver().close()
    }
}
