package com.codeborne.selenide.files

import java.io.Path

class DownloadedFile
/**
 * @param file the downloaded file
 * @param headers map of http headers. NB! Map keys (header names) are LOWER CASE!
 */(
    val file: Path, private val headers: Map<String, String>
) {
    fun hasContentDispositionHeader(): Boolean {
        return headers.containsKey("content-disposition")
    }

    val contentType: String?
        get() = headers["content-type"]
}
