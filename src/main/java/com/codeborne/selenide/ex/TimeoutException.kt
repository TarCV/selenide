package com.codeborne.selenide.ex

import java.net.SocketTimeoutException
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class TimeoutException(message: String?, cause: SocketTimeoutException?) : RuntimeException(message, cause)
