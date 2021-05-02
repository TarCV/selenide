package com.codeborne.selenide.impl

import okio.ExperimentalFileSystem
import org.lighthousegames.logging.logging
import support.System
import support.net.URI

class CiReportUrl {
    @ExperimentalFileSystem
    fun getReportsUrl(reportsUrl: String?): String? {
        var reportsUrl: String? = reportsUrl
        if (!reportsUrl.isNullOrBlank()) {
            log.debug { "Using variable selenide.reportsUrl=${reportsUrl}" }
            return resolveUrlSource(reportsUrl)
        }
        reportsUrl = jenkinsReportsUrl
        if (!reportsUrl.isNullOrBlank()) {
            log.debug { "Using Jenkins BUILD_URL: ${reportsUrl}" }
            return reportsUrl
        }
        reportsUrl = teamCityUrl
        if (!reportsUrl.isNullOrBlank()) {
            log.debug { "Using Teamcity artifacts url: ${reportsUrl}" }
            return reportsUrl
        }
        log.debug { "Variable selenide.reportsUrl not found" }
        return reportsUrl
    }

    private val teamCityUrl: String?
        get() {
            val url = System.getProperty("teamcity.serverUrl")
            val build_type = System.getProperty("teamcity.buildType.id")
            val build_number = System.getProperty("build.number")
            return if (build_type.isNullOrBlank() || build_number.isNullOrBlank() || url.isNullOrBlank()) {
              null
            } else resolveUrlSource("$url/repository/download/$build_type/$build_number:id/")
        }

    // we have a workspace folder. Calculate the report relative path
    @ExperimentalFileSystem
    private val jenkinsReportsUrl: String?
        get() {
            val build_url = System.getProperty("BUILD_URL")
            return if (!build_url.isNullOrBlank()) {
              val workspace = System.getProperty("WORKSPACE", System.getenv("WORKSPACE"))
              var reportRelativePath = ""
              if (workspace.isNotBlank()) { // we have a workspace folder. Calculate the report relative path
                val pathAbsoluteReportsFolder = FileHelper.canonicalPath(FileHelper.pathOf(""))
                val pathAbsoluteWorkSpace = FileHelper.canonicalPath(FileHelper.pathOf(workspace))
                val pathRelative = FileHelper.relativize(pathAbsoluteWorkSpace,pathAbsoluteReportsFolder)
                reportRelativePath = pathRelative.toString().replace('\\', '/') + '/'
              }
              resolveUrlSource("$build_url/artifact/$reportRelativePath")
            } else {
              null
            }
        }

    private fun resolveUrlSource(base: String): String? {
        return try {
            URI(base).normalize().toURL().toString()
        } catch (e: Exception) {
            log.error(e) { "Variable selenide.reportsUrl is incorrect: $base" }
            null
        }
    }

    companion object {
        private val log = logging(CiReportUrl::class.simpleName)
    }
}
