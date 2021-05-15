package integration

import com.codeborne.selenide.Condition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * All checks in this class are equivalent
 */
@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class ElementRemovedTest : ITest() {
    @BeforeEach
    fun clickRemovesElement() = runBlockingTest {
        openFile("elements_disappear_on_click.html")
        setTimeout(2000)
        `$`("#remove").click()
    }

    @Test
    fun shouldBeHidden() {
        `$`("#remove").shouldBe(Condition.hidden)
    }

    @Test
    fun shouldDisappear() {
        `$`("#remove").should(Condition.disappear)
    }

    @Test
    fun waitUntilDisappears() {
        `$`("#remove").waitUntil(Condition.disappears, 2000)
    }

    @Test
    fun shouldNotBeVisible() {
        `$`("#remove").shouldNotBe(Condition.visible)
    }

    @Test
    fun shouldNotExist() {
        `$`("#remove").shouldNot(Condition.exist)
    }

    @Test
    fun shouldNotAppear() {
        `$`("#remove").shouldNot(Condition.appear)
    }
}
