package com.codeborne.selenide.impl

import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import okio.Source
import org.lighthousegames.logging.logging
import support.Platform
import kotlin.jvm.JvmStatic

object FileHelper {
    private val log = logging(FileHelper::class.simpleName)

    @ExperimentalFileSystem
    fun pathOf(vararg item: String): Path {
        val first = item.firstOrNull()?.toPath() ?: throw IOException("Can't convert empty path")
        return item.drop(1).fold(first) { acc, s ->
            acc / s
        }
    }

    @ExperimentalFileSystem
    private val fileSystem: FileSystem = Platform.provideFileSystem()

    @ExperimentalFileSystem
    fun writeToFile(source: ByteArray, targetFile: Path) {
        ensureParentFolderExists(targetFile) // TODO: wasn't present in Java version
        fileSystem.write(targetFile) {
            write(source)
        }
    }

    @ExperimentalFileSystem
    fun copyFile(sourceFile: Path, targetFile: Path) {
        fileSystem.copy(sourceFile, targetFile)
    }

    @ExperimentalFileSystem
    fun copyFile(`in`: Source, targetFile: Path) {
        ensureParentFolderExists(targetFile)
        fileSystem.write(targetFile) {
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
    @JvmStatic
    fun ensureFolderExists(folder: Path): Path {
        if (!exists(canonicalPath(folder))) {
            log.info { "Creating folder: $folder" }
            try {
                fileSystem.createDirectories(folder)
            } catch (e: IOException) {
                throw IllegalArgumentException("Failed to create folder '$folder'", e)
            }
        }
        return folder
    }

    @ExperimentalFileSystem
    fun moveFile(srcFile: Path, destFile: Path) {
        try {
            fileSystem.atomicMove(srcFile, destFile)
        } catch (e: IOException) {
            throw IllegalStateException(
                "Failed to move file $srcFile to $destFile", e
            )
        }
    }

    @ExperimentalFileSystem
    @JvmStatic
    fun deleteFolderIfEmpty(folder: Path) {
        if (fileSystem.metadataOrNull(folder)?.isDirectory == true) {
            val files = fileSystem.list(folder)
            if (files.isEmpty()) {
                fileSystem.delete(folder)
            }
        }
    }

    @ExperimentalFileSystem
    fun exists(path: Path): Boolean {
        return fileSystem.exists(path)
    }

    @ExperimentalFileSystem
    fun isFile(path: Path): Boolean {
        return fileSystem.metadataOrNull(path)?.isRegularFile == true
    }

    @ExperimentalFileSystem
    fun lastModified(file: Path): Long {
        return fileSystem.metadataOrNull(file)?.lastModifiedAtMillis ?: 0
    }

    @ExperimentalFileSystem
    fun listFiles(folder: Path): List<Path> {
        return fileSystem.list(folder)
    }

    @ExperimentalFileSystem
    @JvmStatic
    fun canonicalPath(path: Path): Path {
        return fileSystem.canonicalize(path)
    }

    @ExperimentalFileSystem
    fun relativize(relativeTo: Path, other: Path): Path {
        val fixedSegments = canonicalPath(relativeTo).segments()
        val otherSegments = canonicalPath(other).segments()

        val commonPrefixLength = fixedSegments.zip(otherSegments)
            .takeWhile { (a, b) -> a == b }
            .count()

        val commonPrefix = fixedSegments.take(commonPrefixLength)
        val goParentSteps = List(fixedSegments.size - commonPrefixLength) { ".." }
        val newSegments = otherSegments.drop(commonPrefixLength)

        val resultSegments = commonPrefix + goParentSteps + newSegments
        return pathOf(*resultSegments.toTypedArray())
    }
}

@ExperimentalFileSystem
private fun Path.segments(): List<String> {
    val segments = mutableListOf(this.name)

    var parent = this.parent
    while (parent != null) {
        segments += parent.name
        parent = parent.parent
    }

    return segments.reversed()
}
