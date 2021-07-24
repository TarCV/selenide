package integration

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.Condition.Companion.cssClass
import com.codeborne.selenide.Condition.Companion.value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class ReplacingElementTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("page_with_replacing_elements.html")
    }

    @Test
    fun shouldWaitsUntilElementIsReplaced() = runBlockingTest {
        withLongTimeout {
            `$`("#dynamic-element").shouldHave(value("I will be replaced soon"))
            driver().executeJavaScript<Any>("replaceElement()")
            `$`("#dynamic-element").shouldHave(value("Hello, I am back"), cssClass("reloaded"))
            `$`("#dynamic-element").setValue("New value")
        }
    }

    @Test
    fun getInnerText() = runBlockingTest {
        Assertions.assertThat(`$`("#dynamic-element").innerText())
            .isEmpty()
    }

    @Test
    fun getInnerHtml() = runBlockingTest {
        Assertions.assertThat(`$`("#dynamic-element").innerHtml())
            .isEmpty()
    }

    @Test
    fun findAll() = runBlockingTest {
        `$`("#dynamic-element").findAll(".child").shouldBe(CollectionCondition.empty)
    }

    @Test
    fun testToString() = runBlockingTest {
        Assertions.assertThat(`$`("#dynamic-element").describe())
            .isEqualTo("<input id=\"dynamic-element\" type=\"text\" value=\"I will be replaced soon\"></input>")
    }

    @Test
    @Disabled
    fun tryToCatchStaleElementException() = runBlockingTest {
        driver().executeJavaScript<Any>("startRegularReplacement()")
        for (i in 0..9) {
            `$`("#dynamic-element").shouldHave(value("I am back"), cssClass("reloaded")).setValue("New value from test")
        }
    }
}
