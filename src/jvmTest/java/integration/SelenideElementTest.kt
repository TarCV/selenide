package integration

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selectors.byText
import com.codeborne.selenide.Selectors.withText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class SelenideElementTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun selenideElementImplementsWrapsElement() {
        val wrappedElement = `$`("#login").wrappedElement
        Assertions.assertNotNull(wrappedElement)
        Assertions.assertEquals("login", wrappedElement.getAttribute("id"))
    }

    @Test
    fun selenideElementImplementsWrapsWebdriver() {
        val wrappedDriver = `$`("#login").wrappedDriver
        Assertions.assertNotNull(wrappedDriver)
        val currentUrl = wrappedDriver.currentUrl
        Assertions.assertTrue(
            currentUrl.contains("page_with_selects_without_jquery.html"), "Current URL is $currentUrl"
        )
    }

    @Test
    fun  // @Ignore(value = "probably a bug in Selenide")
            selenideElementChainedWithByTextSelector() {
        `$`("#status").`$`(withText("Smith")).shouldBe(Condition.visible)
        `$`("#status").`$`(byText("Bob Smith")).shouldBe(Condition.visible)
    }

    @Test
    @Disabled(value = "It fails, please check if it is right")
    fun selenideElementChainedElementByTextWhenTextIsDirectContentOfTheParent() {
        // e.g. <div id="radioButton><img/>Мастер<div/></div>
        `$`("#radioButtons").`$`(withText("Мастер")).shouldBe(Condition.visible) //Fails
    }
}
