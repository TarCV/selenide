package integration

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.attribute
import com.codeborne.selenide.Condition.Companion.cssClass
import com.codeborne.selenide.Condition.Companion.text
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class InvisibleElementTest : ITest() {
    @BeforeEach
    fun clickHidesElement() = runBlockingTest {
        openFile("elements_disappear_on_click.html")
        `$`("#hide").click()
        `$`("#hide").waitUntil(Condition.hidden, 2000)
    }

    @Test
    fun shouldBeHidden() {
        `$`("#hide").shouldBe(Condition.hidden)
    }

    @Test
    fun shouldNotBeVisible() {
        `$`("#hide").shouldNotBe(Condition.visible)
    }

    @Test
    fun shouldNotHaveTextHide() {
        `$`("#hide").shouldNotHave(text("Hide me").because("Text should disappear"))
    }

    @Test
    fun shouldHaveAttribute() {
        `$`("#hide").shouldHave(attribute("id", "hide").because("Attributes don't disappear"))
    }

    @Test
    fun shouldHaveCssClasses() {
        `$`("#hide").shouldHave(cssClass("someclass").because("Attributes don't disappear"))
    }

    @Test
    fun shouldNotHaveTextRemove() {
        `$`("#hide").shouldNotHave(text("Remove me").because("Text never existed."))
    }
}
