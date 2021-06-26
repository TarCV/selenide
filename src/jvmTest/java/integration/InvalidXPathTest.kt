package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.messageContains
import com.codeborne.selenide.CollectionCondition.Companion.sizeGreaterThan
import com.codeborne.selenide.CollectionCondition.Companion.sizeLessThan
import com.codeborne.selenide.Condition
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.InvalidSelectorException
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalFileSystem
internal class InvalidXPathTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun usingInvalidXPathShouldThrowInvalidSelectorException() = runBlockingTest {
        assertThat { `$`(By.xpath("##[id")).shouldNot(Condition.exist) }.isFailure()
            .isInstanceOf(InvalidSelectorException::class.java)
    }

    @Test
    fun lookingForMissingElementByXPathShouldFail() = runBlockingTest {
        assertThat { `$`(By.xpath("//tagga")).should(Condition.exist) }.isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                messageContains("Element not found {By.xpath: //tagga}")
            }
    }

    @Test
    fun `$x_insideElement_cannotUseXpathStartingWithSlash`() = runBlockingTest {
        assertThat { `$`("#apostrophes-and-quotes").`$x`("//a").should(Condition.exist) }.isFailure()
            .all {
                isInstanceOf(IllegalArgumentException::class.java)
                hasMessage("XPath starting from / searches from root")
            }
    }

    @Test
    fun find_insideElement_cannotUseXpathStartingWithSlash() = runBlockingTest {
        assertThat { `$`("#apostrophes-and-quotes").`$`(By.xpath("//a")).should(Condition.exist) }.isFailure()
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("XPath starting from / searches from root")
        assertThat {
            `$`("#apostrophes-and-quotes").find(By.xpath("//a"))!!.should(Condition.exist)
        }.isFailure()
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("XPath starting from / searches from root")
    }

    @Test
    fun findAll_insideElement_cannotUseXpathStartingWithSlash() = runBlockingTest {
        assertThat {
            `$`("#apostrophes-and-quotes").`$$`(By.xpath("//a")).shouldHave(sizeGreaterThan(0))
        }.isFailure()
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("XPath starting from / searches from root")
        assertThat {
            `$`("#apostrophes-and-quotes").findAll(By.xpath("//a")).shouldHave(sizeLessThan(0))
        }.isFailure()
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("XPath starting from / searches from root")
    }
}
