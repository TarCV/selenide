package com.codeborne.selenide.proxy

import javax.annotation.ParametersAreNonnullByDefault
import kotlin.jvm.JvmOverloads
import com.browserup.bup.BrowserUpProxy
import com.browserup.bup.filters.RequestFilter
import java.util.HashMap
import com.browserup.bup.filters.ResponseFilter
import com.browserup.bup.client.ClientUtil
import com.codeborne.selenide.Config
import org.apache.commons.lang3.StringUtils
import org.openqa.selenium.Proxy
import java.lang.IllegalStateException
import java.net.InetSocketAddress
import java.util.Arrays
import javax.annotation.CheckReturnValue

/**
 * Selenide own proxy server to intercept server responses
 *
 * It holds map of request and response filters by name.
 */
@ParametersAreNonnullByDefault
class SelenideProxyServer
/**
 * Create server
 * Note that server is not started nor activated yet.
 *
 * @param outsideProxy another proxy server used by test author for his own need (can be null)
 */ @JvmOverloads constructor(
    private val config: Config, private val outsideProxy: Proxy?,
    private val inetAddressResolver: InetAddressResolver = InetAddressResolver(),
    /**
     * Method return current instance of browser up proxy
     *
     * @return browser up proxy instance
     */
    @get:CheckReturnValue
    val proxy: BrowserUpProxy = BrowserUpProxyServerUnlimited()
) {
    private val requestFilters: MutableMap<String, RequestFilter> = HashMap()
    private val responseFilters: MutableMap<String, ResponseFilter> = HashMap()
    private var port = 0

    /**
     * Start the server
     *
     * It automatically adds one response filter "download" that can intercept downloaded files.
     */
    fun start() {
        proxy.setTrustAllServers(true)
        if (outsideProxy != null) {
            proxy.chainedProxy = getProxyAddress(outsideProxy)
            val noProxy = outsideProxy.noProxy
            if (noProxy != null) {
                val noProxyHosts = Arrays.asList(*noProxy.split(",").toTypedArray())
                proxy.setChainedProxyNonProxyHosts(noProxyHosts)
            }
        }
        addRequestFilter("authentication", AuthenticationFilter())
        addRequestFilter("requestSizeWatchdog", RequestSizeWatchdog())
        addResponseFilter("responseSizeWatchdog", ResponseSizeWatchdog())
        addResponseFilter("download", FileDownloadFilter(config))
        proxy.start(config.proxyPort())
        port = proxy.port
    }

    @get:CheckReturnValue
    val isStarted: Boolean
        get() = proxy.isStarted

    /**
     * Add a custom request filter which allows to track/modify all requests from browser to server
     *
     * @param name unique name of filter
     * @param requestFilter the filter
     */
    fun addRequestFilter(name: String, requestFilter: RequestFilter) {
        require(!isRequestFilterAdded(name)) { "Duplicate request filter: $name" }
        proxy.addRequestFilter(requestFilter)
        requestFilters[name] = requestFilter
    }

    private fun isRequestFilterAdded(name: String): Boolean {
        return requestFilters.containsKey(name)
    }

    /**
     * Add a custom response filter which allows to track/modify all server responses to browser
     *
     * @param name unique name of filter
     * @param responseFilter the filter
     */
    fun addResponseFilter(name: String, responseFilter: ResponseFilter) {
        require(!responseFilters.containsKey(name)) { "Duplicate response filter: $name" }
        proxy.addResponseFilter(responseFilter)
        responseFilters[name] = responseFilter
    }

    /**
     * Converts this proxy to a "selenium" proxy that can be used by webdriver
     */
    @CheckReturnValue
    fun createSeleniumProxy(): Proxy {
        return if (StringUtils.isEmpty(config.proxyHost())) ClientUtil.createSeleniumProxy(proxy) else ClientUtil.createSeleniumProxy(
            proxy, inetAddressResolver.getInetAddressByName(config.proxyHost())
        )
    }

    /**
     * Stop the server
     */
    fun shutdown() {
        if (proxy.isStarted) {
            try {
                proxy.abort()
            } catch (ignore: IllegalStateException) {
            }
        }
    }

    @CheckReturnValue
    override fun toString(): String {
        return String.format("Selenide proxy server: %s", port)
    }

    /**
     * Get request filter by name
     */
    @CheckReturnValue
    fun <T : RequestFilter?> requestFilter(name: String): T? {
        return requestFilters[name] as T?
    }

    /**
     * Get response filter by name
     *
     * By default, the only one filter "download" is available.
     */
    @CheckReturnValue
    fun <T : ResponseFilter?> responseFilter(name: String): T? {
        return responseFilters[name] as T?
    }

    companion object {
        @JvmStatic
        fun getProxyAddress(proxy: Proxy): InetSocketAddress {
            val httpProxy = proxy.httpProxy
            val host = httpProxy.replaceFirst("(.*):.*".toRegex(), "$1")
            val port = httpProxy.replaceFirst(".*:(.*)".toRegex(), "$1")
            return InetSocketAddress(host, port.toInt())
        }
    }
}
