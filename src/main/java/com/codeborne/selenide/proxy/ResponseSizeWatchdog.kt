package com.codeborne.selenide.proxy

import com.browserup.bup.filters.ResponseFilter
import com.browserup.bup.util.HttpMessageContents
import com.browserup.bup.util.HttpMessageInfo
import io.netty.handler.codec.http.HttpResponse
import org.slf4j.LoggerFactory
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class ResponseSizeWatchdog : ResponseFilter {
    var threshold = 2 * 1024 * 1024 // 2 MB
    override fun filterResponse(response: HttpResponse, contents: HttpMessageContents, messageInfo: HttpMessageInfo) {
        if (contents.binaryContents.size > threshold) {
            log.warn("Too large response {}: {} bytes", messageInfo.url, contents.binaryContents.size)
            log.trace("Response content: {}", contents.textContents)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ResponseSizeWatchdog::class.java)
    }
}
