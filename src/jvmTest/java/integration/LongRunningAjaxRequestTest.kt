package integration

import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.Selectors.byText
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By

@ExperimentalFileSystem
internal class LongRunningAjaxRequestTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        setTimeout(4000)
        openFile("long_ajax_request.html")
        `$`("#loading").shouldNot(Condition.exist)
        `$`(byText("Run long request")).click()
    }

    @Test
    fun dollarWaitsForElement() = runBlockingTest {
        `$`(byText("Result 1")).shouldBe(Condition.visible)
    }

    @Test
    fun dollarWaitsForElementWithIndex() = runBlockingTest {
        `$`("#results li", 1).shouldHave(text("Result 2"))
    }

    @Test
    fun dollarWaitsUntilElementDisappears() = runBlockingTest {
        `$`(byText("Loading...")).should(Condition.exist)
        `$`(byText("Loading...")).should(Condition.disappear)
        `$`(byText("Loading...")).shouldNot(Condition.exist)
    }

    @Test
    fun userCanWaitUntilConditionIsMet() = runBlockingTest {
        `$`(byText("Result 2")).waitUntil(Condition.visible, 3000)
        Assertions.assertThat(`$`(byText("Result 2")).isDisplayed).isTrue
    }

    @Test
    fun dollarWithParentWaitsUntilElementDisappears() = runBlockingTest {
        `$`("#results").`$`("span#loading").should(Condition.exist)
        `$`("#results").`$`("span#loading").shouldNot(Condition.exist)
    }

    @Test
    fun dollarWithParentAndIndexWaitsUntilElementDisappears() = runBlockingTest {
        `$`("#results").`$`("span#loading", 0).should(Condition.exist)
        `$`("#results").`$`("span#loading", 0).shouldNot(Condition.exist)
        `$`("#results").`$`("span#loading", 666).shouldNot(Condition.exist)
    }

    @Test
    fun waitingTimeout() = runBlockingTest {
        assertThat { `$`("#non-existing-element").should(Condition.exist) }.isFailure()
            .isInstanceOf(AssertionError::class.java)
    }

    @Test
    fun shouldWaitsForCondition() = runBlockingTest {
        `$`("#results").shouldHave(text("Result 1"))
    }

    @Test
    fun shouldWaitsForAllConditions() = runBlockingTest {
        `$`("#results").shouldHave(text("Result 1"), text("Result 2"))
    }

    @Test
    fun shouldNotExist() = runBlockingTest {
        `$`("#non-existing-element").shouldNot(Condition.exist)
        `$`("#non-existing-element", 7).shouldNot(Condition.exist)
        `$`(By.linkText("non-existing-link")).shouldNot(Condition.exist)
        `$`(By.linkText("non-existing-link"), 8).shouldNot(Condition.exist)
    }

    @Test
    fun findWaitsForConditions() = runBlockingTest {
        `$`("#results").find(byText("non-existing element"))!!.shouldNot(Condition.exist)
        `$`("#results").find(byText("non-existing element"), 3).shouldNot(Condition.exist)
        `$`("#results").find(byText("Loading..."))!!.shouldNot(Condition.exist)
        `$`("#results").find(byText("Loading..."), 0).shouldNot(Condition.exist)
    }

    @Test
    fun shouldNotExistWithinParentElement() = runBlockingTest {
        `$`("body").`$`("#non-existing-element").shouldNot(Condition.exist)
        `$`("body").`$`("#non-existing-element", 4).shouldNot(Condition.exist)
    }

    @Test
    fun shouldNotBeVisible() = runBlockingTest {
        `$`("#non-existing-element").shouldNotBe(Condition.visible)
        `$`("#non-existing-element", 7).shouldNotBe(Condition.visible)
        `$`(By.linkText("non-existing-link")).shouldNotBe(Condition.visible)
        `$`(By.linkText("non-existing-link"), 8).shouldNotBe(Condition.visible)
    }

    @Test
    fun shouldNotBeVisibleWithinParentElement() = runBlockingTest {
        `$`("body").`$`("#non-existing-element").shouldNotBe(Condition.visible)
        `$`("body").`$`("#non-existing-element", 4).shouldNotBe(Condition.visible)
    }
}
