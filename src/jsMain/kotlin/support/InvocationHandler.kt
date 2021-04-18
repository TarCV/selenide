package support

import kotlin.reflect.KFunction

interface InvocationHandler {
    suspend fun invoke(proxy: Any, method: KFunction<*>, args: Array<out Any?>?): Any
}
