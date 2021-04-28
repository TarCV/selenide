package com.codeborne.selenide.impl

import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Source
import org.slf4j.LoggerFactory

object FileHelper {
    private val log = LoggerFactory.getLogger(FileHelper::class)

    @ExperimentalFileSystem
    private val virtualFiles: FileSystem = TODO()

    @ExperimentalFileSystem
    fun writeToFile(source: ByteArray, targetFile: Path) {
        ensureParentFolderExists(targetFile) // TODO: wasn't present in Java version
        virtualFiles.write(targetFile) {
            write(source)
        }
    }

    @ExperimentalFileSystem
    fun copyFile(sourceFile: Path, targetFile: Path) {
        virtualFiles.copy(sourceFile, targetFile)
    }

    @ExperimentalFileSystem
    fun copyFile(`in`: Source, targetFile: Path) {
        ensureParentFolderExists(targetFile)
        virtualFiles.write(targetFile) {
            writeAll(`in`)
        }
    }

    @ExperimentalFileSystem
    fun ensureParentFolderExists(targetFile: Path) {
        targetFile.parent?.let {
            ensureFolderExists(it)
        }
    }

    @ExperimentalFileSystem
    fun ensureFolderExists(folder: Path): Path {
        if (!exists(canonicalPath(folder))) {
            log.info("Creating folder: {}", folder)
            try {
                virtualFiles.createDirectories(folder)
            } catch (e: IOException) {
                throw IllegalArgumentException("Failed to create folder '$folder'", e)
            }
        }
        return folder
    }

    @ExperimentalFileSystem
    fun moveFile(srcFile: Path, destFile: Path) {
        try {
            virtualFiles.atomicMove(srcFile, destFile)
        } catch (e: IOException) {
            throw IllegalStateException(
                "Failed to move file $srcFile to $destFile", e
            )
        }
    }

    @ExperimentalFileSystem
    fun deleteFolderIfEmpty(folder: Path) {
        if (virtualFiles.metadataOrNull(folder)?.isDirectory == true) {
            val files = virtualFiles.list(folder)
            if (files.isEmpty()) {
                virtualFiles.delete(folder)
            }
        }
    }

    @ExperimentalFileSystem
    fun exists(path: Path): Boolean {
        return virtualFiles.exists(path)
    }

    @ExperimentalFileSystem
    fun isFile(path: Path): Boolean {
        return virtualFiles.metadataOrNull(path)?.isRegularFile == true
    }

    @ExperimentalFileSystem
    fun lastModified(file: Path): Long {
        return virtualFiles.metadataOrNull(file)?.lastModifiedAtMillis ?: 0
    }

    @ExperimentalFileSystem
    fun listFiles(folder: Path): List<Path> {
        return virtualFiles.list(folder)
    }

    @ExperimentalFileSystem
    fun canonicalPath(path: Path): Path {
        return virtualFiles.canonicalize(path)
    }
}
