package com.codeborne.selenide

import assertk.all
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.Condition.Companion.and
import com.codeborne.selenide.Condition.Companion.attribute
import com.codeborne.selenide.Condition.Companion.attributeMatching
import com.codeborne.selenide.Condition.Companion.be
import com.codeborne.selenide.Condition.Companion.cssClass
import com.codeborne.selenide.Condition.Companion.cssValue
import com.codeborne.selenide.Condition.Companion.exactText
import com.codeborne.selenide.Condition.Companion.exactTextCaseSensitive
import com.codeborne.selenide.Condition.Companion.have
import com.codeborne.selenide.Condition.Companion.id
import com.codeborne.selenide.Condition.Companion.matchesText
import com.codeborne.selenide.Condition.Companion.name
import com.codeborne.selenide.Condition.Companion.not
import com.codeborne.selenide.Condition.Companion.or
import com.codeborne.selenide.Condition.Companion.text
import com.codeborne.selenide.Condition.Companion.textCaseSensitive
import com.codeborne.selenide.Condition.Companion.type
import com.codeborne.selenide.Condition.Companion.value
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

@ExperimentalCoroutinesApi
internal class ConditionTest {
    private val webDriver = Mockito.mock(WebDriver::class.java)
    private val config = SelenideConfig()
    private val driver: Driver = DriverStub(null, config, Browser("opera", false), webDriver, null)
    @Test
    fun displaysHumanReadableName() {
        Assertions.assertThat(Condition.visible).hasToString("visible")
        Assertions.assertThat(Condition.hidden).hasToString("hidden")
        Assertions.assertThat(attribute("lastName", "Malkovich")).hasToString("attribute lastName=\"Malkovich\"")
    }

    @Test
    fun textConditionChecksForSubstring() {
        Assertions.assertThat(
            text("John Malkovich The First").applyBlocking(
                driver,
                elementWithText("John Malkovich The First")
            )
        )
            .isTrue
        Assertions.assertThat(
            text("John Malkovich First").applyBlocking(
                driver,
                elementWithText("John Malkovich The First")
            )
        )
            .isFalse
        Assertions.assertThat(text("john bon jovi").applyBlocking(driver, elementWithText("John Malkovich The First")))
            .isFalse
    }

    private fun elementWithText(text: String): WebElement {
        val element = Mockito.mock(WebElement::class.java)
        Mockito.`when`(element.text).thenReturn(text)
        return element
    }

    @Test
    fun textConditionIsCaseInsensitive() = runBlockingTest {
        val element = elementWithText("John Malkovich The First")
        Assertions.assertThat(text("john malkovich").apply(driver, element)).isTrue()
    }

    @Test
    fun textConditionIgnoresWhitespaces() = runBlockingTest {
        Assertions.assertThat(text("john the malkovich").apply(driver, elementWithText("John  the\n Malkovich")))
            .isTrue
        Assertions.assertThat(
            text("This is nonbreakable space").apply(
                driver,
                elementWithText("This is nonbreakable\u00a0space")
            )
        )
            .isTrue()
    }

    @Test
    fun testTextCaseSensitive() = runBlockingTest {
        val element = elementWithText("John Malkovich The First")
        Assertions.assertThat(textCaseSensitive("john malkovich").apply(driver, element)).isFalse
        Assertions.assertThat(textCaseSensitive("John Malkovich").apply(driver, element)).isTrue()
    }

    @Test
    fun textCaseSensitiveIgnoresWhitespaces() = runBlockingTest {
        val element = elementWithText("John Malkovich\t The   \n First")
        Assertions.assertThat(textCaseSensitive("john malkovich").apply(driver, element)).isFalse()
        Assertions.assertThat(textCaseSensitive("John        Malkovich The   ").apply(driver, element)).isTrue()
    }

    @Test
    fun textCaseSensitiveToString() {
        Assertions.assertThat(textCaseSensitive("John Malcovich")).hasToString("textCaseSensitive 'John Malcovich'")
    }

    @Test
    fun exactTextIsCaseInsensitive() = runBlockingTest {
        val element = elementWithText("John Malkovich")
        Assertions.assertThat(exactText("john malkovich").apply(driver, element)).isTrue()
        Assertions.assertThat(exactText("john").apply(driver, element)).isFalse()
    }

