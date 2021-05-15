package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.startsWith
import com.codeborne.selenide.CollectionCondition.Companion.size
import com.codeborne.selenide.CollectionCondition.Companion.texts
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalFileSystem
internal class MultipleSelectTest : ITest() {
    private val select = `$`("#character")
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("page_with_multiple_select.html")
    }

    @Test
    fun userCanSelectMultipleOptionsByText() = runBlockingTest {
        select.selectOption("Маргарита", "Theodor Woland")
        select.selectedOptions.shouldHave(
            texts("Маргарита", "Theodor Woland")
        )
    }

    @Test
    fun reportsElementNotFound_ifThereIsNoSelectedOptions() = runBlockingTest {
        assertThat { select.selectedOptions.shouldHave(texts("Кот", "Бегемот")) }.isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                message().isNotNull().startsWith("Element not found {#character selected options}")
            }
    }

    @Test
    fun canGiveHumanReadableNameToSelectedOptions() = runBlockingTest {
        val namedCollection = select.selectedOptions.`as`("my animals")
        assertThat { namedCollection.shouldHave(texts("Кот", "Бегемот")) }.isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                message().isNotNull().startsWith("Element not found {my animals}")
            }
    }

    @Test
    fun userCanSelectMultipleOptionsByIndex() = runBlockingTest {
        select.selectOption(0, 2, 3)
        select.selectedOptions.shouldHave(texts("Мастер", "Кот \"Бегемот\"", "Theodor Woland"))
        select.selectedOptions.get(0).shouldHave(text("Мастер"))
        select.selectedOptions.get(1).shouldHave(text("Кот \"Бегемот\""))
        select.selectedOptions.get(2).shouldHave(text("Theodor Woland"))
    }

    @Test
    fun userCanSelectMultipleOptionsByValue() = runBlockingTest {
        select.selectOptionByValue("cat", "woland")
        select.selectedOptions.shouldHave(
            size(2),
            texts("Кот \"Бегемот\"", "Theodor Woland")
        )
    }

    @Test
    fun userCanUseSetSelectedOnOptions() = runBlockingTest {
        select.`$`("option[value=cat]").isSelected = true
        select.selectedOptions.shouldHave(
            size(1),
            texts("Кот \"Бегемот\"")
        )
        select.`$`("option[value=cat]").isSelected = false
        select.selectedOptions.shouldHave(size(0))
    }
}
