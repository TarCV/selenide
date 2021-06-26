package integration

import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.Condition
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalFileSystem
internal class ElementEnabledTest : ITest() {
    @BeforeEach
    fun setUp() = runBlockingTest {
        openFile("page_with_disabled_elements.html")
    }

    @Test
    fun canCheckIfElementIsEnabled() = runBlockingTest {
        `$`("#login-button").shouldNotBe(Condition.enabled)
        `$`("#login-button").shouldBe(Condition.disabled)
        `$`("#username").shouldBe(Condition.enabled)
        `$`("#username").shouldNotBe(Condition.disabled)
    }

    @Test
    fun unexistingElementIsNotEnabled() = runBlockingTest {
        assertThat { `$`("#unexisting-element").shouldBe(Condition.enabled) }.isFailure()
            .isInstanceOf(ElementNotFound::class.java)
    }

    @Test
    fun hiddenElementIsEnabled() = runBlockingTest {
        `$`("#captcha").shouldBe(Condition.enabled)
        `$`("#captcha").shouldNotBe(Condition.disabled)
    }
}
