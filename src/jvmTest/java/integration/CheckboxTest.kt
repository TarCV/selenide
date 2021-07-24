package integration

import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.Condition
import com.codeborne.selenide.ex.InvalidStateException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class CheckboxTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun userCanSelectCheckbox() = runBlockingTest {
        `$`(By.name("rememberMe")).shouldNotBe(Condition.selected)
        `$`(By.name("rememberMe")).shouldNotBe(Condition.checked)
        `$`(By.name("rememberMe")).click()
        `$`(By.name("rememberMe")).shouldBe(Condition.selected)
        `$`(By.name("rememberMe")).shouldBe(Condition.checked)
        Assertions.assertThat(`$`(By.name("rememberMe")).describe())
            .isEqualTo("<input name=\"rememberMe\" type=\"checkbox\" value=\"on\" selected:true></input>")
    }

    @Test
    fun userCanCheckCheckbox() = runBlockingTest {
        `$`(By.name("rememberMe")).isSelected = true
        `$`(By.name("rememberMe")).shouldBe(Condition.selected)
        `$`(By.name("rememberMe")).shouldBe(Condition.checked)
        `$`(By.name("rememberMe")).isSelected = true
        `$`(By.name("rememberMe")).shouldBe(Condition.selected)
        `$`(By.name("rememberMe")).shouldBe(Condition.checked)
    }

    @Test
    fun userCanUnCheckCheckbox() = runBlockingTest {
        `$`(By.name("rememberMe")).isSelected = true
        `$`(By.name("rememberMe")).shouldBe(Condition.selected)
        `$`(By.name("rememberMe")).isSelected = false
        `$`(By.name("rememberMe")).shouldNotBe(Condition.selected)
        `$`(By.name("rememberMe")).isSelected = false
        `$`(By.name("rememberMe")).shouldNotBe(Condition.selected)
    }

    @Test
    fun userCannotSetSelectOnTextInput() {
        assertThat { `$`("#username").isSelected = false }.isFailure()
            .isInstanceOf(InvalidStateException::class.java)
    }

    @Test
    fun userCannotSetSelectOnArbitryElement() {
        assertThat { `$`("#username-mirror").isSelected = false }.isFailure()
            .isInstanceOf(InvalidStateException::class.java)
    }

    @Test
    fun userCannotCheckInvisibleCheckbox() {
        assertThat { `$`(By.name("invisibleCheckbox")).isSelected = false }.isFailure()
            .isInstanceOf(InvalidStateException::class.java)
    }
}
