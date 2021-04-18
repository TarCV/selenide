package com.codeborne.selenide.files

import java.io.File
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class DownloadedFile
/**
 * @param file the downloaded file
 * @param headers map of http headers. NB! Map keys (header names) are LOWER CASE!
 */(
    @get:CheckReturnValue
    val file: File, private val headers: Map<String, String>
) {
    @CheckReturnValue
    fun hasContentDispositionHeader(): Boolean {
        return headers.containsKey("content-disposition")
    }

    val contentType: String?
        get() = headers["content-type"]
}
