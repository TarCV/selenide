package com.codeborne.selenide.impl

import org.slf4j.LoggerFactory
import okio.IOException
import support.URL
import java.util.ServiceLoader

/**
 * We assume this API will change in next releases.
 * Be aware if you are going to use it.
 *
 * @since Selenide 5.15.0
 */
object Plugins {
    private val logger = LoggerFactory.getLogger(Plugins::class)
    private val cache: MutableMap<kotlin.reflect.KClass<*>, Any?> = ConcurrentHashMap()

    fun <T: Any> inject(klass: kotlin.reflect.KClass<T>): T = synchronized(this) {
        var plugin = cache[klass] as T?
        if (plugin == null) {
            plugin = loadPlugin(klass)
            cache[klass] = plugin
        }
        return plugin
    }

    private fun <T: Any> loadPlugin(klass: kotlin.reflect.KClass<T>): T {
        val loader: Iterator<T> = ServiceLoader.load(klass).iterator()
        if (!loader.hasNext()) {
            val defaultPlugin = getDefaultPlugin(klass)
            logger.debug("Using default implementation of {}: {}", klass.simpleName, defaultPlugin::class.simpleName)
            return defaultPlugin
        }
        val implementation = loader.next()
        logger.info("Using implementation of {}: {}", klass.simpleName, implementation::class.simpleName)
        return implementation
    }

    private fun <T: Any> getDefaultPlugin(klass: kotlin.reflect.KClass<T>): T {
        val resource = "/META-INF/defaultservices/" + klass.simpleName
        val file = Plugins::class.getResource(resource)
            ?: throw IllegalStateException("Resource not found in classpath: $resource")
        val className = readFile(file).trim { it <= ' ' }
        return try {
            instantiate(className)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to initialize default plugin $className from $file", e)
        }
    }

/*
TODO:    private fun <T: Any> instantiate(className: String): T {
        val constructor = kotlin.reflect.KClass.forName(className).getDeclaredConstructor() as Constructor<T>
        return constructor.newInstance()
    }
*/

    private fun readFile(file: URL): String {
        return try {
            file.readText()
        } catch (e: IOException) {
            throw IllegalStateException("Failed to read $file", e)
        }
    }
}
