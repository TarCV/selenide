package integration

import com.codeborne.selenide.Driver
import com.codeborne.selenide.commands.Click
import com.codeborne.selenide.commands.Commands
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.WebElement
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.ParametersAreNonnullByDefault

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class OverrideCommandsTest : ITest() {
    private val clickCounter = AtomicInteger()
    @BeforeEach
    fun openTestPageWithImages() = runBlockingTest {
        openFile("page_with_images.html")
    }

    @AfterEach
    fun tearDown() {
        Commands.instance.add("click", Click())
    }

    @Test
    fun userCanOverrideAnyCommand() {
        Commands.instance.add("click", MyClick())
        `$`("#valid-image").click()
        `$`("#invalid-image").click()
        Assertions.assertThat(clickCounter.get()).isEqualTo(2)
    }

    @ParametersAreNonnullByDefault
    private inner class MyClick : Click() {
        override suspend fun click(driver: Driver, element: WebElement) {
            super.click(driver, element)
            clickCounter.incrementAndGet()
        }
    }
}
