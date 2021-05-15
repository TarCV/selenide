package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.startsWith
import com.codeborne.selenide.CollectionCondition.Companion.size
import com.codeborne.selenide.Condition
import com.codeborne.selenide.ex.ListSizeMismatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@ExperimentalFileSystem
class NamedCollectionsTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun canGiveCollectionHumanReadableName() = runBlockingTest {
        assertThat { `$$x`("/long/ugly/xpath").`as`("Login buttons").shouldHave(size(666)) }.isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                message().isNotNull().startsWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons")
            }
    }

    @Test
    fun canGiveCollectionHumanReadableName_filtered_and_named() = runBlockingTest {
        assertThat {
            `$$x`("/long/ugly/xpath").`as`("Login buttons").filter(Condition.enabled).`as`("enabled buttons")
                .shouldHave(
                    size(666)
                )
        }.isFailure().all {
            isInstanceOf(ListSizeMismatch::class.java)
            message().isNotNull().startsWith("List size mismatch: expected: = 666, actual: 0, collection: enabled buttons")
        }
    }

    @Test
    fun canGiveCollectionHumanReadableName_named_and_filtered() = runBlockingTest {
        assertThat {
            `$$x`("/long/ugly/xpath").`as`("Login buttons").filter(Condition.enabled).shouldHave(
                size(666)
            )
        }.isFailure().all {
            isInstanceOf(ListSizeMismatch::class.java)
            message().isNotNull().startsWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons.filter(enabled)")
        }
    }

    @Test
    fun canGiveCollectionHumanReadableName_wrapped() = runBlockingTest {
        val unfiltered = driver().webDriver.findElements(By.xpath("/long/ugly/xpath"))
        assertThat {
            val unnamed = driver().`$$`(unfiltered)
            unnamed.`as`("Login buttons").filter(Condition.enabled).shouldHave(size(666))
        }.isFailure().all {
            isInstanceOf(ListSizeMismatch::class.java)
            message().isNotNull().startsWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons.filter(enabled)")
        }
    }

    @Test
    fun canGiveCollectionHumanReadableName_snapshot() = runBlockingTest {
        assertThat {
            `$$x`("/long/ugly/xpath").snapshot().`as`("Login buttons").shouldHave(size(666))
        }.isFailure().all {
            isInstanceOf(ListSizeMismatch::class.java)
            message().isNotNull().startsWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons")
        }
    }

    @Test
    fun collectionDescription_head() = runBlockingTest {
        assertThat { `$$x`("/long/ugly/xpath").first(42).shouldHave(size(666)) }.isFailure().all {
            isInstanceOf(ListSizeMismatch::class.java)
            message().isNotNull().startsWith("List size mismatch: expected: = 666, actual: 0, collection: By.xpath: /long/ugly/xpath:first(42)")
        }
    }

    @Test
    fun canGiveCollectionHumanReadableName_head() = runBlockingTest {
        assertThat {
            `$$x`("/long/ugly/xpath").first(42).`as`("Login buttons").shouldHave(size(666))
        }.isFailure().all {
            isInstanceOf(ListSizeMismatch::class.java)
            message().isNotNull().startsWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons")
        }
    }

    @Test
    fun canGiveCollectionHumanReadableName_tail() = runBlockingTest {
        assertThat { `$$x`("/long/ugly/xpath").last(42).`as`("Login buttons").shouldHave(size(666)) }.isFailure().all {
            isInstanceOf(ListSizeMismatch::class.java)
            message().isNotNull().startsWith("List size mismatch: expected: = 666, actual: 0, collection: Login buttons")
        }
    }
}
