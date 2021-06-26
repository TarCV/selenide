package integration

import com.codeborne.selenide.CollectionCondition.Companion.size
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.attribute
import com.codeborne.selenide.Condition.Companion.cssClass
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.Selectors.byText
import com.codeborne.selenide.Selectors.withText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import kotlin.time.ExperimentalTime

@ExperimentalFileSystem
@ExperimentalTime
@ExperimentalCoroutinesApi
internal class ByTextTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun userCanFindElementByText() = runBlockingTest {
        `$`(byText("Page with selects")).shouldHave(text("Page with selects"))
        `$`(byText("Dropdown list")).shouldHave(text("Dropdown list"))
        `$`(byText("@livemail.ru")).shouldHave(text("@livemail.ru"))
    }

    @Test
    fun spacesInTextAreIgnored() = runBlockingTest {
        `$`(byText("L'a Baskerville")).shouldHave(text("L'a Baskerville"))
        `$`(withText("L'a Baskerville")).shouldHave(text("L'a Baskerville"))
    }

    @Test
    fun nonBreakableSpacesInTextAreIgnored() = runBlockingTest {
        `$`("#hello-world").shouldHave(text("Hello world"))
        `$`(byText("Hello world")).shouldHave(attribute("id", "hello-world"))
        `$`(withText("Hello world")).shouldHave(text("Hello world"))
        `$`(withText("Hello ")).shouldHave(text("Hello world"))
        `$`(withText(" world")).shouldHave(text("Hello world"))
    }

    @Test
    fun canFindElementByTextInsideParentElement() = runBlockingTest {
        Assertions.assertThat(`$`("#multirowTable").findAll(byText("Chack")).getSize())
            .isEqualTo(2)
        Assertions.assertThat(`$`("#multirowTable tr").findAll(byText("Chack")).getSize())
            .isEqualTo(1)
        Assertions.assertThat(`$`("#multirowTable tr").find(byText("Chack"))!!.getAttribute("class"))
            .isEqualTo("first_row")
    }

    @Test
    fun canFindElementContainingText() = runBlockingTest {
        `$`(withText("age with s")).shouldHave(text("Page with selects"))
        `$`(withText("Dropdown")).shouldHave(text("Dropdown list"))
        `$`(withText("@livemail.r")).shouldHave(text("@livemail.ru"))
    }

    @Test
    fun canFindElementContainingTextInsideParentElement() = runBlockingTest {
        `$`("#multirowTable").findAll(withText("Cha")).shouldHave(size(2))
        `$`("#multirowTable tr").findAll(withText("ack")).shouldHave(size(1))
        `$`("#multirowTable tr", 1).find(withText("hac"))!!.shouldHave(cssClass("second_row"))
    }

    @Test
    fun canFindElementsByI18nText() = runBlockingTest {
        `$`(byText("Маргарита")).shouldHave(text("Маргарита"))
        `$`(withText("Марг")).shouldHave(text("Маргарита"))
        `$`(byText("Кот \"Бегемот\"")).click()
    }

    @Test
    fun quotesInText() = runBlockingTest {
        `$`(byText("Arnold \"Schwarzenegger\"")).shouldBe(Condition.visible)
        `$`("#hero").find(byText("Arnold \"Schwarzenegger\""))!!.shouldBe(Condition.visible)
        `$`("#apostrophes-and-quotes").find(By.linkText("Options with 'apostrophes' and \"quotes\""))!!
            .click()
    }
}
