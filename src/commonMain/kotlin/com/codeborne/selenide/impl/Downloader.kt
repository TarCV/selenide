package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import okio.ExperimentalFileSystem
import okio.Path
import okio.Path.Companion.toPath

class Downloader constructor(private val random: Randomizer = Randomizer()) {
    fun randomFileName(): String {
        return random.text()
    }
    @ExperimentalFileSystem
    fun prepareTargetFile(config: Config, fileName: String): Path {
        val uniqueFolder = prepareTargetFolder(config)
        return (uniqueFolder / fileName)
    }

    @ExperimentalFileSystem
    fun prepareTargetFolder(config: Config): Path {
        val uniqueFolder = (config.downloadsFolder().toPath() / random.text())
        check(!FileHelper.exists(uniqueFolder)) { "Unbelievable! Unique folder already exists: $uniqueFolder" }
        FileHelper.ensureFolderExists(uniqueFolder)
        return uniqueFolder
    }
}
