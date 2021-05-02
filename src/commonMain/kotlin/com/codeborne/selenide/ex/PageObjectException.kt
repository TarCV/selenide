package com.codeborne.selenide.ex

import support.reflect.ReflectiveOperationException

class PageObjectException(message: String?, cause: ReflectiveOperationException?) : RuntimeException(message, cause)
