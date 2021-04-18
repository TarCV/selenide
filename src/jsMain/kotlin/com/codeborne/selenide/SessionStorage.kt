package com.codeborne.selenide


class SessionStorage internal constructor(driver: Driver) : JSStorage(driver, "sessionStorage")
