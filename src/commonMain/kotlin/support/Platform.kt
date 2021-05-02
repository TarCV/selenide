package support

import okio.ExperimentalFileSystem
import okio.FileSystem

expect object Platform {
    @ExperimentalFileSystem
    fun provideFileSystem(): FileSystem
}
