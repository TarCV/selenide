package com.codeborne.selenide.ex

import java.lang.ReflectiveOperationException

class PageObjectException(message: String?, cause: ReflectiveOperationException?) : RuntimeException(message, cause)
