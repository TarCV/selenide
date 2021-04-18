package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import com.codeborne.selenide.Driver
import com.codeborne.selenide.ex.TimeoutException
import com.codeborne.selenide.files.DownloadedFile
import com.codeborne.selenide.files.FileFilter
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.config.RequestConfig
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.client5.http.socket.ConnectionSocketFactory
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.config.RegistryBuilder
import org.apache.hc.core5.http.protocol.BasicHttpContext
import org.apache.hc.core5.http.protocol.HttpContext
import org.apache.hc.core5.ssl.SSLContextBuilder
import org.apache.hc.core5.ssl.TrustStrategy
import org.openqa.selenium.WebElement
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.URI
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault
import javax.net.ssl.HostnameVerifier

@ParametersAreNonnullByDefault
open class DownloadFileWithHttpRequest internal constructor(private val downloader: Downloader) {
    private val describe = Plugins.inject(
        ElementDescriber::class.java
    )
    protected var ignoreSelfSignedCerts = true
    private val httpHelper = HttpHelper()

    constructor() : this(Downloader()) {}

    @CheckReturnValue
    @Throws(IOException::class)
    fun download(driver: Driver, element: WebElement, timeout: Long, fileFilter: FileFilter): File {
        val fileToDownloadLocation = element.getAttribute("href")
        require(!(fileToDownloadLocation == null || fileToDownloadLocation.trim { it <= ' ' }
            .isEmpty())) { "The element does not have href attribute: " + describe.fully(driver, element) }
        return download(driver, fileToDownloadLocation, timeout, fileFilter)
    }

    @CheckReturnValue
    @Throws(IOException::class)
    fun download(driver: Driver, url: URI, timeout: Long, fileFilter: FileFilter): File {
        return download(driver, url.toASCIIString(), timeout, fileFilter)
    }

    @CheckReturnValue
    @Throws(IOException::class)
    fun download(driver: Driver, relativeOrAbsoluteUrl: String, timeout: Long, fileFilter: FileFilter): File {
        val url = makeAbsoluteUrl(driver.config(), relativeOrAbsoluteUrl)
        val response = executeHttpRequest(driver, url, timeout)
        if (response.code >= 500) {
            throw RuntimeException("Failed to download file $url: $response")
        }
        if (response.code >= 400) {
            throw FileNotFoundException("Failed to download file $url: $response")
        }
        val fileName = getFileName(url, response)
        val downloadedFile = downloader.prepareTargetFile(driver.config(), fileName)
        saveContentToFile(response, downloadedFile)
        if (!fileFilter.match(DownloadedFile(downloadedFile, emptyMap()))) {
            throw FileNotFoundException(
                String.format(
                    "Failed to download file from %s in %d ms. %s;%n actually downloaded: %s",
                    relativeOrAbsoluteUrl, timeout, fileFilter.description(), downloadedFile.absolutePath
                )
            )
        }
        return downloadedFile
    }

    @CheckReturnValue
    fun makeAbsoluteUrl(config: Config, relativeOrAbsoluteUrl: String): String {
        return if (relativeOrAbsoluteUrl.startsWith("/")) config.baseUrl() + relativeOrAbsoluteUrl else relativeOrAbsoluteUrl
    }

    @CheckReturnValue
    @Throws(IOException::class)
    protected fun executeHttpRequest(
        driver: Driver,
        fileToDownloadLocation: String,
        timeout: Long
    ): CloseableHttpResponse {
        val httpClient = if (ignoreSelfSignedCerts) createTrustingHttpClient() else createDefaultHttpClient()
        val httpGet = HttpGet(fileToDownloadLocation)
        configureHttpGet(httpGet, timeout)
        addHttpHeaders(driver, httpGet)
        return try {
            httpClient.execute(httpGet, createHttpContext(driver))
        } catch (timeoutException: SocketTimeoutException) {
            throw TimeoutException("Failed to download $fileToDownloadLocation in $timeout ms.", timeoutException)
        }
    }

    protected fun configureHttpGet(httpGet: HttpGet, timeout: Long) {
        httpGet.config = RequestConfig.custom()
            .setConnectTimeout(timeout, TimeUnit.MILLISECONDS)
            .setConnectionRequestTimeout(timeout, TimeUnit.MILLISECONDS)
            .setResponseTimeout(timeout, TimeUnit.MILLISECONDS)
            .setRedirectsEnabled(true)
            .setCircularRedirectsAllowed(true)
            .setMaxRedirects(20)
            .build()
    }

    @CheckReturnValue
    protected fun createDefaultHttpClient(): CloseableHttpClient {
        return HttpClients.createDefault()
    }

    @ParametersAreNonnullByDefault
    private class TrustAllStrategy : TrustStrategy {
        override fun isTrusted(arg0: Array<X509Certificate>, arg1: String): Boolean {
            return true
        }
    }

    /**
     * configure HttpClient to ignore self-signed certs
     * as described here: http://literatejava.com/networks/ignore-ssl-certificate-errors-apache-httpclient-4-4/
     */
    @CheckReturnValue
    @Throws(IOException::class)
    protected fun createTrustingHttpClient(): CloseableHttpClient {
        return try {
            val builder = HttpClientBuilder.create()
            val sslContext = SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy()).build()
            val hostnameVerifier: HostnameVerifier = NoopHostnameVerifier.INSTANCE
            val sslSocketFactory = SSLConnectionSocketFactory(sslContext, hostnameVerifier)
            val socketFactoryRegistry = RegistryBuilder.create<ConnectionSocketFactory>()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build()
            val connMgr = PoolingHttpClientConnectionManager(socketFactoryRegistry)
            builder.setConnectionManager(connMgr)
            builder.build()
        } catch (e: Exception) {
            throw IOException(e)
        }
    }

    @CheckReturnValue
    fun createHttpContext(driver: Driver): HttpContext {
        val localContext: HttpContext = BasicHttpContext()
        if (driver.hasWebDriverStarted()) {
            localContext.setAttribute(HttpClientContext.COOKIE_STORE, WebdriverCookieStore(driver.webDriver))
        }
        return localContext
    }

    fun addHttpHeaders(driver: Driver, httpGet: HttpGet) {
        if (driver.hasWebDriverStarted()) {
            httpGet.setHeader("User-Agent", driver.userAgent)
        }
    }

    @CheckReturnValue
    fun getFileName(fileToDownloadLocation: String, response: HttpResponse): String {
        for (header in response.headers) {
            val fileName = httpHelper.getFileNameFromContentDisposition(header.name, header.value)
            if (fileName.isPresent) {
                return httpHelper.normalize(fileName.get())
            }
        }
        log.info("Cannot extract file name from http headers. Found headers: ")
        for (header in response.headers) {
            log.info("{}={}", header.name, header.value)
        }
        val fileNameFromUrl = httpHelper.getFileName(fileToDownloadLocation)
        return if (StringUtils.isNotBlank(fileNameFromUrl)) fileNameFromUrl else downloader.randomFileName()
    }

    @Throws(IOException::class)
    protected fun saveContentToFile(response: CloseableHttpResponse, downloadedFile: File) {
        FileUtils.copyInputStreamToFile(response.entity.content, downloadedFile)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DownloadFileWithHttpRequest::class.java)
    }
}
