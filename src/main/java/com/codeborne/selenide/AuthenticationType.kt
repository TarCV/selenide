package com.codeborne.selenide

import javax.annotation.ParametersAreNonnullByDefault

/**
 * Authentication schemes.
 *
 * @see [Web HTTP reference](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication.Authentication_schemes)
 */
@ParametersAreNonnullByDefault
enum class AuthenticationType(
    val value: String
) {
    BASIC("Basic"), BEARER("Bearer"), DIGEST("Digest"), HOBA("HOBA"), MUTUAL("Mutual"), AWS4_HMAC_SHA256("AWS4-HMAC-SHA256");

}
