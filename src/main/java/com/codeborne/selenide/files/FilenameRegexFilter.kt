package com.codeborne.selenide.files

import java.util.regex.Pattern

internal class FilenameRegexFilter(fileNameRegex: String) : FileFilter {
    private val fileNameRegex: Pattern = Pattern.compile(fileNameRegex)

    override fun match(file: DownloadedFile): Boolean {
        return fileNameRegex.matcher(file.file.name).matches()
    }

    override fun description(): String {
        return "with file name matching \"$fileNameRegex\""
    }

    override fun toString(): String {
        return description()
    }

}
