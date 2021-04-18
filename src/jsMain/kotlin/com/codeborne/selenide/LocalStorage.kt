package com.codeborne.selenide


class LocalStorage internal constructor(driver: Driver) : JSStorage(driver, "localStorage")
