package integration

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.Condition.Companion.value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class DoubleClickTest : ITest() {
    @BeforeEach
    @Throws(InterruptedException::class)
    fun hackForFlakyTestInChrome() {
        if (driver().browser().isChrome) {
            Thread.sleep(500)
        }
    }

    @Test
    fun userCanDoubleClickOnElement() = runBlockingTest {
        openFile("page_with_double_clickable_button.html")
        withLongTimeout {
            `$`("#double-clickable-button")
                .shouldHave(value("double click me"))
                .shouldBe(Condition.enabled)
            `$`("#double-clickable-button")
                .doubleClick()
                .shouldHave(value("do not click me anymore"))
                .shouldBe(Condition.disabled)
            `$`("h2").shouldHave(text("Double click worked"))
        }
    }
}
