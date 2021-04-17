package com.codeborne.selenide.proxy

import com.browserup.bup.filters.RequestFilter
import com.browserup.bup.util.HttpMessageContents
import com.browserup.bup.util.HttpMessageInfo
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponse
import org.slf4j.LoggerFactory
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class RequestSizeWatchdog : RequestFilter {
    var threshold = 2 * 1024 * 1024 // 2 MB
    override fun filterRequest(
        request: HttpRequest,
        contents: HttpMessageContents,
        messageInfo: HttpMessageInfo
    ): HttpResponse? {
        if (contents.binaryContents.size > threshold) {
            log.warn("Too large request {}: {} bytes", messageInfo.url, contents.binaryContents.size)
            log.trace("Request content: {}", contents.textContents)
        }
        return null
    }

    companion object {
        private val log = LoggerFactory.getLogger(RequestSizeWatchdog::class.java)
    }
}
