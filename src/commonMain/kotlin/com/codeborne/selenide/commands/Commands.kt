package com.codeborne.selenide.commands

import co.touchlab.stately.collections.IsoMutableMap
import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.Plugins
import com.codeborne.selenide.impl.WebElementSource
import okio.ExperimentalFileSystem
import kotlin.jvm.JvmStatic

@ExperimentalFileSystem
open class Commands protected constructor() {
    private val commands: MutableMap<String, Command<*>> = IsoMutableMap()
    private fun addTechnicalCommands() {
        add("as", As())
        add("getAlias", GetAlias())
        add("toString", ToString())
        add("toWebElement", ToWebElement())
        add("getWrappedElement", GetWrappedElement())
// TODO:        add("screenshot", TakeScreenshot())
// TODO:        add("screenshotAsImage", TakeScreenshotAsImage())
        add("getSearchCriteria", GetSearchCriteria())
        add("execute", Execute<Any>())
    }

    private fun addActionsCommands() {
        add("dragAndDropTo", DragAndDropTo())
        add("hover", Hover())
        add("scrollTo", ScrollTo())
        add("scrollIntoView", ScrollIntoView())
    }

    private fun addInfoCommands() {
        add("attr", GetAttribute())
        add("getAttribute", GetAttribute())
        add("getCssValue", GetCssValue())
        add("data", GetDataAttribute())
        add("exists", Exists())
        add("getOwnText", GetOwnText())
        add("innerText", GetInnerText())
        add("innerHtml", GetInnerHtml())
        add("has", Matches())
        add("is", Matches())
        add("isDisplayed", IsDisplayed())
        add("isImage", IsImage())
        add("getText", GetText())
        add("name", GetName())
        add("text", GetText())
        add("getValue", GetValue())
        add("pseudo", GetPseudoValue())
    }

    private fun addClickCommands() {
        add("click", Click())
        add("contextClick", ContextClick())
        add("doubleClick", DoubleClick())
    }

    private fun addModifyCommands() {
        add("selectRadio", SelectRadio())
        add("setSelected", SetSelected())
        add("setValue", SetValue())
        add("val", Val())
        add("append", Append())
    }

    private fun addFindCommands() {
        add("find", Find())
        add("$", Find())
        add("\$x", FindByXpath())
        add("findAll", FindAll())
        add("$$", FindAll())
        add("$\$x", FindAllByXpath())
        add("closest", GetClosest())
        add("parent", GetParent())
        add("sibling", GetSibling())
        add("preceding", GetPreceding())
        add("lastChild", GetLastChild())
    }

    private fun addKeyboardCommands() {
        add("pressEnter", PressEnter())
        add("pressEscape", PressEscape())
        add("pressTab", PressTab())
    }

    private fun addSelectCommands() {
        add("getSelectedOption", GetSelectedOption())
        add("getSelectedOptions", GetSelectedOptions())
        add("getSelectedText", GetSelectedText())
        add("getSelectedValue", GetSelectedValue())
        add("selectOption", SelectOptionByTextOrIndex())
        add("selectOptionContainingText", SelectOptionContainingText())
        add("selectOptionByValue", SelectOptionByValue())
    }

    @ExperimentalFileSystem
    private fun addFileCommands() {
        add("download", DownloadFile())
        add("uploadFile", UploadFile())
// TODO:        add("uploadFromClasspath", UploadFileFromClasspath())
    }

    private fun addShouldNotCommands() {
        add("shouldNot", ShouldNot())
        add("shouldNotHave", ShouldNotHave())
        add("shouldNotBe", ShouldNotBe())
        add("waitWhile", WaitWhile())
    }

    private fun addShouldCommands() {
        add("should", Should())
        add("shouldHave", ShouldHave())
        add("shouldBe", ShouldBe())
        add("waitUntil", WaitUntil())
    }

    fun add(method: String, command: Command<*>) {
        commands[method] = command
    }

    suspend fun <T> execute(
      proxy: Any, webElementSource: WebElementSource, methodName: String,
      args: Array<out Any>
    ): T? {
        val command: Command<T> = getCommand(methodName)
        return command.execute((proxy as SelenideElement), webElementSource, args)
    }
    private fun <T> getCommand(methodName: String): Command<T> = synchronized(this) {
        return commands[methodName] as Command<T>?
            ?: throw IllegalArgumentException("Unknown Selenide method: $methodName")
    }

    companion object {
        val instance: Commands by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Plugins.injectA(
              Commands::class
            )
        }
    }

    init {
        addFindCommands()
        addClickCommands()
        addModifyCommands()
        addInfoCommands()
        addSelectCommands()
        addKeyboardCommands()
        addActionsCommands()
        addShouldCommands()
        addShouldNotCommands()
        addFileCommands()
        addTechnicalCommands()
    }
}
