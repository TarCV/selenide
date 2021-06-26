package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.startsWith
import com.codeborne.selenide.Condition.Companion.cssClass
import com.codeborne.selenide.Condition.Companion.id
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalFileSystem
internal class SiblingTest : ITest() {
    @BeforeEach
    fun openTestPageWith() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun canGetSiblingElement() = runBlockingTest {
        `$`("#multirowTableFirstRow").sibling(0).shouldHave(id("multirowTableSecondRow"))
        `$`(".first_row").sibling(0).shouldHave(text("Norris"))
    }

    @Test
    fun canGetSiblingOfParent() = runBlockingTest {
        `$`(".first_row").parent().sibling(0).find("td", 1).shouldHave(id("baskerville"))
    }

    @Test
    fun errorWhenSiblingAbsent() = runBlockingTest {
        assertThat { `$`("#multirowTableFirstRow").sibling(3).click() }.isFailure().all {
            isInstanceOf(ElementNotFound::class.java)
            message().isNotNull().startsWith("Element not found {#multirowTableFirstRow/By.xpath: following-sibling::*[4]}")
        }
    }

    @Test
    fun canGetPrecedingElement() = runBlockingTest {
        `$`("#multirowTableSecondRow").preceding(0).shouldHave(id("multirowTableFirstRow"))
        `$`("#baskerville").preceding(0).shouldHave(text("Chack"))
    }

    @Test
    fun canGetPrecedingElementOfParent() = runBlockingTest {
        `$`(".second_row").parent().preceding(0).find("td", 0).shouldHave(cssClass("first_row"))
    }

    @Test
    fun errorWhenPrecedingElementAbsent() = runBlockingTest {
        assertThat { `$`("#multirowTableSecondRow").preceding(3).click() }.isFailure().all {
            isInstanceOf(ElementNotFound::class.java)
            message().isNotNull().startsWith("Element not found {#multirowTableSecondRow/By.xpath: preceding-sibling::*[4]}")
        }
    }
}
