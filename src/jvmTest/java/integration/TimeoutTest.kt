package integration

import com.codeborne.selenide.Condition
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException

@ExperimentalFileSystem
internal class TimeoutTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun lookingForNonExistingElementShouldFailFast() {
        val start = System.nanoTime()
        try {
            driver().webDriver.findElement(By.id("nonExistingElement"))
            Assertions.fail<Any>("Looking for non-existing element should fail")
        } catch (expectedException: NoSuchElementException) {
            val end = System.nanoTime()
            Assertions.assertThat(end - start < 1200000000L)
                .withFailMessage("Looking for non-existing element took more than 1.2 ms: " + (end - start) / 1000000 + " ms.")
                .isTrue
        }
    }

    @Test
    fun timeoutShouldBetoLongMilliseconds() {
        try {
            `$`(By.xpath("//h16")).waitUntil(Condition.visible, 15)
        } catch (expectedException: ElementNotFound) {
            Assertions.assertThat(expectedException.toString())
                .withFailMessage(
                    String.format(
                        "Error message should contain timeout '15 ms', but received: %s",
                        expectedException.toString()
                    )
                )
                .contains("15 ms")
        }
    }

    @Test
    fun timeoutShouldBeFormattedInErrorMessage() {
        try {
            `$`(By.xpath("//h19")).waitUntil(Condition.visible, 1500)
            Assertions.fail<Any>("Expected ElementNotFound")
        } catch (expectedException: ElementNotFound) {
            Assertions.assertThat(expectedException.toString())
                .withFailMessage(
                    String.format(
                        "Error message should contain timeout '1.500 s', but received: %s",
                        expectedException.toString()
                    )
                )
                .contains("1.500 s")
        }
    }

    @Test
    fun timeoutLessThanSecond() {
        try {
            `$`(By.xpath("//h18")).waitUntil(Condition.visible, 800)
            Assertions.fail<Any>("Expected ElementNotFound")
        } catch (expectedException: ElementNotFound) {
            Assertions.assertThat(expectedException.toString())
                .withFailMessage(
                    String.format(
                        "Error message should contain timeout '800 ms', but received: %s",
                        expectedException.toString()
                    )
                )
                .contains("800 ms")
        }
    }
}
