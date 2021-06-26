package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.cause
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.messageContains
import com.codeborne.selenide.Condition.Companion.exactText
import com.codeborne.selenide.Condition.Companion.exactValue
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.Selectors.shadowCss
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.NoSuchElementException
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalFileSystem
internal class ShadowElementTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("page_with_shadow_dom.html")
    }

    @Test
    fun sendKeysInsideShadowHost() = runBlockingTest {
        Assumptions.assumeThat(false)
            .`as`("Firefox doesn't support sendKeys() inside shadow dom, see https://bugzilla.mozilla.org/show_bug.cgi?id=1503860")
            .isFalse
        val input = `$`(shadowCss("#inputInShadow", "#shadow-host"))
        input.sendKeys("I can type text inside of shadow dom")
        input.shouldHave(exactValue("I can type text inside of shadow dom"))
    }

    @Test
    fun setValueInsideShadowHost() = runBlockingTest {
        val input = `$`(shadowCss("#inputInShadow", "#shadow-host"))
        withFastSetValue {
            input.setValue("I can type text inside of shadow dom")
            input.shouldHave(exactValue("I can type text inside of shadow dom"))
        }
    }

    @Test
    fun sendKeysInsideInnerShadowHost() = runBlockingTest {
        Assumptions.assumeThat(false)
            .`as`("Firefox doesn't support sendKeys() inside shadow dom, see https://bugzilla.mozilla.org/show_bug.cgi?id=1503860")
            .isFalse
        val input = `$`(shadowCss("#inputInInnerShadow", "#shadow-host", "#inner-shadow-host"))
        input.sendKeys("I can type text inside of shadow dom")
        input.shouldHave(exactValue("I can type text inside of shadow dom"))
    }

    @Test
    fun setValueInsideInnerShadowHost() = runBlockingTest {
        val input = `$`(shadowCss("#inputInInnerShadow", "#shadow-host", "#inner-shadow-host"))
        withFastSetValue {
            input.setValue("I can type text inside of shadow dom")
            input.shouldHave(exactValue("I can type text inside of shadow dom"))
        }
    }

    @Test
    fun clickInsideShadowHost() = runBlockingTest {
        val button = `$`(shadowCss("#buttonInShadow", "#shadow-host"))
        button.shouldHave(exactText("Button 1"))
        button.click()
        button.shouldHave(exactText("Changed Button 1"))
    }

    @Test
    fun clickInsideInnerShadowHost() = runBlockingTest {
        val button = `$`(shadowCss("#buttonInInnerShadow", "#shadow-host", "#inner-shadow-host"))
        button.shouldHave(exactText("Button 2"))
        button.click()
        button.shouldHave(exactText("Changed Button 2"))
    }

    @Test
    fun clickInsideShadowHostInsideOfElement() = runBlockingTest {
        `$`("#shadow-host")
            .find(shadowCss("p", "#inner-shadow-host"))
            .shouldHave(text("The Shadow-DOM inside another shadow tree"))
    }

    @Test
    fun getTargetElementViaShadowHost() = runBlockingTest {
        `$`(shadowCss("p", "#shadow-host"))
            .shouldHave(text("Inside Shadow-DOM"))
    }

    @Test
    fun getElementInsideInnerShadowHost() = runBlockingTest {
        `$`(shadowCss("p", "#shadow-host", "#inner-shadow-host"))
            .shouldHave(text("The Shadow-DOM inside another shadow tree"))
    }

    @Test
    fun throwErrorWhenGetNonExistingTargetInsideShadowRoot() = runBlockingTest {
        assertThat { `$`(shadowCss("#nonexistent", "#shadow-host")).text }.isFailure()
            .isInstanceOf(ElementNotFound::class.java)
    }

    @Test
    fun throwErrorWhenBypassingShadowHost() = runBlockingTest {
        assertThat { `$`("p").text }.isFailure()
            .isInstanceOf(ElementNotFound::class.java)
    }

    @Test
    fun throwErrorWhenShadowHostDoesNotHaveShadowRoot() = runBlockingTest {
        assertThat { `$`(shadowCss("p", "h1")).text }.isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                cause().isNotNull().isInstanceOf(NoSuchElementException::class.java)
                messageContains("The element is not a shadow host or has 'closed' shadow-dom mode:")
            }
    }

    @Test
    fun throwErrorWhenInnerShadowHostAbsent() = runBlockingTest {
        assertThat { `$`(shadowCss("p", "#shadow-host", "#nonexistent")).text }.isFailure().all {
            isInstanceOf(ElementNotFound::class.java)
            messageContains("Element not found {#shadow-host -> #nonexistent -> p}")
            cause().isNotNull().all {
                isInstanceOf(NoSuchElementException::class.java)
                messageContains("Cannot locate an element p in shadow roots #shadow-host -> #nonexistent")
            }
        }
    }

    @Test
    fun getTargetElementsViaShadowHost() = runBlockingTest {
        `$$`(shadowCss("div.test-class", "#shadow-host"))
            .shouldHaveSize(2)
    }

    @Test
    fun getElementsInsideInnerShadowHost() = runBlockingTest {
        `$$`(shadowCss("p", "#shadow-host", "#inner-shadow-host"))
            .shouldHaveSize(1)
    }

    @Test
    fun getNonExistingTargetElementsInsideShadowHost() = runBlockingTest {
        `$$`(shadowCss("#nonexistent", "#shadow-host"))
            .shouldHaveSize(0)
    }

    @Test
    fun getAllElementsInAllNestedShadowHosts() = runBlockingTest {
        val elements = `$$`(
            shadowCss(
                ".shadow-container-child-child-item",
                "#shadow-container", ".shadow-container-child", ".shadow-container-child-child"
            )
        )
        elements.shouldHaveSize(3)
        Assertions.assertThat(elements.get(0).text).isEqualTo("shadowContainerChildChild1Host1")
            .`as`("Mismatch in name of first child container")
        Assertions.assertThat(elements.get(2).text).isEqualTo("shadowContainerChildChild1Host3")
            .`as`("Mismatch in name of last child container")
    }
}
