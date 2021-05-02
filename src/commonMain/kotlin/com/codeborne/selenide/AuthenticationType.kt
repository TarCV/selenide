package com.codeborne.selenide

/**
 * Authentication schemes.
 *
 * @see [Web HTTP reference](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication.Authentication_schemes)
 */
enum class AuthenticationType(
    val value: String
) {
    BASIC("Basic"), BEARER("Bearer"), DIGEST("Digest"), HOBA("HOBA"), MUTUAL("Mutual"), AWS4_HMAC_SHA256("AWS4-HMAC-SHA256");

}
