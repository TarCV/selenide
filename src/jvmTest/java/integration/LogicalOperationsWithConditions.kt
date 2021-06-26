package integration

import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.and
import com.codeborne.selenide.Condition.Companion.be
import com.codeborne.selenide.Condition.Companion.not
import com.codeborne.selenide.Condition.Companion.or
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.ex.ElementNotFound
import com.codeborne.selenide.ex.ElementShouldNot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
@ExperimentalCoroutinesApi
@ExperimentalFileSystem
class LogicalOperationsWithConditions : ITest() {
    @BeforeEach
    fun openPage() = runBlockingTest {
        openFile("elements_disappear_on_click.html")
    }

    @Test
    fun not_be() = runBlockingTest {
        `$`(".lolkek").shouldNotBe(Condition.visible)
        `$`(".lolkek").shouldBe(not(Condition.visible))
        `$`(".lolkek").shouldNot(be(Condition.visible))
        `$`(".lolkek").shouldNot(be(not(not(Condition.visible))))
        `$`(".lolkek").shouldNot(not(be(not(Condition.visible))))
    }

    @Test
    fun andRevertsMissingElementTolerance() = runBlockingTest {
        `$`(".lolkek").shouldNotBe(and("visible&visible", Condition.visible, Condition.visible))
        assertThat { `$`(".lolkek").shouldNotHave(text("Lasnamäe")) }.isFailure()
            .isInstanceOf(ElementNotFound::class.java)
        assertThat {
            `$`(".lolkek").shouldNotBe(
                and(
                    "visible&text",
                    Condition.visible,
                    text("Lasnamäe")
                )
            )
        }.isFailure()
            .isInstanceOf(ElementNotFound::class.java)
        assertThat {
            `$`(".lolkek").shouldNotBe(
                and(
                    "visible&text",
                    Condition.visible,
                    text("Lasnamäe")
                ),
                5.milliseconds
            )
        }.isFailure()
            .isInstanceOf(ElementNotFound::class.java)
        assertThat {
            `$`(".lolkek").shouldNotBe(
                and(
                    "text&visible",
                    text("Lasnamäe"),
                    Condition.visible
                )
            )
        }.isFailure()
            .isInstanceOf(ElementNotFound::class.java)
        assertThat {
            `$`(".lolkek").shouldNotBe(
                and(
                    "text&visible",
                    text("Lasnamäe"),
                    Condition.visible
                ), 5.milliseconds
            )
        }.isFailure()
            .isInstanceOf(ElementNotFound::class.java)
    }

    @Test
    fun orRevertsMissingElementTolerance() = runBlockingTest {
        `$`(".lolkek").shouldNotBe(or("visible||exist", Condition.visible, Condition.exist))
        assertThat {
            `$`(".lolkek").shouldNotBe(
                or(
                    "visible||text",
                    Condition.visible,
                    text("Lasnamäe")
                )
            )
        }.isFailure()
            .isInstanceOf(ElementNotFound::class.java)
        assertThat {
            `$`(".lolkek").shouldNotBe(
                or(
                    "text||visible",
                    text("Lasnamäe"),
                    Condition.visible
                )
            )
        }.isFailure()
            .isInstanceOf(ElementNotFound::class.java)
    }

    @Test
    fun andRevertsExistingElement() = runBlockingTest {
        `$`("h1").shouldHave(text("Element removed"), text("from DOM"))
        `$`("h1").shouldHave(and("2 texts", text("Element removed"), text("from DOM")))
        `$`("h1").shouldNotHave(and("2 texts", text("Lasnamäe"), text("from DOM")))
        `$`("h1").shouldNotHave(and("2 texts", text("Element removed"), text("Lasnamäe")))
        assertThat {
            `$`("h1").shouldNotHave(
                and(
                    "2 texts",
                    text("Element removed"),
                    text("from DOM")
                )
            )
        }.isFailure()
            .isInstanceOf(ElementShouldNot::class.java)
    }

    @Test
    fun orRevertsExistingElement() = runBlockingTest {
        val text1 = "Element removed"
        val text2 = "from DOM"
        `$`("h1").shouldHave(or("2 texts", text(text1), text(text2)))
        `$`("h1").shouldHave(or("2 texts", text("Lasnamäe"), text(text2)))
        `$`("h1").shouldHave(or("2 texts", text(text1), text("Lasnamäe")))
        `$`("h1").shouldNotHave(or("2 texts", text("Tallinn"), text("Lasnamäe")))
        assertThat { `$`("h1").shouldNotHave(or("2 texts", text(text1), text(text2))) }.isFailure()
            .isInstanceOf(ElementShouldNot::class.java)
        assertThat { `$`("h1").shouldNotHave(or("2 texts", text("Lasnamäe"), text(text2))) }.isFailure()
            .isInstanceOf(ElementShouldNot::class.java)
        assertThat { `$`("h1").shouldNotHave(or("2 texts", text(text1), text("Lasnamäe"))) }.isFailure()
            .isInstanceOf(ElementShouldNot::class.java)
    }
}
