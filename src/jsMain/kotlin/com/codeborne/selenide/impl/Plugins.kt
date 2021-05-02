package com.codeborne.selenide.impl

import org.kodein.di.DI
import org.kodein.di.instance
import org.lighthousegames.logging.logging
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

    inline fun <reified T : Any> inject(klass: KClass<T>): T {
        val implementation: T by context.instance()
        logger.info { "Using implementation of ${klass.simpleName}: ${implementation::class.simpleName}" }
        return implementation
    }
}
