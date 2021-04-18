package com.codeborne.selenide.proxy

import com.browserup.bup.BrowserUpProxyServer
import com.browserup.bup.filters.RequestFilter
import com.browserup.bup.filters.RequestFilterAdapter
import com.browserup.bup.filters.ResponseFilter
import com.browserup.bup.filters.ResponseFilterAdapter

/**
 * By default, BrowserUpProxyServer doesn't allow requests/responses bugger than 2 MB.
 * We need this class to enable bigger sizes.
 */
internal class BrowserUpProxyServerUnlimited : BrowserUpProxyServer() {
    override fun addRequestFilter(filter: RequestFilter) {
        addFirstHttpFilterFactory(RequestFilterAdapter.FilterSource(filter, maxSize))
    }

    override fun addResponseFilter(filter: ResponseFilter) {
        addLastHttpFilterFactory(ResponseFilterAdapter.FilterSource(filter, maxSize))
    }

    companion object {
        private const val maxSize = 64 * 1024 * 1024 // 64 MB
    }
}
