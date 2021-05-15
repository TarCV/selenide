package integration

import com.codeborne.selenide.Condition.Companion.selectedText
import com.codeborne.selenide.SelenideElement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class SelectedTextTest : ITest() {
    @BeforeEach
    fun before() = runBlockingTest {
        openFile("page_with_selectable_text.html")
    }

    @Test
    fun selectedTextIsCorrect() {
        makeSelection(0, 5)
        selectableElement.shouldHave(selectedText("this "))
    }

    @Test
    fun selectedTextIsCaseSensitive() {
        makeSelection(5, 10)
        selectableElement.shouldNotHave(selectedText("Is a "))
    }

    @Test
    fun selectedTextReturnsEmptyWhenNothingIsSelected() {
        selectableElement.shouldHave(selectedText(""))
    }

    @Test
    fun reappliedSelectionsAreDetectedCorrectly() {
        makeSelection(2, 4)
        makeSelection(3, 13)
        selectableElement.shouldHave(selectedText("s is a lon"))
    }

    private val selectableElement: SelenideElement
        get() = `$`(By.id("selected"))

    private fun makeSelection(start: Int, tail: Int) {
        `$`(By.id("start")).setValue(start.toString())
        `$`(By.id("tail")).setValue(tail.toString())
        `$`(By.id("selectable")).click()
    }
}
