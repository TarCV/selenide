package com.codeborne.selenide.impl

import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.File

object FileHelper {
    private val log = LoggerFactory.getLogger(FileHelper::class)
    private val virtualFiles: FakeFileSystem = TODO()

    fun writeToFile(source: ByteArray, targetFile: Path) {
        ensureParentFolderExists(targetFile) // TODO: wasn't present in Java version
        virtualFiles.write(targetFile) {
            write(source)
        }
    }

    fun copyFile(sourceFile: File, targetFile: File) {
        FileSystem.copy(sourceFile, targetFile)
    }

    fun copyFile(`in`: okio.okio.Source, targetFile: File) {
        ensureParentFolderExists(targetFile)
        FileSystem.write(targetFile) {
            writeAll(`in`)
        }
    }

    fun ensureParentFolderExists(targetFile: File) {
        ensureFolderExists(targetFile.parentFile)
    }

    fun ensureFolderExists(folder: File): File {
        if (!folder.exists() || !folder.absoluteFile.exists()) {
            log.info("Creating folder: {}", folder.absolutePath)
            try {
                FileSystem.createDirectories(folder.toPath())
            } catch (e: IOException) {
                throw IllegalArgumentException("Failed to create folder '" + folder.absolutePath + "'", e)
            }
        }
        return folder
    }

    fun moveFile(srcFile: File, destFile: File) {
        try {
            FileUtils.moveFile(srcFile, destFile)
        } catch (e: IOException) {
            throw IllegalStateException(
                "Failed to move file " + srcFile.absolutePath +
                        " to " + destFile.absolutePath, e
            )
        }
    }

    fun deleteFolderIfEmpty(folder: File) {
        if (folder.isDirectory) {
            val files = folder.listFiles()
            if (files == null || files.isEmpty()) {
                if (folder.delete()) {
                    log.info("Deleted empty folder: {}", folder.absolutePath)
                } else {
                    log.error("Failed to delete empty folder: {}", folder.absolutePath)
                }
            }
        }
    }
}
