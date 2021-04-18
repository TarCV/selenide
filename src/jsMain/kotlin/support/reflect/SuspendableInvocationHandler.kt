package support.reflect

import kotlin.reflect.KFunction

interface SuspendableInvocationHandler {
    suspend fun invoke(proxy: Any, method: KFunction<*>, args: Array<out Any?>?): Any
}
