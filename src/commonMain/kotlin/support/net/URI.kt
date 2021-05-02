package support.net

expect class URI(uri: String) {
    fun normalize(): URI
    fun toURL(): URL
}
