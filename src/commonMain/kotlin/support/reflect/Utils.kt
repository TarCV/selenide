package support.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

expect fun <T: Any> KClass<T>.newInstance(): T

expect fun <T: Any> KClass<T>.newInstance(arg1: Any): T

expect fun <R> KFunction<R>.invokeAsync(target: Any?, vararg args: Any?): R
