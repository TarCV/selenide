package com.codeborne.selenide.impl

import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
object FileHelper {
    private val log = LoggerFactory.getLogger(FileHelper::class.java)
    @JvmStatic
    @Throws(IOException::class)
    fun writeToFile(source: ByteArray, targetFile: File) {
        ByteArrayInputStream(source).use { `in` -> copyFile(`in`, targetFile) }
    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: File, targetFile: File) {
        FileInputStream(sourceFile).use { `in` -> copyFile(`in`, targetFile) }
    }

    @JvmStatic
    @Throws(IOException::class)
    fun copyFile(`in`: InputStream, targetFile: File) {
        ensureParentFolderExists(targetFile)
        FileOutputStream(targetFile).use { out ->
            val buffer = ByteArray(1024)
            var len: Int
            while (`in`.read(buffer).also { len = it } != -1) {
                out.write(buffer, 0, len)
            }
        }
    }

    @JvmStatic
    fun ensureParentFolderExists(targetFile: File) {
        ensureFolderExists(targetFile.parentFile)
    }

    @JvmStatic
    fun ensureFolderExists(folder: File): File {
        if (!folder.exists() || !folder.absoluteFile.exists()) {
            log.info("Creating folder: {}", folder.absolutePath)
            try {
                Files.createDirectories(folder.toPath())
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

    @JvmStatic
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
