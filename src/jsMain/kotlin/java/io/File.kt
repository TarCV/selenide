package java.io

class File {
    constructor(path: String) {
        TODO()
    }
    constructor(parent: String, name: String) {
        TODO()
    }
    constructor(parent: File, name: String) {
        TODO()
    }

    val absoluteFile: File
        get() = TODO()
    val absolutePath: String
        get() = TODO()
    val name: String
        get() = TODO()

    fun exists(): Boolean = TODO()
    fun listFiles(): Array<File> = TODO()
    fun lastModified(): Long = TODO()

    companion object {
        val separatorChar = '/'
    }
}