    @Test
    fun exactTextToString() = runBlockingTest {
        Assertions.assertThat(exactText("John Malcovich")).hasToString("exact text 'John Malcovich'")
    }

    @Test
    fun testExactTextCaseSensitive() = runBlockingTest {
        val element = elementWithText("John Malkovich")
        Assertions.assertThat(exactTextCaseSensitive("john malkovich").apply(driver, element)).isFalse()
        Assertions.assertThat(exactTextCaseSensitive("John Malkovich").apply(driver, element)).isTrue()
        Assertions.assertThat(exactTextCaseSensitive("John").apply(driver, element)).isFalse()
    }

    @Test
    fun exactTextCaseSensitiveToString() {
        Assertions.assertThat(exactTextCaseSensitive("John Malcovich"))
            .hasToString("exact text case sensitive 'John Malcovich'")
    }

    @Test
    fun value() = runBlockingTest {
        val element = elementWithAttribute("value", "John Malkovich")
        Assertions.assertThat(value("Peter").apply(driver, element)).isFalse()
        Assertions.assertThat(value("john").apply(driver, element)).isTrue()
        Assertions.assertThat(value("john malkovich").apply(driver, element)).isTrue()
        Assertions.assertThat(value("John").apply(driver, element)).isTrue()
        Assertions.assertThat(value("John Malkovich").apply(driver, element)).isTrue()
        Assertions.assertThat(value("malko").apply(driver, element)).isTrue()
    }

    private fun elementWithAttribute(name: String, value: String): WebElement {
        val element = Mockito.mock(WebElement::class.java)
        Mockito.`when`(element.getAttribute(name)).thenReturn(value)
        return element
    }

    @Test
    fun valueToString() {
        Assertions.assertThat(value("John Malkovich"))
            .hasToString("value 'John Malkovich'")
    }

    @Test
    fun elementIsVisible() = runBlockingTest {
        Assertions.assertThat(Condition.visible.apply(driver, elementWithVisibility(true))).isTrue()
        Assertions.assertThat(Condition.visible.apply(driver, elementWithVisibility(false))).isFalse()
    }

    private fun elementWithVisibility(isVisible: Boolean): WebElement {
        val element = Mockito.mock(WebElement::class.java)
        Mockito.`when`(element.isDisplayed).thenReturn(isVisible)
        return element
    }

    @Test
    fun elementExists() = runBlockingTest{
        Assertions.assertThat(Condition.exist.apply(driver, elementWithVisibility(true))).isTrue()
        Assertions.assertThat(Condition.exist.apply(driver, elementWithVisibility(false))).isTrue()
    }

    @Test
    fun elementExists_returnsFalse_ifItThrowsException() = runBlockingTest {
        val element = Mockito.mock(WebElement::class.java)
        Mockito.`when`(element.isDisplayed).thenThrow(StaleElementReferenceException("ups"))
        Assertions.assertThat(Condition.exist.apply(driver, element)).isFalse()
    }

    @Test
    fun elementIsHidden() = runBlockingTest {
        Assertions.assertThat(Condition.hidden.apply(driver, elementWithVisibility(false))).isTrue()
        Assertions.assertThat(Condition.hidden.apply(driver, elementWithVisibility(true))).isFalse()
    }

    @Test
    fun elementIsHiddenWithStaleElementException() = runBlockingTest {
        val element = Mockito.mock(WebElement::class.java)
        Mockito.doThrow(StaleElementReferenceException("Oooops")).`when`(element).isDisplayed
        Assertions.assertThat(Condition.hidden.apply(driver, element)).isTrue()
    }

    @Test
    fun elementHasAttribute() = runBlockingTest {
        Assertions.assertThat(attribute("name").apply(driver, elementWithAttribute("name", "selenide"))).isTrue()
        Assertions.assertThat(attribute("name").apply(driver, elementWithAttribute("name", ""))).isTrue()
        Assertions.assertThat(attribute("name").apply(driver, elementWithAttribute("id", "id3"))).isFalse()
    }

