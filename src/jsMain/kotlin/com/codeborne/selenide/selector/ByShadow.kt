package com.codeborne.selenide.selector

import com.codeborne.selenide.filecontent.findInShadowRootsJs
import com.codeborne.selenide.impl.Cleanup
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptException
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement
import org.openqa.selenium.WrapsDriver

object ByShadow {

    /**
     * Find target elements inside shadow-root that attached to shadow-host.
     * <br></br> Supports inner shadow-hosts.
     *
     *
     * <br></br> For example: shadow-host &gt; inner-shadow-host &gt; target-element
     * (each shadow-host must be specified explicitly).
     *
     * @param target           CSS expression of target element
     * @param shadowHost       CSS expression of the shadow-host with attached shadow-root
     * @param innerShadowHosts subsequent inner shadow-hosts
     * @return A By which locates elements by CSS inside shadow-root.
     */
    fun cssSelector(target: String, shadowHost: String, vararg innerShadowHosts: String): By {
        return ByShadowCss(target, shadowHost, *innerShadowHosts)
    }

    class ByShadowCss internal constructor(target: String, shadowHost: String, vararg innerShadowHosts: String) : By {
        private val shadowHostsChain: MutableList<String>
        private val target: String
        override fun findElement(context: SearchContext): WebElement {
            val found = findElements(context)
            if (found.isEmpty()) {
                throw NoSuchElementException("Cannot locate an element " + target + " in shadow roots " + describeShadowRoots())
            }
            return found[0]
        }
        override fun findElements(context: SearchContext): List<WebElement> {
            return try {
                if (context is JavascriptExecutor) {
                    findElementsInDocument(context as JavascriptExecutor)
                } else {
                    findElementsInElement(context)
                }
            } catch (e: JavascriptException) {
                throw NoSuchElementException(Cleanup.of.webdriverExceptionMessage(e))
            }
        }

        private fun findElementsInDocument(context: JavascriptExecutor): List<WebElement> {
            return context.executeScript(
                "return $findInShadowRootsJs(arguments[0], arguments[1])", target, shadowHostsChain
            ) as List<WebElement>
        }

        private fun findElementsInElement(context: SearchContext): List<WebElement> {
            val js = (context as WrapsDriver).wrappedDriver as JavascriptExecutor
            return js.executeScript(
                "return $findInShadowRootsJs(arguments[0], arguments[1], arguments[2])",
                target,
                shadowHostsChain,
                context
            ) as List<WebElement>
        }
        override fun toString(): String {
            return "By.cssSelector: " + describeShadowRoots() + " -> " + target
        }
        private fun describeShadowRoots(): String {
            return shadowHostsChain.joinToString(" -> ")
        }

        init {
            shadowHostsChain = ArrayList(1 + innerShadowHosts.size)
            shadowHostsChain.add(shadowHost)
            shadowHostsChain.addAll(listOf(*innerShadowHosts))
            this.target = target
        }
    }
}
