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
import support.URL

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
            val message = "Failed to set downloads folder to ${downloadsFolder}"
            throw RuntimeException(message, e)
        }
    }
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
    private fun request(url: String, command: String): HttpPost {
        val request = HttpPost(url)
        request.addHeader("Content-Type", "application/json")
        request.entity = StringEntity(command)
        return request
    }

    private fun post(remoteDriverUrl: URL, driverSessionId: SessionId, command: String) {
        val url = "${remoteDriverUrl}/session/{driverSessionId}/chromium/send_command"
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
        private val log = LoggerFactory.getLogger(CdpClient::class)
    }
}
