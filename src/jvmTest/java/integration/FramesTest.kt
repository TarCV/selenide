package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.messageContains
import assertk.assertions.startsWith
import com.codeborne.selenide.Condition.Companion.name
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.ex.FrameNotFoundException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class FramesTest : ITest() {
    @BeforeEach
    fun openPage() = runBlockingTest {
        openFile("page_with_frames.html")
    }

    @Test
    fun canSwitchIntoInnerFrame() = runBlockingTest {
        Assertions.assertThat(driver().title()).isEqualTo("Test::frames")
        switchTo().innerFrame("parentFrame")
        `$`("frame").shouldHave(name("childFrame_1"))
        Assertions.assertThat(driver().getCurrentFrameUrl()).isEqualTo(getBaseUrl() + "/page_with_parent_frame.html")
        switchTo().innerFrame("parentFrame", "childFrame_1")
        Assertions.assertThat(driver().source()).contains("Hello, WinRar!")
        Assertions.assertThat(driver().getCurrentFrameUrl()).isEqualTo(getBaseUrl() + "/hello_world.txt")
        switchTo().innerFrame("parentFrame", "childFrame_2")
        `$`("frame").shouldHave(name("childFrame_2_1"))
        Assertions.assertThat(driver().getCurrentFrameUrl()).isEqualTo(getBaseUrl() + "/page_with_child_frame.html")
        switchTo().innerFrame("parentFrame", "childFrame_2", "childFrame_2_1")
        Assertions.assertThat(driver().source()).contains("This is last frame!")
        Assertions.assertThat(driver().getCurrentFrameUrl()).isEqualTo(getBaseUrl() + "/child_frame.txt")
        switchTo().innerFrame("parentFrame")
        `$`("frame").shouldHave(name("childFrame_1"))
        Assertions.assertThat(driver().getCurrentFrameUrl()).isEqualTo(getBaseUrl() + "/page_with_parent_frame.html")
    }

    @Test
    fun switchToInnerFrame_withoutParameters_switchesToDefaultContent() = runBlockingTest {
        switchTo().innerFrame("parentFrame")
        `$`("frame").shouldHave(name("childFrame_1"))
        switchTo().innerFrame()
        `$`("frame").shouldHave(name("topFrame"))
    }

    @Test
    fun canSwitchBetweenFramesByTitle() {
        Assertions.assertThat(driver().title()).isEqualTo("Test::frames")
        switchTo().frame("topFrame")
        Assertions.assertThat(driver().source()).contains("Hello, WinRar!")
        switchTo().defaultContent()
        switchTo().frame("leftFrame")
        `$`("h1").shouldHave(text("Page with dynamic select"))
        switchTo().defaultContent()
        switchTo().frame("mainFrame")
        `$`("h1").shouldHave(text("Page with JQuery"))
    }

    @Test
    fun canSwitchBetweenFramesByIndex() {
        Assumptions.assumeFalse(browser().isChrome)
        Assertions.assertThat(driver().title()).isEqualTo("Test::frames")
        switchTo().frame(0)
        Assertions.assertThat(driver().source()).contains("Hello, WinRar!")
        switchTo().defaultContent()
        switchTo().frame(1)
        `$`("h1").shouldHave(text("Page with dynamic select"))
        switchTo().defaultContent()
        switchTo().frame(2)
        `$`("h1").shouldHave(text("Page with JQuery"))
    }

    @Test
    fun throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByElement() {
        Assertions.assertThat(driver().title()).isEqualTo("Test::frames")
        assertThat {
            switchTo().frame("mainFrame")
            // $("#log") is present, but not frame.
            switchTo().frame(`$`("#log"))
        }.isFailure().all {
            isInstanceOf(FrameNotFoundException::class.java)
            message().isNotNull().startsWith("No frame found with element: <div id=\"log\" displayed:false></div>")
        }
    }

    @Test
    fun throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByTitle() {
        Assertions.assertThat(driver().title()).isEqualTo("Test::frames")
        assertThat { switchTo().frame("absentFrame") }.isFailure()
            .isInstanceOf(FrameNotFoundException::class.java)
            .message().isNotNull().startsWith("No frame found with id/name: absentFrame")
    }

    @Test
    fun throwsNoSuchFrameExceptionWhenSwitchingToAbsentFrameByIndex() {
        Assertions.assertThat(driver().title()).isEqualTo("Test::frames")
        assertThat { switchTo().frame(Int.MAX_VALUE) }.isFailure()
            .isInstanceOf(FrameNotFoundException::class.java)
            .message().isNotNull().startsWith("No frame found with index: " + Int.MAX_VALUE)
    }

    @Test
    fun attachesScreenshotWhenCannotFrameNotFound() {
        assertThat { switchTo().frame(33) }.isFailure().all {
            isInstanceOf(FrameNotFoundException::class.java)
            message().isNotNull().startsWith("No frame found with index: 33")
            messageContains("Screenshot: file:")
            messageContains("Page source: file:")
            messageContains("Caused by: TimeoutException:")
        }
    }
}
