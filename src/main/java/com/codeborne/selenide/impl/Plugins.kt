package com.codeborne.selenide.impl

import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.lang.reflect.Constructor
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.ServiceLoader
import java.util.concurrent.ConcurrentHashMap

/**
 * We assume this API will change in next releases.
 * Be aware if you are going to use it.
 *
 * @since Selenide 5.15.0
 */
object Plugins {
    private val logger = LoggerFactory.getLogger(Plugins::class.java)
    private val cache: MutableMap<Class<*>, Any?> = ConcurrentHashMap()
    @JvmStatic
    @Synchronized
    fun <T: Any> inject(klass: Class<T>): T {
        var plugin = cache[klass] as T?
        if (plugin == null) {
            plugin = loadPlugin(klass)
            cache[klass] = plugin
        }
        return plugin
    }

    private fun <T: Any> loadPlugin(klass: Class<T>): T {
        val loader: Iterator<T> = ServiceLoader.load(klass).iterator()
        if (!loader.hasNext()) {
            val defaultPlugin = getDefaultPlugin(klass)
            logger.debug("Using default implementation of {}: {}", klass.name, defaultPlugin.javaClass.name)
            return defaultPlugin
        }
        val implementation = loader.next()
        logger.info("Using implementation of {}: {}", klass.name, implementation.javaClass.name)
        return implementation
    }

    private fun <T: Any> getDefaultPlugin(klass: Class<T>): T {
        val resource = "/META-INF/defaultservices/" + klass.name
        val file = Plugins::class.java.getResource(resource)
            ?: throw IllegalStateException("Resource not found in classpath: $resource")
        val className = readFile(file).trim { it <= ' ' }
        return try {
            instantiate(className)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize default plugin $className from $file", e)
        }
    }

    @Throws(Exception::class)
    private fun <T: Any> instantiate(className: String): T {
        val constructor = Class.forName(className).getDeclaredConstructor() as Constructor<T>
        constructor.isAccessible = true
        return constructor.newInstance()
    }

    private fun readFile(file: URL): String {
        return try {
            IOUtils.toString(file, StandardCharsets.UTF_8)
        } catch (e: IOException) {
            throw IllegalStateException("Failed to read $file", e)
        }
    }
}
