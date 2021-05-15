package integration

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.attribute
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.Selectors.byAttribute
import com.codeborne.selenide.Selectors.byId
import com.codeborne.selenide.Selectors.byLinkText
import com.codeborne.selenide.Selectors.byName
import com.codeborne.selenide.Selectors.byPartialLinkText
import com.codeborne.selenide.Selectors.byXpath
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalFileSystem
internal class SelectorsTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun canFindElementByName() {
        `$`(byName("domain")).should(Condition.exist)
    }

    @Test
    fun canFindElementByXPath() {
        `$`(byXpath("//h1")).shouldHave(text("Page with selects"))
        `$`(byXpath("//*[@name='domain']")).shouldBe(Condition.visible)
    }

    @Test
    fun canFindElementByLinkText() {
        `$`(byLinkText("Options with 'apostrophes' and \"quotes\"")).shouldHave(text("Options with 'apostrophes' and \"quotes\""))
    }

    @Test
    fun canFindElementByPartialLinkText() {
        `$`(byPartialLinkText("'apostrophes")).shouldHave(text("Options with 'apostrophes' and \"quotes\""))
        `$`(byPartialLinkText("quotes\"")).shouldHave(text("Options with 'apostrophes' and \"quotes\""))
    }

    @Test
    fun byAttributeEscapesQuotes() {
        `$`(byAttribute("value", "john mc'lain")).shouldHave(attribute("value", "john mc'lain"))
        `$`(byAttribute("value", "arnold \"schwarzenegger\"")).shouldHave(
            attribute(
                "value",
                "arnold \"schwarzenegger\""
            )
        )
        `$`("#denzel-washington").shouldHave(attribute("value", "denzel \\\\\"equalizer\\\\\" washington"))
        `$`(byAttribute("value", "denzel \\\\\"equalizer\\\\\" washington"))
            .shouldHave(attribute("value", "denzel \\\\\"equalizer\\\\\" washington"))
            .shouldHave(text("Denzel Washington"))
    }

    @Test
    fun canFindElementById() {
        `$`(byId("status")).shouldHave(text("Username:"))
    }

    @Test
    fun canFindSelenideElementByXpath() {
        `$x`("//h1").shouldHave(text("Page with selects"))
        `$x`("//*[@id='status']").shouldHave(text("Username:"))
        `$x`("//*[@name='domain']").shouldBe(Condition.visible)
    }

    @Test
    fun canFindElementsCollectionByXpath() {
        `$$x`("//h1").get(0).shouldHave(text("Page with selects"))
        `$$x`("//*[@id='status']").get(0).shouldHave(text("Username:"))
        `$$x`("//*[@name='domain']").get(0).shouldBe(Condition.visible)
    }

    @Test
    fun canFindChildSelenideElementByXpath() {
        val parent = `$x`("//div[@id='radioButtons']")
        parent.`$x`("./h2").shouldHave(text("Radio buttons"))
    }

    @Test
    fun canFindChildElementsCollectionByXpath() = runBlockingTest {
        val parent = `$x`("//table[@id='multirowTable']")
        parent.`$$x`(".//tr").shouldHaveSize(2)
    }

    @Test
    fun canFindNthChildSelenideElementByXpath() {
        val parent = `$x`("//table[@id='multirowTable']")
        parent.`$x`(".//tr", 0).shouldHave(text("Chack Norris"))
    }
}
