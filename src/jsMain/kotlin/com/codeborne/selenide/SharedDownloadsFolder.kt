package com.codeborne.selenide

import okio.Path.Companion.toPath

class SharedDownloadsFolder(folder: String) : DownloadsFolder(folder.toPath()) {
    override fun cleanupBeforeDownload() {}
}
