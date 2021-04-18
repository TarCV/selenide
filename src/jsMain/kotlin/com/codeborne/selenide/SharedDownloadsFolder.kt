package com.codeborne.selenide

import java.io.File

class SharedDownloadsFolder(folder: String) : DownloadsFolder(File(folder)) {
    override fun cleanupBeforeDownload() {}
}
