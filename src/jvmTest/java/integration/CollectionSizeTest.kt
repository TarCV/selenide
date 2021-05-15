package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.messageContains
import com.codeborne.selenide.CollectionCondition.Companion.size
import com.codeborne.selenide.CollectionCondition.Companion.sizeGreaterThan
import com.codeborne.selenide.CollectionCondition.Companion.sizeGreaterThanOrEqual
import com.codeborne.selenide.CollectionCondition.Companion.sizeLessThan
import com.codeborne.selenide.CollectionCondition.Companion.sizeLessThanOrEqual
import com.codeborne.selenide.CollectionCondition.Companion.sizeNotEqual
import com.codeborne.selenide.ex.ListSizeMismatch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalFileSystem
@ExperimentalTime
@ExperimentalCoroutinesApi
internal class CollectionSizeTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun size_equals() = runBlockingTest {
        `$$`("#radioButtons input").shouldHave(size(4))
    }

    @Test
    fun size_greaterThan() = runBlockingTest {
        `$$`("#radioButtons input").shouldHave(sizeGreaterThan(3))
        `$$`("#radioButtons input").shouldHave(sizeGreaterThan(2))
        `$$`("#radioButtons input").shouldHave(sizeGreaterThan(1))
    }

    @Test
    fun size_greaterThan_failure() = runBlockingTest {
        assertThat { `$$`("#radioButtons input").shouldHave(sizeGreaterThan(4)) }
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                messageContains("expected: > 4, actual: 4")
            }
    }

    @Test
    fun size_greaterThanOrEqual() = runBlockingTest {
        `$$`("#radioButtons input").shouldHave(sizeGreaterThanOrEqual(4))
        `$$`("#radioButtons input").shouldHave(sizeGreaterThanOrEqual(3))
    }

    @Test
    fun size_greaterThanOrEqual_failure() = runBlockingTest {
        assertThat { `$$`("#radioButtons input").shouldHave(sizeGreaterThanOrEqual(5)) }
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                messageContains("expected: >= 5, actual: 4")
            }
    }

    @Test
    fun size_lessThan() = runBlockingTest {
        `$$`("#radioButtons input").shouldHave(sizeLessThan(5))
        `$$`("#radioButtons input").shouldHave(sizeLessThan(6))
        `$$`("#radioButtons input").shouldHave(sizeLessThan(7))
    }

    @Test
    fun size_lessThan_failure() = runBlockingTest {
        assertThat { `$$`("#radioButtons input").shouldHave(sizeLessThan(4)) }
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                messageContains("expected: < 4, actual: 4")
            }
    }

    @Test
    fun size_lessThanOrEqual() = runBlockingTest {
        `$$`("#radioButtons input").shouldHave(sizeLessThanOrEqual(4))
        `$$`("#radioButtons input").shouldHave(sizeLessThanOrEqual(5))
    }

    @Test
    fun size_lessThanOrEqual_failure() = runBlockingTest {
        assertThat { `$$`("#radioButtons input").shouldHave(sizeLessThanOrEqual(3)) }
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                messageContains("expected: <= 3, actual: 4")
            }
    }

    @Test
    fun size_notEqual() = runBlockingTest {
        `$$`("#radioButtons input").shouldHave(sizeNotEqual(3))
        `$$`("#radioButtons input").shouldHave(sizeNotEqual(5))
    }

    @Test
    fun size_notEqual_failure() = runBlockingTest {
        assertThat { `$$`("#radioButtons input").shouldHave(sizeNotEqual(4)) }
            .isFailure()
            .all {
                isInstanceOf(ListSizeMismatch::class.java)
                messageContains("expected: <> 4, actual: 4")
            }
    }
}
