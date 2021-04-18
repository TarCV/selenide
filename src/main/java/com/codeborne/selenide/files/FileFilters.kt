package com.codeborne.selenide.files

object FileFilters {
    private val NONE: FileFilter = EmptyFileFilter()
    @JvmStatic
    fun none(): FileFilter {
        return NONE
    }

    @JvmStatic
    fun withName(fileName: String): FileFilter {
        return FilenameFilter(fileName)
    }

    @JvmStatic
    fun withNameMatching(fileNameRegex: String): FileFilter {
        return FilenameRegexFilter(fileNameRegex)
    }

    @JvmStatic
    fun withExtension(extension: String): FileFilter {
        return ExtensionFilter(extension)
    }
}
