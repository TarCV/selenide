package com.codeborne.selenide
import com.codeborne.selenide.impl.CollectionSource
import org.openqa.selenium.WebElement
import java.util.Arrays.asList
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.stub

@Suppress("UsePropertyAccessSyntax")
object Mocks {
    @JvmStatic
    fun mockElement(text: String): SelenideElement {
        return mockElement("div", text)
    }

    @JvmStatic
    fun mockElement( tag:String,  text:String): SelenideElement
    {
        val element = mock(SelenideElement::class.java)
        `when`(element.getTagName()).thenReturn(tag)
        `when`(element.getText()).thenReturn(text)
        return element
    }

    @JvmStatic
    fun mockWebElement(tag: String, text: String): WebElement {
        val element = mock (WebElement::class.java)
        `when`(element.getTagName()).thenReturn(tag)
        `when`(element.getText()).thenReturn(text)
        `when`(element.isDisplayed()).thenReturn(true)
        return element
    }

    @JvmStatic
    fun mockCollection(description: String, vararg elements: WebElement): CollectionSource {
        val driver = mock (Driver::class.java)
        `when`(driver.config()).thenReturn(SelenideConfig())
        val collection = mock (CollectionSource::class.java)
        `when`(collection.driver()).thenReturn(driver)
        collection.stub {
            onBlocking { description() }.thenReturn(description)
            onBlocking { getElements() }.thenReturn(elements.toList())
            for (i in elements.indices) {
                onBlocking {collection.getElement(i) }.thenReturn(elements[i])
            }
        }
        return collection
    }
}
