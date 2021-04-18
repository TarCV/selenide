package com.codeborne.selenide.impl

import org.apache.hc.client5.http.cookie.BasicCookieStore
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie
import org.openqa.selenium.Cookie
import org.openqa.selenium.WebDriver

internal class WebdriverCookieStore(webDriver: WebDriver) : BasicCookieStore() {
    private fun duplicateCookie(seleniumCookie: Cookie): BasicClientCookie {
        val duplicateCookie = BasicClientCookie(seleniumCookie.name, seleniumCookie.value)
        duplicateCookie.domain = seleniumCookie.domain
        duplicateCookie.setAttribute(org.apache.hc.client5.http.cookie.Cookie.DOMAIN_ATTR, seleniumCookie.domain)
        duplicateCookie.isSecure = seleniumCookie.isSecure
        duplicateCookie.expiryDate = seleniumCookie.expiry
        duplicateCookie.path = seleniumCookie.path
        return duplicateCookie
    }

    init {
        val seleniumCookieSet = webDriver.manage().cookies
        for (seleniumCookie in seleniumCookieSet) {
            addCookie(duplicateCookie(seleniumCookie))
        }
    }
}