    @Test
    fun elementHasAttributeWithGivenValue() = runBlockingTest {
        Assertions.assertThat(
            attribute("name", "selenide").apply(
                driver,
                elementWithAttribute("name", "selenide")
            )
        ).isTrue()
        Assertions.assertThat(
            attribute("name", "selenide").apply(
                driver,
                elementWithAttribute("name", "selenide is great")
            )
        ).isFalse()
        Assertions.assertThat(attribute("name", "selenide").apply(driver, elementWithAttribute("id", "id2")))
            .isFalse()
    }

    @Test
    fun elementHasAttributeMatching() = runBlockingTest {
        Assertions.assertThat(
            attributeMatching("name", "selenide").apply(
                driver,
                elementWithAttribute("name", "selenide")
            )
        ).isTrue()
        Assertions.assertThat(
            attributeMatching("name", "selenide.*").apply(
                driver,
                elementWithAttribute("name", "selenide is great")
            )
        ).isTrue()
        Assertions.assertThat(
            attributeMatching("name", "selenide.*").apply(
                driver,
                elementWithAttribute("id", "selenide")
            )
        ).isFalse()
        Assertions.assertThat(
            attributeMatching("name", "value.*").apply(
                driver,
                elementWithAttribute("name", "another value")
            )
        ).isFalse()
    }

    @Test
    fun elementHasValue() = runBlockingTest {
        Assertions.assertThat(value("selenide").apply(driver, elementWithAttribute("value", "selenide"))).isTrue()
        Assertions.assertThat(value("selenide").apply(driver, elementWithAttribute("value", "selenide is great")))
            .isTrue()
        Assertions.assertThat(value("selenide").apply(driver, elementWithAttribute("value", "is great"))).isFalse()
    }

    @Test
    fun elementHasName() = runBlockingTest {
        Assertions.assertThat(name("selenide").apply(driver, elementWithAttribute("name", "selenide"))).isTrue()
        Assertions.assertThat(name("selenide").apply(driver, elementWithAttribute("name", "selenide is great")))
            .isFalse()
    }

    @Test
    fun elementHasType() = runBlockingTest {
        Assertions.assertThat(type("selenide").apply(driver, elementWithAttribute("type", "selenide"))).isTrue()
        Assertions.assertThat(type("selenide").apply(driver, elementWithAttribute("type", "selenide is great")))
            .isFalse()
    }

    @Test
    fun elementHasId() = runBlockingTest {
        Assertions.assertThat(id("selenide").apply(driver, elementWithAttribute("id", "selenide"))).isTrue()
        Assertions.assertThat(id("selenide").apply(driver, elementWithAttribute("id", "selenide is great")))
            .isFalse()
    }

    @Test
    fun elementMatchesText() = runBlockingTest {
        Assertions.assertThat(matchesText("selenide").apply(driver, elementWithText("selenidehello"))).isTrue()
        Assertions.assertThat(
            matchesText("selenide").apply(
                driver,
                elementWithText("  this is  selenide  the great ")
            )
        ).isTrue()
        Assertions.assertThat(
            matchesText("selenide\\s+hello\\s*").apply(
                driver,
                elementWithText("selenide    hello")
            )
        ).isTrue()
        Assertions.assertThat(matchesText("selenide").apply(driver, elementWithText("selenite"))).isFalse()
    }

    @Test
    fun elementMatchTextToString() {
        Assertions.assertThat(matchesText("John Malcovich")).hasToString("match text 'John Malcovich'")
    }

    @Test
    fun elementHasClass() = runBlockingTest {
        Assertions.assertThat(cssClass("btn").apply(driver, elementWithAttribute("class", "btn btn-warning")))
            .isTrue()
        Assertions.assertThat(
            cssClass("btn-warning").apply(
                driver,
                elementWithAttribute("class", "btn btn-warning")
            )
        ).isTrue()
        Assertions.assertThat(cssClass("active").apply(driver, elementWithAttribute("class", "btn btn-warning")))
            .isFalse()
        Assertions.assertThat(cssClass("").apply(driver, elementWithAttribute("class", "btn btn-warning active")))
            .isFalse()
        Assertions.assertThat(cssClass("active").apply(driver, elementWithAttribute("href", "no-class"))).isFalse()
    }

