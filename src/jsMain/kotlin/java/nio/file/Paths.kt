package java.nio.file

import okio.ExperimentalFileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath

@ExperimentalFileSystem
object Paths {
    fun get(vararg item: String): Path {
        val first = item.firstOrNull()?.toPath() ?: throw IOException("Can't convert empty path")
        return item.drop(1).fold(first) { acc, s ->
            acc / s
        }
    }
}
