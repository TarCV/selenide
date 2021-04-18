package com.codeborne.selenide.impl

import com.codeborne.selenide.Config
import java.io.File
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class Downloader @JvmOverloads constructor(private val random: Randomizer = Randomizer()) {
    @CheckReturnValue
    fun randomFileName(): String {
        return random.text()
    }

    @CheckReturnValue
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
