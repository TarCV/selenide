package com.codeborne.selenide.impl

import org.kodein.di.DI
import org.kodein.di.Instance
import org.kodein.di.instance
import org.kodein.type.erased
import org.kodein.type.generic
import org.lighthousegames.logging.logging
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

/**
 * We assume this API will change in next releases.
 * Be aware if you are going to use it.
 *
 * @since Selenide 5.15.0
 */
object Plugins {
    lateinit var context: DI
    val logger = logging(Plugins::class.simpleName)

    inline fun <reified T : Any> injectA(klass: KClass<T>): T {
        val implementation: T by context.instance()
        logger.info { "Using implementation of ${klass.simpleName}: ${implementation::class.simpleName}" }
        return implementation
    }

    @JvmStatic
    fun <T: Any> injectJvm(klass: KClass<T>): T {
        val implementation = context.run {
            val implementation: T by Instance(erased(klass))
            implementation
        }
        logger.info { "Using implementation of ${klass.simpleName}: ${implementation::class.simpleName}" }
        return implementation
    }
}
