package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.CommandSync
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.WebElementSource
import okio.ExperimentalFileSystem
import kotlin.reflect.KProperty1

@ExperimentalFileSystem
open class Commands {
    open val find: CommandSync<SelenideElement>
    open val `$`: CommandSync<SelenideElement>
    open val `$x`: CommandSync<SelenideElement>
    open val findAll: CommandSync<ElementsCollection>
    open val `$$`: CommandSync<ElementsCollection>
    open val `$$x`: CommandSync<ElementsCollection>
    open val closest: CommandSync<SelenideElement>
    open val parent: CommandSync<SelenideElement>
    open val sibling: CommandSync<SelenideElement>
    open val preceding: CommandSync<SelenideElement>
    open val lastChild: CommandSync<SelenideElement>
    open val click: Command<Unit>
    open val contextClick: Command<SelenideElement>
    open val doubleClick: Command<SelenideElement>
    open val selectRadio: Command<SelenideElement>
    open val setSelected: Command<SelenideElement>
    open val setValue: Command<SelenideElement>
    open val `val`: Command<SelenideElement>
    open val getVal: Command<String?>
    open val append: Command<SelenideElement>
    open val attr: Command<String?>
    open val getAttribute: Command<String?>
    open val getCssValue: Command<String>
    open val `data`: Command<String?>
    open val exists: Command<Boolean>
    open val getOwnText: Command<String>
    open val innerText: Command<String>
    open val innerHtml: Command<String>
    open val has: Command<Boolean>
    open val `is`: Command<Boolean>
    open val isDisplayed: Command<Boolean>
    open val isImage: Command<Boolean>
    open val getText: Command<String>
    open val name: Command<String?>
    open val text: Command<*>
    open val getValue: Command<String?>
    open val pseudo: Command<String>
    open val getSelectedOption: Command<SelenideElement>
    open val getSelectedOptions: Command<ElementsCollection>
    open val getSelectedText: Command<String>
    open val getSelectedValue: Command<String?>
    open val selectOption: Command<Unit>
    open val selectOptionContainingText: Command<Unit>
    open val selectOptionByValue: Command<Unit>
    open val pressEnter: Command<SelenideElement>
    open val pressEscape: Command<SelenideElement>
    open val pressTab: Command<SelenideElement>
    open val dragAndDropTo: Command<SelenideElement>
    open val hover: Command<SelenideElement>
    open val scrollTo: Command<SelenideElement>
    open val scrollIntoView: Command<SelenideElement>
    open val should: SoftAssertionCommand
    open val shouldHave: SoftAssertionCommand
    open val shouldBe: SoftAssertionCommand
    open val waitUntil: SoftAssertionCommand
    open val shouldNot: SoftAssertionCommand
    open val shouldNotHave: SoftAssertionCommand
    open val shouldNotBe: SoftAssertionCommand
    open val waitWhile: SoftAssertionCommand
    open val download: Command<*>
    open val uploadFile: Command<*>
    open val `as`: CommandSync<SelenideElement>
    open val getAlias: CommandSync<String?>
    open val toString: Command<String>
    open val toWebElement: Command<org.openqa.selenium.WebElement>
    open val getWrappedElement: Command<org.openqa.selenium.WebElement>
    open val getSearchCriteria: CommandSync<String>
    open val execute: Command<Any?>

    suspend fun <T> doExecute(
        proxy: Any, webElementSource: WebElementSource, method: KProperty1<Commands, Command<T>>,
        args: Array<out Any>
    ): T {
        val command: Command<T> = method.get(this)
        return command.execute((proxy as SelenideElement), webElementSource, args)
    }

    fun <T> doExecute(
        proxy: Any, webElementSource: WebElementSource, method: KProperty1<Commands, CommandSync<T>>,
        args: Array<out Any>
    ): T {
        val command: CommandSync<T> = method.get(this)
        return command.execute((proxy as SelenideElement), webElementSource, args)
    }

    companion object {
        val instance: Commands by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Plugins.commands
        }
    }

    init {
        `find` = Find()
        `$` = Find()
        `$x` = FindByXpath()
        `findAll` = FindAll()
        `$$` = FindAll()
        `$$x` = FindAllByXpath()
        `closest` = GetClosest()
        `parent` = GetParent()
        `sibling` = GetSibling()
        `preceding` = GetPreceding()
        `lastChild` = GetLastChild()
        `click` = Click()
        `contextClick` = ContextClick()
        `doubleClick` = DoubleClick()
        `selectRadio` = SelectRadio()
        `setSelected` = SetSelected()
        `setValue` = SetValue()
        `val` = SetVal()
        getVal = GetVal()
        `append` = Append()
        `attr` = GetAttribute()
        `getAttribute` = GetAttribute()
        `getCssValue` = GetCssValue()
        `data` = GetDataAttribute()
        `exists` = Exists()
        `getOwnText` = GetOwnText()
        `innerText` = GetInnerText()
        `innerHtml` = GetInnerHtml()
        `has` = Matches()
        `is` = Matches()
        `isDisplayed` = IsDisplayed()
        `isImage` = IsImage()
        `getText` = GetText()
        `name` = GetName()
        `text` = GetText()
        `getValue` = GetValue()
        `pseudo` = GetPseudoValue()
        `getSelectedOption` = GetSelectedOption()
        `getSelectedOptions` = GetSelectedOptions()
        `getSelectedText` = GetSelectedText()
        `getSelectedValue` = GetSelectedValue()
        `selectOption` = SelectOptionByTextOrIndex()
        `selectOptionContainingText` = SelectOptionContainingText()
        `selectOptionByValue` = SelectOptionByValue()
        `pressEnter` = PressEnter()
        `pressEscape` = PressEscape()
        `pressTab` = PressTab()
        `dragAndDropTo` = DragAndDropTo()
        `hover` = Hover()
        `scrollTo` = ScrollTo()
        `scrollIntoView` = ScrollIntoView()
        `should` = Should()
        `shouldHave` = ShouldHave()
        `shouldBe` = ShouldBe()
        `waitUntil` = WaitUntil()
        `shouldNot` = ShouldNot()
        `shouldNotHave` = ShouldNotHave()
        `shouldNotBe` = ShouldNotBe()
        `waitWhile` = WaitWhile()
        `download` = DownloadFile()
        `uploadFile` = UploadFile()
        `as` = As()
        `getAlias` = GetAlias()
        `toString` = ToString()
        `toWebElement` = ToWebElement()
        `getWrappedElement` = GetWrappedElement()
        `getSearchCriteria` = GetSearchCriteria()
        `execute` = Execute<Any>()
        // TODO:        add("uploadFromClasspath", UploadFileFromClasspath())
        // TODO:        add("screenshot", TakeScreenshot())
        // TODO:        add("screenshotAsImage", TakeScreenshotAsImage())
    }
}
