package com.codeborne.selenide.impl

import org.slf4j.LoggerFactory
import java.net.URI
import java.nio.file.Paths
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class CiReportUrl {
    @CheckReturnValue
    fun getReportsUrl(reportsUrl: String?): String? {
        var reportsUrl: String? = reportsUrl
        if (!reportsUrl.isNullOrBlank()) {
            log.debug("Using variable selenide.reportsUrl={}", reportsUrl)
            return resolveUrlSource(reportsUrl)
        }
        reportsUrl = jenkinsReportsUrl
        if (!reportsUrl.isNullOrBlank()) {
            log.debug("Using Jenkins BUILD_URL: {}", reportsUrl)
            return reportsUrl
        }
        reportsUrl = teamCityUrl
        if (!reportsUrl.isNullOrBlank()) {
            log.debug("Using Teamcity artifacts url: {}", reportsUrl)
            return reportsUrl
        }
        log.debug("Variable selenide.reportsUrl not found")
        return reportsUrl
    }

    private val teamCityUrl: String?
        get() {
            val url = System.getProperty("teamcity.serverUrl")
            val build_type = System.getProperty("teamcity.buildType.id")
            val build_number = System.getProperty("build.number")
            return if (build_type.isNullOrBlank() || build_number.isNullOrBlank() || url.isNullOrBlank()) {
              null
            } else resolveUrlSource("%s/repository/download/%s/%s:id/", url, build_type, build_number)
        }

    // we have a workspace folder. Calculate the report relative path
    private val jenkinsReportsUrl: String?
        get() {
            val build_url = System.getProperty("BUILD_URL")
            return if (!build_url.isNullOrBlank()) {
              val workspace = System.getProperty("WORKSPACE", System.getenv("WORKSPACE"))
              var reportRelativePath = ""
              if (!workspace.isNullOrBlank()) { // we have a workspace folder. Calculate the report relative path
                val pathAbsoluteReportsFolder = Paths.get("").normalize().toAbsolutePath()
                val pathAbsoluteWorkSpace = Paths.get(workspace).normalize().toAbsolutePath()
                val pathRelative = pathAbsoluteWorkSpace.relativize(pathAbsoluteReportsFolder)
                reportRelativePath = pathRelative.toString().replace('\\', '/') + '/'
              }
              resolveUrlSource("%s/artifact/%s", build_url, reportRelativePath)
            } else {
              null
            }
        }

    private fun resolveUrlSource(base: String, vararg format: Any): String? {
        var base = base
        if (format.isNotEmpty()) {
            base = String.format(base, *format)
        }
        return try {
            URI(base).normalize().toURL().toString()
        } catch (e: Exception) {
            log.error("Variable selenide.reportsUrl is incorrect: {}", base, e)
            null
        }
    }

  companion object {
        private val log = LoggerFactory.getLogger(CiReportUrl::class.java)
    }
}
