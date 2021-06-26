package integration

import com.codeborne.selenide.Condition
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalFileSystem
internal class ElementHiddenTest : ITest() {
    @BeforeEach
    fun clickRemovesElement() = runBlockingTest {
        setTimeout(4000)
        openFile("elements_disappear_on_click.html")
        `$`("#hide").click()
    }

    @Test
    fun shouldBeHidden() = runBlockingTest {
        `$`("#hide").shouldBe(Condition.hidden)
    }

    @Test
    fun shouldDisappear() = runBlockingTest {
        `$`("#hide").should(Condition.disappear)
    }

    @Test
    fun waitUntilDisappears() = runBlockingTest {
        `$`("#hide").waitUntil(Condition.disappears, 2000)
    }

    @Test
    fun shouldNotBeVisible() = runBlockingTest {
        `$`("#hide").shouldNotBe(Condition.visible)
    }

    @Test
    fun shouldExist() = runBlockingTest {
        `$`("#hide").should(Condition.exist)
    }

    @Test
    fun shouldNotAppear() = runBlockingTest {
        `$`("#hide").shouldNot(Condition.appear)
    }
}
