package integration

import com.codeborne.selenide.Condition.Companion.text
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalFileSystem
internal class HoverTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_jquery.html")
    }

    @Test
    fun canEmulateHover() {
        `$`("#hoverable").hover().shouldHave(text("It's hover"))
        `$`("h1").hover()
        `$`("#hoverable").shouldHave(text("It's not hover"))
    }
}