    @Test
    fun elementHasCssValue() = runBlockingTest {
        Assertions.assertThat(cssValue("display", "none").apply(driver, elementWithCssStyle("display", "none")))
            .isTrue()
        Assertions.assertThat(cssValue("font-size", "24").apply(driver, elementWithCssStyle("font-size", "20")))
            .isFalse()
    }

    private fun elementWithCssStyle(propertyName: String, value: String): WebElement {
        val element = Mockito.mock(WebElement::class.java)
        Mockito.`when`(element.getCssValue(propertyName)).thenReturn(value)
        return element
    }

    @Test
    fun elementHasClassToString() {
        Assertions.assertThat(cssClass("Foo")).hasToString("css class 'Foo'")
    }

    @Test
    fun elementEnabled() = runBlockingTest {
        Assertions.assertThat(Condition.enabled.apply(driver, elementWithEnabled(true))).isTrue()
        Assertions.assertThat(Condition.enabled.apply(driver, elementWithEnabled(false))).isFalse()
    }

    private fun elementWithEnabled(isEnabled: Boolean): WebElement {
        val element = Mockito.mock(WebElement::class.java)
        Mockito.`when`(element.isEnabled).thenReturn(isEnabled)
        return element
    }

    @Test
    fun elementEnabledActualValue() = runBlockingTest {
        Assertions.assertThat<Any?>(Condition.enabled.actualValue(driver, elementWithEnabled(true)))
            .isEqualTo("enabled")
        Assertions.assertThat<Any?>(Condition.enabled.actualValue(driver, elementWithEnabled(false)))
            .isEqualTo("disabled")
    }

    @Test
    fun elementDisabled() = runBlockingTest {
        Assertions.assertThat(Condition.disabled.apply(driver, elementWithEnabled(false))).isTrue()
        Assertions.assertThat(Condition.disabled.apply(driver, elementWithEnabled(true))).isFalse()
    }

    @Test
    fun elementDisabledActualValue() = runBlockingTest {
        Assertions.assertThat<Any?>(Condition.disabled.actualValue(driver, elementWithEnabled(true)))
            .isEqualTo("enabled")
        Assertions.assertThat<Any?>(Condition.disabled.actualValue(driver, elementWithEnabled(false)))
            .isEqualTo("disabled")
    }

    @Test
    fun elementSelected() = runBlockingTest {
        Assertions.assertThat(Condition.selected.apply(driver, elementWithSelected(true))).isTrue()
        Assertions.assertThat(Condition.selected.apply(driver, elementWithSelected(false))).isFalse()
    }

    private fun elementWithSelected(isSelected: Boolean): WebElement {
        val element = Mockito.mock(WebElement::class.java)
        Mockito.`when`(element.isSelected).thenReturn(isSelected)
        return element
    }

    @Test
    fun elementSelectedActualValue() = runBlockingTest {
        Assertions.assertThat<Any?>(Condition.selected.actualValue(driver, elementWithSelected(true))).isEqualTo("true")
        Assertions.assertThat<Any?>(Condition.selected.actualValue(driver, elementWithSelected(false)))
            .isEqualTo("false")
    }

    @Test
    fun elementChecked() = runBlockingTest {
        Assertions.assertThat(Condition.checked.apply(driver, elementWithSelected(true))).isTrue()
        Assertions.assertThat(Condition.checked.apply(driver, elementWithSelected(false))).isFalse()
    }

    @Test
    fun elementCheckedActualValue() = runBlockingTest {
        Assertions.assertThat<Any?>(Condition.checked.actualValue(driver, elementWithSelected(true))).isEqualTo("true")
        Assertions.assertThat<Any?>(Condition.checked.actualValue(driver, elementWithSelected(false)))
            .isEqualTo("false")
    }

    @Test
    fun elementNotCondition() = runBlockingTest {
        Assertions.assertThat(not(Condition.checked).apply(driver, elementWithSelected(false))).isTrue()
        Assertions.assertThat(not(Condition.checked).apply(driver, elementWithSelected(true))).isFalse()
    }

    @Test
    fun elementNotConditionActualValue() = runBlockingTest {
        Assertions.assertThat<Any?>(not(Condition.checked).actualValue(driver, elementWithSelected(false)))
            .isEqualTo("false")
        Assertions.assertThat<Any?>(not(Condition.checked).actualValue(driver, elementWithSelected(true)))
            .isEqualTo("true")
    }

