package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
open class UploadFileFromClasspath : Command<File?> {
    var uploadFile = UploadFile()
    @CheckReturnValue
    @Throws(IOException::class)
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<out Any?>?): File {
        val fileName = Util.firstOf<Array<String>>(args)
        val files = Array(fileName.size) { i ->
            findFileInClasspath(fileName[i])
        }
        return uploadFile.execute(proxy, locator, files as Array<Any>)
    }

    @CheckReturnValue
    protected fun findFileInClasspath(name: String): File {
        val resource = Thread.currentThread().contextClassLoader.getResource(name)
            ?: throw IllegalArgumentException("File not found in classpath: $name")
        return try {
            File(resource.toURI())
        } catch (e: URISyntaxException) {
            throw IllegalArgumentException(e)
        }
    }
}
