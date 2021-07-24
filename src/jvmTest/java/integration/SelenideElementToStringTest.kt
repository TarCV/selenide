package integration

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Condition.Companion.value
import com.codeborne.selenide.Selectors.byText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.openqa.selenium.By

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class SelenideElementToStringTest : ITest() {
    @Test
    fun describeMethodShowsElementDetails() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
        Assertions.assertThat(`$`("h1").describe())
            .isEqualTo("<h1>Page with selects</h1>")
        Assertions.assertThat(`$`("h2").describe())
            .isEqualTo("<h2>Dropdown list</h2>")
        Assertions.assertThat(`$`(By.name("rememberMe")).describe())
            .isEqualTo("<input name=\"rememberMe\" type=\"checkbox\" value=\"on\"></input>")
        Assertions.assertThat(`$`(By.name("domain")).find("option").describe())
            .isEqualTo("<option data-mailserverid=\"111\" value=\"livemail.ru\" selected:true>@livemail.ru</option>")
        Assertions.assertThat(`$`(byText("Want to see ajax in action?")).describe())
            .contains("<a href=")
        Assertions.assertThat(`$`(byText("Want to see ajax in action?")).describe())
            .contains(">Want to see ajax in action?</a>")
    }

    @Test
    fun describeShowsAllAttributesButStyleSortedAlphabetically() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
        Assertions.assertThat(`$`("#gopher").describe())
            .isEqualTo(
                "<div class=\"invisible-with-multiple-attributes\" " +
                        "data-animal-id=\"111\" id=\"gopher\" ng-class=\"widget\" ng-click=\"none\" " +
                        "onchange=\"console.log(this);\" onclick=\"void(0);\" placeholder=\"Животное\" " +
                        "displayed:false></div>"
            )
    }

    @Test
    fun describeShowsValueAttributeThatHasBeenUpdatedDynamically() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
        `$`("#age").clear()
        `$`("#age").sendKeys("21")
        Assertions.assertThat(`$`("#age").describe())
            .isEqualTo("<input id=\"age\" name=\"age\" type=\"text\" value=\"21\"></input>")
    }

    @Test
    fun describeShowsCurrentValue_evenIfItWasDynamicallyChanged() = runBlockingTest {
        openFile("page_with_double_clickable_button.html")
        Assertions.assertThat(`$`("#double-clickable-button").describe())
            .contains("value=\"double click me\"")
        `$`("#double-clickable-button").shouldBe(Condition.enabled).doubleClick()
        `$`("#double-clickable-button").shouldHave(value("do not click me anymore"))
        Assertions.assertThat(`$`("#double-clickable-button").describe())
            .contains("value=\"do not click me anymore\"")
    }
}
