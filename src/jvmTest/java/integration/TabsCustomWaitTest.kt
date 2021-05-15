package integration

import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.ex.WindowNotFoundException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalTime
@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class TabsCustomWaitTest : ITest() {
    @BeforeEach
    fun setUp() = runBlockingTest {
        openFile("page_with_tabs_with_big_delays.html")
    }

    @Test
    fun waitsUntilTabAppears_withCustomTimeout() = runBlockingTest {
        setTimeout(1000)
        `$`("#open-new-tab-with-delay").click()
        switchTo().window("Test::alerts", 3.seconds)
        `$`("h1").shouldHave(text("Page with alerts"))
    }

    @Test
    fun waitsUntilTabAppears_withoutCustomTimeout() = runBlockingTest {
        `$`("#open-new-tab-with-delay").click()
        assertThat { switchTo().window(1) }.isFailure()
            .isInstanceOf(WindowNotFoundException::class.java)
    }

    @Test
    fun waitsUntilTabAppears_withLowerTimeout() = runBlockingTest {
        `$`("#open-new-tab-with-delay").click()
        assertThat { switchTo().window(1, 1.seconds) }.isFailure()
            .isInstanceOf(WindowNotFoundException::class.java)
    }

    @AfterEach
    fun tearDown() {
        driver().close()
    }
}
