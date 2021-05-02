package support.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

fun <T: Any> KClass<T>.newInstance(): T {
    // TODO: throw exceptions similar to Java on error
    return this.asDynamic()() as T
}

fun <T: Any> KClass<T>.newInstance(arg1: Any): T {
    // TODO: throw exceptions similar to Java on error
    return this.asDynamic()(arg1) as T
}

fun <R> KFunction<R>.invoke(target: Any?, vararg args: Any?): R {
    return js("this.apply(target, args)") as R
}
