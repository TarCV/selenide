package org.slf4j

import kotlin.reflect.KClass

class LoggerFactory {
    companion object {
        fun getLogger(klass: KClass<*>): Logger = TODO()
    }
}
