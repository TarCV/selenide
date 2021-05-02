package com.codeborne.selenide

import okio.ExperimentalFileSystem
import okio.Path.Companion.toPath

@ExperimentalFileSystem
class SharedDownloadsFolder(folder: String) : DownloadsFolder(folder.toPath()) {
    override fun cleanupBeforeDownload() {}
}
