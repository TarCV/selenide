package com.codeborne.selenide.impl

import com.codeborne.selenide.SelenideDriver

/**
 * A `SelenideDriver` implementation which uses thread-local
 * webdriver and proxy from `WebDriverRunner`.
 *
 * @see com.codeborne.selenide.impl.StaticConfig
 *
 * @see com.codeborne.selenide.impl.StaticDriver
 */
class ThreadLocalSelenideDriver : SelenideDriver(StaticConfig(), StaticDriver())
