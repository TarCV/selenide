package com.codeborne.selenide

import java.io.File
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class SharedDownloadsFolder(folder: String) : DownloadsFolder(File(folder)) {
    override fun cleanupBeforeDownload() {}
}
