package com.codeborne.selenide

import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.interactions.Locatable

interface WebElementMethods: org.openqa.selenium.WebElement, /*org.openqa.selenium.WrapsDriver,
    org.openqa.selenium.internal.WrapsElement, */Locatable,
    TakesScreenshot, org.openqa.selenium.internal.HasIdentity
