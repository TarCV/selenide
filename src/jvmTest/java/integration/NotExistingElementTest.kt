package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.cause
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.messageContains
import assertk.assertions.startsWith
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.attribute
import com.codeborne.selenide.Condition.Companion.be
import com.codeborne.selenide.Condition.Companion.match
import com.codeborne.selenide.Condition.Companion.not
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.ex.ElementNotFound
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.NoSuchElementException
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class NotExistingElementTest : ITest() {
    @BeforeEach
    fun openPage() = runBlockingTest {
        openFile("elements_disappear_on_click.html")
    }

    @Test
    fun shouldNotExist() {
        `$`("#not_exist").shouldNot(Condition.exist)
    }

    @Test
    fun shouldNotExist_because() {
        `$`("#not_exist").shouldNot(Condition.exist.because("it was removed in last release"))
    }

    @Test
    fun should_not_exist() {
        `$`("#not_exist").should(not(Condition.exist))
    }

    @Test
    fun should_not_exist_because() {
        `$`("#not_exist").should(not(Condition.exist).because("it was removed in last release"))
    }

    @Test
    fun shouldNot_be_exist() {
        `$`("#not_exist").shouldNot(be(Condition.exist))
    }

    @Test
    fun shouldNot_be_exist_because() {
        `$`("#not_exist").shouldNot(be(Condition.exist).because("it was removed in last release"))
    }

    @Test
    fun shouldNot_match() {
        assertThat {
            `$`("#not_exist").shouldNot(match("border=1") { el ->
                el.getAttribute(
                    "border"
                ).equals("1")
            })
        }.isFailure()
            .all {
                isInstanceOf(ElementNotFound::class.java)
                message().isNotNull().startsWith("Element not found {#not_exist}")
            }
    }

    @Test
    fun shouldNotExistIfParentDoesNotExist() {
        `$$`("#not_exist").first().`$`("#multirowTable").shouldNot(Condition.exist)
    }

    @Test
    fun shouldBeHidden() {
        `$`("#not_exist").shouldBe(Condition.hidden)
    }

    @Test
    fun shouldNotBeVisible() {
        `$`("#not_exist").shouldNotBe(Condition.visible)
    }

    @Test
    fun toWebElement_shouldNotWait() {
        setTimeout(4000)
        val start = System.nanoTime()
        try {
            assertThat { `$`("#not_exist").toWebElement() }.isFailure()
                .all {
                    isInstanceOf(NoSuchElementException::class.java)
                    messageContains("Unable to locate element:")
                    messageContains("#not_exist")
                }
        } finally {
            val end = System.nanoTime()
            Assertions.assertThat(TimeUnit.NANOSECONDS.toMillis(end - start)).isLessThan(500)
        }
    }

    @get:Test
    val wrappedElement_waits_untilElementApears: Unit
        get() {
            setTimeout(1000)
            val start = System.nanoTime()
            try {
                assertThat { `$`("#not_exist").wrappedElement }.isFailure().all {
                    isInstanceOf(ElementNotFound::class.java)
                    message().isNotNull().startsWith("Element not found {#not_exist}")
                    cause().isNotNull().all {
                        isInstanceOf(NoSuchElementException::class.java)
                        messageContains("Unable to locate element:")
                        messageContains("#not_exist")
                    }
                }
            } finally {
                val end = System.nanoTime()
                Assertions.assertThat(TimeUnit.NANOSECONDS.toMillis(end - start))
                    .isBetween(1000L, 3000L)
            }
        }

    @Test
    fun shouldNotHaveText_fails_ifElementIsNotFound() {
        assertThat { `$`("#not_exist").shouldNotHave(text("Remove me")) }.isFailure().all {
            isInstanceOf(ElementNotFound::class.java)
            message().isNotNull().startsWith("Element not found {#not_exist}")
        }
    }

    @Test
    fun shouldNotHaveText_withBecause_fails_ifElementIsNotFound() {
        assertThat { `$`("#not_exist").shouldNotHave(text("Remove me").because("it was removed in last release")) }.isFailure().all {
            isInstanceOf(ElementNotFound::class.java)
            message().isNotNull().startsWith("Element not found {#not_exist}")
            messageContains("because it was removed in last release")
        }
    }

    @Test
    fun shouldNotHaveAttribute_fails_ifElementIsNotFound() {
        assertThat { `$`("#not_exist").shouldNotHave(attribute("abc")) }.isFailure().all {
            isInstanceOf(ElementNotFound::class.java)
            message().isNotNull().startsWith("Element not found {#not_exist}")
        }
    }
}
