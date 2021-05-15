package integration

import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

@ExperimentalFileSystem
internal class UserAgentTest : ITest() {
    @Test
    fun currentUserAgentTest() = runBlockingTest {
        driver().open("/start_page.html")
        val userAgent = driver().getUserAgent()
        Assertions.assertThat(userAgent).isNotBlank
        Assertions.assertThat(userAgent)
            .withFailMessage(String.format("Current user agent [%s] should belong to '%s' browser", userAgent, browser))
            .containsIgnoringCase(browser.substring(0, browser.length - 1))
    }
}
