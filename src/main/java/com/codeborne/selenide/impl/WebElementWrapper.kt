package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.Plugins.inject
import org.openqa.selenium.WebElement
import java.lang.reflect.Proxy
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class WebElementWrapper(
    private val driver: Driver, @get:CheckReturnValue
    override val webElement: WebElement
) : WebElementSource() {
    private val describe = inject(ElementDescriber::class.java)

    @get:CheckReturnValue
    override val searchCriteria: String
        get() = describe.briefly(driver, webElement)

    @CheckReturnValue
    override fun toString(): String {
        return alias.getOrElse { describe.fully(driver(), webElement) }
    }

    @CheckReturnValue
    override fun driver(): Driver {
        return driver
    }

    companion object {
        fun wrap(driver: Driver, element: WebElement): SelenideElement {
            return if (element is SelenideElement) element else (Proxy.newProxyInstance(
                element.javaClass.classLoader, arrayOf<Class<*>>(SelenideElement::class.java),
                SelenideElementProxy(WebElementWrapper(driver, element))
            ) as SelenideElement)
        }
    }
}
