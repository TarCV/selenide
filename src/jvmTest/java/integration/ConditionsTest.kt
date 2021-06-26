package integration

import assertk.all
import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.message
import assertk.assertions.messageContains
import assertk.assertions.startsWith
import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.and
import com.codeborne.selenide.Condition.Companion.attribute
import com.codeborne.selenide.Condition.Companion.be
import com.codeborne.selenide.Condition.Companion.cssClass
import com.codeborne.selenide.Condition.Companion.have
import com.codeborne.selenide.Condition.Companion.match
import com.codeborne.selenide.Condition.Companion.not
import com.codeborne.selenide.Condition.Companion.or
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.ex.ElementShould
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class ConditionsTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun andShouldCheckConditions() = runBlockingTest {
        `$`("#multirowTable").should(
            and(
                "visible && table",
                be(Condition.visible),
                have(cssClass("table"))
            )
        ) // both true
        `$`("#multirowTable").shouldNot(
            and(
                "visible && list",
                be(Condition.visible),
                have(cssClass("list"))
            )
        ) // first true
        `$`("#multirowTable").shouldNot(
            and(
                "hidden && table",
                be(Condition.hidden),
                have(cssClass("table"))
            )
        ) // second true
        `$`("#multirowTable").shouldNot(
            and(
                "hidden && list",
                be(Condition.hidden),
                have(cssClass("list"))
            )
        ) // both false
    }

    @Test
    fun orShouldCheckConditions() = runBlockingTest {
        `$`("#multirowTable").should(
            or(
                "visible || table",
                be(Condition.visible),
                have(cssClass("table"))
            )
        ) // both true
        `$`("#multirowTable").should(
            or(
                "visible || list",
                be(Condition.visible),
                have(cssClass("table1"))
            )
        ) // first true
        `$`("#multirowTable").should(
            or(
                "hidden || table",
                be(Condition.hidden),
                have(cssClass("table"))
            )
        ) // second true
        `$`("#multirowTable").shouldNot(
            or(
                "hidden || list",
                be(Condition.hidden),
                have(cssClass("list"))
            )
        ) // both false
    }

    @Test
    fun orShouldReportAllConditions() = runBlockingTest {
        assertThat {
            `$`("#multirowTable").shouldBe(
                or(
                    "non-active", be(Condition.disabled), have(
                        cssClass("inactive")
                    )
                )
            )
        }.isFailure()
            .all {
                isInstanceOf(ElementShould::class.java)
                message().isNotNull().startsWith("Element should be non-active: be disabled or have css class 'inactive' {#multirowTable}")
            }
    }

    @Test
    fun orShouldReportAllConditionsWithActualValues() = runBlockingTest {
        assertThat {
            `$`("#multirowTable").shouldHave(
                or(
                    "class || border",
                    attribute("class", "foo"),
                    attribute("border", "bar")
                )
            )
        }.isFailure()
            .all {
                isInstanceOf(ElementShould::class.java)
                message().isNotNull().startsWith(
                    "Element should have class || border: attribute class=\"foo\" or attribute border=\"bar\" {#multirowTable}"
                )
                messageContains("Actual value: class=\"table multirow_table\", border=\"1\"")
            }
    }

    @Test
    fun notShouldCheckConditions() = runBlockingTest {
        `$`("#multirowTable").should(be(Condition.visible))
        `$`("#multirowTable").should(not(be(Condition.hidden)))
    }

    @Test
    fun userCanUseOrCondition() = runBlockingTest {
        val one_of_conditions = or("baskerville", text("Basker"), text("Walle"))
        `$`("#baskerville").shouldBe(one_of_conditions)
        val all_of_conditions = or("baskerville", text("Basker"), text("rville"))
        `$`("#baskerville").shouldBe(all_of_conditions)
        val none_of_conditions = or("baskerville", text("pasker"), text("wille"))
        `$`("#baskerville").shouldNotBe(none_of_conditions)
    }

    @Test
    fun matchWithCustomPredicateShouldCheckCondition() = runBlockingTest {
        `$`("#multirowTable").should(match("border=1") { el ->
            el.getAttribute("border").equals("1")
        })
    }

    @Test
    fun matchWithPredicateShouldReportErrorMessage() = runBlockingTest {
        assertThat {
            `$`("#multirowTable").should(match("tag=input") { el ->
                el.getTagName().equals("input1")
            })
        }.isFailure()
            .message().isNotNull().startsWith(
                "Element should match 'tag=input' predicate. {#multirowTable}"
            )
    }

    @Test
    fun matchWithShouldNotPredicateReportErrorMessage() = runBlockingTest {
        assertThat {
            `$`("#multirowTable").shouldNot(match("border=1") { el ->
                el.getAttribute(
                    "border"
                ).equals("1")
            })
        }.isFailure()
            .message().isNotNull().startsWith(
                "Element should not match 'border=1' predicate. {#multirowTable}"
            )
    }
}