    @Test
    fun elementAndCondition() = runBlockingTest {
        val element = mockElement(true, "text")
        Assertions.assertThat(
            and("selected with text", be(Condition.selected), have(text("text"))).apply(
                driver,
                element
            )
        )
            .isTrue()
        Assertions.assertThat(
            and("selected with text", not(be(Condition.selected)), have(text("text")))
                .apply(driver, element)
        )
            .isFalse()
        Assertions.assertThat(
            and("selected with text", be(Condition.selected), have(text("incorrect"))).apply(
                driver,
                element
            )
        )
            .isFalse()
    }

    @Test
    fun elementAndConditionActualValue() = runBlockingTest {
        val element = mockElement(false, "text")
        val condition = and("selected with text", be(Condition.selected), have(text("text")))
        Assertions.assertThat(condition.actualValue(driver, element)).isNullOrEmpty()
        Assertions.assertThat(condition.apply(driver, element)).isFalse()
        Assertions.assertThat<Any?>(condition.actualValue(driver, element)).isEqualTo("false")
    }

    @Test
    fun elementAndConditionToString() = runBlockingTest {
        val element = mockElement(false, "text")
        val condition = and("selected with text", be(Condition.selected), have(text("text")))
        Assertions.assertThat(condition).hasToString("selected with text: be selected and have text 'text'")
        Assertions.assertThat(condition.apply(driver, element)).isFalse()
        Assertions.assertThat(condition).hasToString("selected with text: be selected and have text 'text'")
    }

    @Test
    fun elementOrCondition() = runBlockingTest {
        val element = mockElement(false, "text")
        Mockito.`when`(element.isDisplayed).thenReturn(true)
        Assertions.assertThat(
            or("Visible, not Selected", Condition.visible, Condition.checked).apply(
                driver,
                element
            )
        ).isTrue()
        Assertions.assertThat(
            or("Selected with text", Condition.checked, text("incorrect")).apply(
                driver,
                element
            )
        ).isFalse()
    }

    @Test
    fun elementOrConditionActualValue() = runBlockingTest {
        val element = mockElement(false, "text")
        val condition = or("selected with text", be(Condition.selected), have(text("text")))
        Assertions.assertThat<Any?>(condition.actualValue(driver, element)).isEqualTo("false, null")
        Assertions.assertThat(condition.apply(driver, element)).isTrue()
    }

    @Test
    fun elementOrConditionToString() = runBlockingTest {
        val element = mockElement(false, "text")
        val condition = or("selected with text", be(Condition.selected), have(text("text")))
        Assertions.assertThat(condition).hasToString("selected with text: be selected or have text 'text'")
        Assertions.assertThat(condition.apply(driver, element)).isTrue()
    }

    @Test
    fun conditionBe() {
        val condition = be(Condition.visible)
        Assertions.assertThat(condition).hasToString("be visible")
    }

    @Test
    fun conditionHave() {
        val condition = have(attribute("name"))
        Assertions.assertThat(condition).hasToString("have attribute name")
    }

    @Test
    fun conditionApplyNull() {
        val condition = attribute("name")
        Assertions.assertThat(condition.applyNull()).isFalse
    }

    @Test
    fun conditionToString() {
        val condition = attribute("name").because("it's awesome")
        Assertions.assertThat(condition).hasToString("attribute name (because it's awesome)")
    }

    @Test
    fun shouldHaveText_doesNotAccept_emptyString() {
        assertThat { text("") }.isFailure()
            .all {
                isInstanceOf(IllegalArgumentException::class.java)
                hasMessage("Argument must not be null or empty string. Use $.shouldBe(empty) or $.shouldHave(exactText(\"\").")
            }
    }

    @Test
    fun shouldHaveText_accepts_blankNonEmptyString() {
        text(" ")
        text("  ")
        text("\t")
        text("\n")
    }

    private fun mockElement(isSelected: Boolean, text: String): WebElement {
        val element = Mockito.mock(WebElement::class.java)
        Mockito.`when`(element.isSelected).thenReturn(isSelected)
        Mockito.`when`(element.text).thenReturn(text)
        return element
    }
}
