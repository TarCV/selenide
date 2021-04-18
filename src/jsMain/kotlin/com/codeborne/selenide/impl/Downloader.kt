package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import java.io.File

class Downloader constructor(private val random: Randomizer = Randomizer()) {
    fun randomFileName(): String {
        return random.text()
    }
    fun prepareTargetFile(config: Config, fileName: String): File {
        val uniqueFolder = prepareTargetFolder(config)
        return File(uniqueFolder, fileName)
    }

    fun prepareTargetFolder(config: Config): File {
        val uniqueFolder = File(config.downloadsFolder(), random.text()).absoluteFile
        check(!uniqueFolder.exists()) { "Unbelievable! Unique folder already exists: $uniqueFolder" }
        FileHelper.ensureFolderExists(uniqueFolder)
        return uniqueFolder
    }
}
