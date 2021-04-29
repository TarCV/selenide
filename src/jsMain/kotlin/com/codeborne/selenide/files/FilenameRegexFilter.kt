package com.codeborne.selenide.files

import okio.ExperimentalFileSystem


internal class FilenameRegexFilter(fileNameRegex: String) : FileFilter {
    private val fileNameRegex: kotlin.text.Regex = kotlin.text.Regex(fileNameRegex)

    @ExperimentalFileSystem
    override fun match(file: DownloadedFile): Boolean {
        return fileNameRegex.matches(file.file.name)
    }

    override fun description(): String {
        return "with file name matching \"$fileNameRegex\""
    }

    override fun toString(): String {
        return description()
    }

}
