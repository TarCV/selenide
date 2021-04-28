package support.reflect

import kotlin.reflect.KClass

fun <T: Any> KClass<T>.newInstance(): T {
    // TODO: throw exceptions similar to Java on error
    return this.asDynamic()() as T
}
