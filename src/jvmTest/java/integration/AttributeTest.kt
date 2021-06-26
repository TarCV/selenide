package integration

import com.codeborne.selenide.Condition.Companion.attribute
import com.codeborne.selenide.Condition.Companion.attributeMatching
import com.codeborne.selenide.Selectors.by
import com.codeborne.selenide.Selectors.byAttribute
import com.codeborne.selenide.Selectors.byText
import com.codeborne.selenide.Selectors.byTitle
import com.codeborne.selenide.Selectors.byValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
class AttributeTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun canVerifyAttributeExistence() = runBlockingTest {
        `$`("#domain-container").shouldHave(attribute("class"))
        `$`("#domain-container").shouldNotHave(attribute("foo"))
    }

    @Test
    fun canVerifyAttributeValue() = runBlockingTest {
        `$`("#domain-container").shouldHave(attribute("class", "container"))
        `$`("#domain-container").shouldNotHave(attribute("class", "kopli"))
    }

    @Test
    fun canVerifyAttributeMatching() = runBlockingTest {
        `$`("#multirowTable").shouldHave(attributeMatching("class", ".*multirow_table.*"))
        `$`("#domain-container").shouldHave(attributeMatching("class", "contain.*"))
        `$`("#domain-container").shouldNotHave(attributeMatching("class", ".*another.*"))
        `$`("#domain-container").shouldNotHave(attributeMatching("foo", ".*contain.*"))
    }

    @Test
    fun canCheckAttributeExistence() = runBlockingTest {
        Assertions.assertThat(`$`("#domain-container").has(attribute("class")))
            .isTrue
        Assertions.assertThat(`$`("#domain-container").has(attribute("foo")))
            .isFalse
    }

    @Test
    fun userCanFindElementByAttribute() = runBlockingTest {
        assertThat(`$`(byAttribute("name", "domain")).tagName)
            .isEqualTo("select")
        Assertions.assertThat(`$`(byAttribute("value", "мыло.ру")).text)
            .isEqualTo("@мыло.ру")
        assertThat(`$`(byAttribute("id", "radioButtons")).tagName)
            .isEqualTo("div")
        assertThat(`$$`(byAttribute("type", "radio")).getSize())
            .isEqualTo(4)
        Assertions.assertThat(`$`(byAttribute("readonly", "readonly")).getAttribute("name"))
            .isEqualTo("username")
        assertThat(`$`(byAttribute("http-equiv", "Content-Type")).tagName)
            .isEqualTo("meta")
    }

    @Test
    fun userCanGetAttr() = runBlockingTest {
        val element = `$`(by("readonly", "readonly"))
        Assertions.assertThat(element.attr("name")).isEqualTo("username")
        Assertions.assertThat(element.attr("value")).isEqualTo("")
        Assertions.assertThat(element.attr("foo")).isNull()
    }

    @Test
    fun userCanGetNameAttribute() = runBlockingTest {
        Assertions.assertThat(`$`(by("readonly", "readonly")).name()).isEqualTo("username")
        Assertions.assertThat(`$`("h2").name()).isNull()
    }

    @Test
    fun userCanGetDataAttributes() = runBlockingTest {
        Assertions.assertThat(`$`(byValue("livemail.ru")).getAttribute("data-mailServerId"))
            .isEqualTo("111")
        Assertions.assertThat(`$`(byValue("livemail.ru")).data("mailServerId"))
            .isEqualTo("111")
        Assertions.assertThat(`$`(byText("@myrambler.ru")).data("mailServerId"))
            .isEqualTo("222A")
        Assertions.assertThat(`$`(byValue("rusmail.ru")).data("mailServerId"))
            .isEqualTo("33333B")
        Assertions.assertThat(`$`(byText("@мыло.ру")).data("mailServerId"))
            .isEqualTo("111АБВГД")
    }

    @Test
    fun userCanSearchElementByDataAttribute() = runBlockingTest {
        Assertions.assertThat(`$`(by("data-mailServerId", "111")).data("mailServerId"))
            .isEqualTo("111")
        Assertions.assertThat(`$`(by("data-mailServerId", "222A")).data("mailServerId"))
            .isEqualTo("222A")
        Assertions.assertThat(`$`(by("data-mailServerId", "33333B")).data("mailServerId"))
            .isEqualTo("33333B")
        Assertions.assertThat(`$`(by("data-mailServerId", "111АБВГД")).data("mailServerId"))
            .isEqualTo("111АБВГД")
    }

    @Test
    fun userCanSearchElementByTitleAttribute() {
        assertThat(`$`(byTitle("Login form")).tagName)
            .isEqualTo("fieldset")
    } /* TODO:
  @Test
  void canCheckHyperReference() {
    $("#non-clickable-element a").shouldHave(href("http://google.com"));
    $("#clickable-element a").shouldHave(href("http://www.yandex.ru"));
    $("#ajax-button").shouldHave(href("long_ajax_request.html"));
    $("#empty h3 a").shouldHave(href("#"));
  }
*/
}
