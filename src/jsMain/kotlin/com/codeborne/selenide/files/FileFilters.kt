package com.codeborne.selenide.files

object FileFilters {
    private val NONE: FileFilter = EmptyFileFilter()
    fun none(): FileFilter {
        return NONE
    }

    fun withName(fileName: String): FileFilter {
        return FilenameFilter(fileName)
    }

    fun withNameMatching(fileNameRegex: String): FileFilter {
        return FilenameRegexFilter(fileNameRegex)
    }

    fun withExtension(extension: String): FileFilter {
        return ExtensionFilter(extension)
    }
}
