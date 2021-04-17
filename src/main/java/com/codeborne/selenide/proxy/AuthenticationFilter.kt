package com.codeborne.selenide.proxy

import com.browserup.bup.filters.RequestFilter
import com.browserup.bup.util.HttpMessageContents
import com.browserup.bup.util.HttpMessageInfo
import com.codeborne.selenide.AuthenticationType
import com.codeborne.selenide.Credentials
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class AuthenticationFilter : RequestFilter {
    private var authenticationType: AuthenticationType? = null
    private var credentials: Credentials? = null
    override fun filterRequest(
        request: HttpRequest,
        contents: HttpMessageContents,
        messageInfo: HttpMessageInfo
    ): HttpResponse? {
        authenticationType?.let {  authenticationType ->
            val authorization = String.format("%s %s", authenticationType.value, credentials!!.encode())
            val headers = request.headers()
            headers.add("Authorization", authorization)
            headers.add("Proxy-Authorization", authorization)
        }
        return null
    }

    fun setAuthentication(authenticationType: AuthenticationType?, credentials: Credentials?) {
        this.authenticationType = authenticationType
        this.credentials = credentials
    }

    fun removeAuthentication() {
        setAuthentication(null, null)
    }
}
