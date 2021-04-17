package com.codeborne.selenide

import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Credentials(val login: String, val password: String) {
    /**
     * The resulting string is base64 encoded (YWxhZGRpbjpvcGVuc2VzYW1l).
     *
     * @return encoded string
     */
    @CheckReturnValue
    fun encode(): String {
        val credentialsBytes = combine().toByteArray(StandardCharsets.UTF_8)
        return Base64.getEncoder().encodeToString(credentialsBytes)
    }

    private fun combine(): String {
        return String.format("%s:%s", login, password)
    }

    @CheckReturnValue
    override fun toString(): String {
        return combine()
    }
}
