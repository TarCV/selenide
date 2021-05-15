package integration

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.text
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By

@ExperimentalFileSystem
internal class DynamicSelectsTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("page_with_dynamic_select.html")
        setTimeout(4000)
    }

    @Test
    fun waitsUntilOptionWithTextAppears() {
        `$`("#language").selectOption("l'a \"English\"")
        val select = `$`("#language")
        select.selectedOption.shouldBe(Condition.selected)
        Assertions.assertThat(select.selectedValue)
            .isEqualTo("'eng'")
        Assertions.assertThat(select.selectedText)
            .isEqualTo("l'a \"English\"")
    }

    @Test
    fun waitsUntilOptionWithValueAppears() {
        `$`("#language").selectOptionByValue("\"est\"")
        val select = `$`("#language")
        select.selectedOption.shouldBe(Condition.selected)
        Assertions.assertThat(select.selectedValue)
            .isEqualTo("\"est\"")
        Assertions.assertThat(select.selectedText)
            .isEqualTo("l'a \"Eesti\"")
    }

    @Test
    fun selectByXPath() {
        `$`(By.xpath("html/body/div[1]/form[1]/label[1]/select[1]")).selectOption("l'a \"English\"")
        Assertions.assertThat(`$`(By.xpath("html/body/div[1]/form[1]/label[1]/select[1]")).selectedText)
            .isEqualTo("l'a \"English\"")
    }

    @Test
    fun selectingOptionTriggersChangeEvent() {
        `$`("#language").selectOption("l'a \"English\"")
        `$`("h2").shouldHave(text("'eng'"))
    }

    @Test
    fun selectingOptionRebuildsAnotherSelect() {
        `$`("#language").selectOption("l'a \"Русский\"")
        `$`("#books").selectOption("книжко")
    }
}
