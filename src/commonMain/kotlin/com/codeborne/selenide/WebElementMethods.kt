package com.codeborne.selenide

interface WebElementMethods: org.openqa.selenium.WebElement, org.openqa.selenium.WrapsDriver,
    /*org.openqa.selenium.internal.WrapsElement, */org.openqa.selenium.interactions.Locatable,
    org.openqa.selenium.TakesScreenshot, org.openqa.selenium.internal.HasIdentity
