package integration

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.Selectors.byText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class FindInsideParentTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("long_ajax_request.html")
        setTimeout(4000)
    }

    @Test
    fun findWaitsForParentAndChildElements() {
        `$`(byText("Result 1")).find("#result-1").shouldNotBe(Condition.visible)
        `$`("#results li", 1).find("#result-2").shouldNotBe(Condition.visible)
        `$`(byText("Run long request")).click()
        `$`(byText("Result 1")).shouldBe(Condition.visible)
        `$`(byText("Result 1")).find("#result-1").shouldHave(text("r1"))
        `$`("#results li", 1).shouldBe(Condition.visible)
        `$`("#results li", 1).find("#result-2").shouldHave(text("r2"))
    }

    @Test
    fun findWaitsForParentAndChildElementsMeetsCondition() {
        `$`("#unexisting-parent").shouldNotBe(Condition.visible)
        `$`("#unexisting-parent").find("#unexisting-child").shouldNotBe(Condition.visible)
    }
}
