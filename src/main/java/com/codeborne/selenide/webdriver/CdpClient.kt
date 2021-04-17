package com.codeborne.selenide.webdriver

import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.core5.http.HttpResponse
import org.apache.hc.core5.http.io.entity.StringEntity
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.SessionId
import org.openqa.selenium.remote.service.DriverService
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.net.URL
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class CdpClient {
    fun setDownloadsFolder(driverService: DriverService, driver: RemoteWebDriver, downloadsFolder: File) {
        setDownloadsFolder(driverService.url, driver.sessionId, downloadsFolder)
    }

    fun setDownloadsFolder(remoteDriverUrl: URL, driverSessionId: SessionId, downloadsFolder: File) {
        try {
            val command = command(downloadsFolder)
            post(remoteDriverUrl, driverSessionId, command)
            log.info("Downloading files to {}", downloadsFolder)
        } catch (e: IOException) {
            val message = String.format("Failed to set downloads folder to %s", downloadsFolder)
            throw RuntimeException(message, e)
        }
    }

    @CheckReturnValue
    private fun command(downloadsFolder: File): String {
        return """{  "cmd": "Page.setDownloadBehavior",
  "params": {
    "behavior": "allow",
    "downloadPath": "${escapeForJson(downloadsFolder.absolutePath)}"
  }
}"""
    }

    fun escapeForJson(text: String): String {
        return text.replace("\\", "\\\\").replace("\"", "\\\"")
    }

    @CheckReturnValue
    private fun request(url: String, command: String): HttpPost {
        val request = HttpPost(url)
        request.addHeader("Content-Type", "application/json")
        request.entity = StringEntity(command)
        return request
    }

    @Throws(IOException::class)
    private fun post(remoteDriverUrl: URL, driverSessionId: SessionId, command: String) {
        val url = String.format("%s/session/%s/chromium/send_command", remoteDriverUrl, driverSessionId)
        val request = request(url, command)
        HttpClientBuilder.create().build().use { httpClient ->
            val response: HttpResponse = httpClient.execute(request)
            val code = response.code
            if (code != 200) {
                val error = response.reasonPhrase
                val message = String.format("Failed to send CDP command %s: status=%s, error=%s", command, code, error)
                throw IOException(message)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CdpClient::class.java)
    }
}
