package integration

import com.codeborne.selenide.Condition
import com.codeborne.selenide.SelenideConfig
import com.codeborne.selenide.logevents.LogEvent
import com.codeborne.selenide.logevents.LogEventListener
import com.codeborne.selenide.logevents.SelenideLogger.addListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class LogEventListenerTest : BaseIntegrationTest() {
    private val driver = ITest.SelenideDriver(SelenideConfig().baseUrl(getBaseUrl()))
    private val beforeEvents: MutableList<String> = ArrayList()
    private val afterEvents: MutableList<String> = ArrayList()
    @AfterEach
    fun tearDown() {
        driver.close()
    }

    @Test
    fun canCatchBeforeAndAfterEvents() = runBlockingTest {
        addListener("log events collector", SelenideListener())
        driver.open("/elements_disappear_on_click.html")
        val removeMeButton = driver.`$`("#remove").shouldBe(Condition.visible)
        removeMeButton.click()
        removeMeButton.shouldNotBe(Condition.visible)
        val sa = SoftAssertions()
        sa.assertThat(beforeEvents).hasSize(1)
        sa.assertThat(beforeEvents[0]).isEqualTo("before: $(#remove) click()")
        sa.assertThat(afterEvents).hasSize(4)
        sa.assertThat(afterEvents[0]).startsWith("after: $(open)")
        sa.assertThat(afterEvents[1]).isEqualTo("after: $(#remove) should be(visible)")
        sa.assertThat(afterEvents[2]).isEqualTo("after: $(#remove) click()")
        sa.assertThat(afterEvents[3]).isEqualTo("after: $(#remove) should not be(visible)")
        sa.assertAll()
    }

    private inner class SelenideListener : LogEventListener {
        override fun afterEvent(logEvent: LogEvent) {
            afterEvents.add(String.format("after: $(%s) %s", logEvent.element, logEvent.subject))
        }

        override fun beforeEvent(logEvent: LogEvent) {
            if (logEvent.subject!!.contains("click()")) {
                beforeEvents.add(String.format("before: $(%s) %s", logEvent.element, logEvent.subject))
            }
        }
    }
}
