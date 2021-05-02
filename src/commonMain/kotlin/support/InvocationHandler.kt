package support

import kotlin.reflect.KFunction

expect interface InvocationHandler {
    suspend fun invoke(proxy: Any, method: KFunction<*>, args: Array<out Any?>?): Any
}
