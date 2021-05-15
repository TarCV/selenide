package integration

import com.codeborne.selenide.CollectionCondition.Companion.size
import com.codeborne.selenide.CollectionCondition.Companion.sizeGreaterThanOrEqual
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.attribute
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.SelectorMode
import com.codeborne.selenide.SelenideConfig
import com.codeborne.selenide.SelenideDriver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalFileSystem
@ExperimentalCoroutinesApi
internal class SizzleSelectorsTest : BaseIntegrationTest() {
    var driver = ITest.SelenideDriver(SelenideConfig().baseUrl(getBaseUrl()).selectorMode(SelectorMode.Sizzle))

    @AfterEach
    fun tearDown() = runBlockingTest {
        driver.close()
    }

    @Test
    fun canUseSizzleSelectors() = runBlockingTest {
        driver.open("/page_with_jquery.html")
        driver.`$$`(":input").shouldHave(size(4))
        driver.`$$`(":input:not(.masked)").shouldHave(size(3))
        driver.`$$`(":header").shouldHave(size(3)) // h1, h1, h2
        driver.`$$`(":parent").shouldHave(sizeGreaterThanOrEqual(13)) // all non-leaf elements
        driver.`$$`(":not(:parent)").shouldHave(size(14)) // all leaf elements
        driver.`$`("input:first").shouldHave(attribute("name", "username"))
        driver.`$`("input:nth(1)").shouldHave(attribute("name", "password"))
        driver.`$`("input:last").shouldHave(attribute("id", "some-button"))
        driver.`$`("input[name!='username'][name!='password']").shouldHave(attribute("name", "rememberMe"))
        driver.`$`(":header").shouldHave(text("Page with JQuery"))
        driver.`$`(":header", 1).shouldHave(text("Now typing"))
        driver.`$`("label:contains('assword')").shouldHave(text("Password:"))
        assertThat(driver.`$`(":parent:not('html'):not('head')").tagName).isEqualTo("title")
    }

    @Test
    fun canUseSizzleSelectors_onTodoList_dojo() = runBlockingTest {
        driver.open("https://todomvc.com/examples/dojo/")
        driver.`$$`(":header").shouldHave(size(6))
        driver.`$$`(":input").shouldHave(sizeGreaterThanOrEqual(3))
        driver.`$$`(":input:not(.masked)").shouldHave(sizeGreaterThanOrEqual(3))
        driver.`$$`(":header").shouldHave(sizeGreaterThanOrEqual(6)) // h1, h1, h2
        driver.`$$`(":parent").shouldHave(sizeGreaterThanOrEqual(13)) // all non-leaf elements
        driver.`$$`(":not(:parent)").shouldHave(sizeGreaterThanOrEqual(21)) // all leaf elements
        driver.`$`("input:first").shouldHave(attribute("class", "new-todo"))
        driver.`$`("input:nth(1)").shouldHave(attribute("class", "toggle-all"))
        driver.`$`("input:last").should(Condition.exist)
        driver.`$`("input[name!='username'][name!='password']")
            .should(Condition.exist)
            .shouldNotHave(attribute("name", "username"), attribute("name", "password"))
        driver.`$`(":header").shouldHave(text("Dojo"))
        driver.`$`(":header", 1).shouldHave(text("Example"))
        driver.`$`("label:contains('assword')").shouldNot(Condition.exist)
        assertThat(driver.`$`(":parent:not('html'):not('head')").tagName).isEqualTo("title")
    }

    @Test
    fun canUseSizzleSelectors_onTodoList_jquery() = runBlockingTest {
        driver.open("https://todomvc4tasj.herokuapp.com/")
        driver.`$$`(":header").shouldHave(sizeGreaterThanOrEqual(1))
    }

    @Test
    fun canUseSizzleSelectors_onTodoList_troopjs() = runBlockingTest {
        driver.open("https://todomvc.com/examples/troopjs_require//")
        driver.`$$`(":header").shouldHave(sizeGreaterThanOrEqual(1))
    }
}
