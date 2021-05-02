package support

import kotlin.reflect.KFunction

actual interface InvocationHandler {
    actual suspend fun invoke(proxy: Any, method: KFunction<*>, args: Array<out Any?>?): Any
}
