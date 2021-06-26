package integration

import com.automation.remarks.video.annotations.Video
import com.codeborne.selenide.Condition.Companion.name
import com.codeborne.selenide.Condition.Companion.text
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class FrameWaitTest : ITest() {
    @BeforeEach
    fun setUp() = runBlockingTest {
        openFile("page_with_frames_with_delays.html")
        setTimeout(2000)
    }

    @Test
    @Video
    fun waitsUntilFrameAppears_inner() = runBlockingTest {
        switchTo().innerFrame("parentFrame")
        `$`("frame").shouldHave(name("childFrame_1"))
    }

    @Test
    @Video
    fun waitsUntilFrameAppears_byTitle() = runBlockingTest {
        switchTo().frame("leftFrame")
        `$`("h1").shouldHave(text("Page with dynamic select"))
    }

    @Test
    @Video
    fun waitsUntilFrameAppears_byIndex() = runBlockingTest {
        switchTo().frame(2)
        `$`("h1").shouldHave(text("Page with JQuery"))
        Assertions.assertThat(driver().source()).contains("Test::jquery")
    }
}
