package integration

import com.codeborne.selenide.Condition.Companion.text
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalFileSystem
internal class TabsWaitTest : ITest() {
    @BeforeEach
    fun setUp() = runBlockingTest {
        openFile("page_with_tabs_with_delays.html")
        setTimeout(2000)
    }

    @Test
    fun waitsUntilTabAppears_byTitle() = runBlockingTest {
        `$`("#open-new-tab-with-delay").click()
        switchTo().window("Test::alerts")
        `$`("h1").shouldHave(text("Page with alerts"))
    }

    @Test
    fun waitsUntilTabAppears_byIndex() = runBlockingTest {
        `$`("#open-new-tab-with-delay").click()
        switchTo().window(1)
        `$`("h1").shouldHave(text("Page with alerts"))
    }
}
