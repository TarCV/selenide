package support

import okio.ExperimentalFileSystem
import okio.FileSystem

actual object Platform {
    @ExperimentalFileSystem
    actual fun provideFileSystem(): FileSystem = okio.NodeJsFileSystem
}
