package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.startsWith
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.attribute
import com.codeborne.selenide.Condition.Companion.be
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class LastChildTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun canFindLastChildOnAGivenElement() {
        `$x`("//table[@id='user-table']/thead/tr").lastChild().shouldHave(text("Age"))
    }

    @Test
    fun chainingLastChildFunctionsCorrectly() {
        `$`("#multirowTable").lastChild().lastChild().shouldHave(attribute("id", "multirowTableSecondRow"))
    }

    @Test
    fun throwsExceptionWhenNoChildrenExist() {
        val expectedError = String.format(
            "Element not found {By.xpath: //span[@id='hello-world']" +
                    "/By.xpath: *[last()]}%nExpected: be visible"
        )
        assertThat { `$x`("//span[@id='hello-world']").lastChild().should(be(Condition.visible)) }.isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                message().isNotNull().startsWith(expectedError)
            }
    }
}
