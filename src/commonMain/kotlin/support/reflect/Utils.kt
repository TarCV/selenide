package support.reflect

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

expect fun <T: Any> KClass<T>.newInstance(): T

expect fun <T: Any> KClass<T>.newInstance(arg1: Any): T

expect fun <R> KCallable<R>.invokeAsync(target: Any?, vararg args: Any?): R

expect fun KProperty<*>.isDeclaringClassAssignableTo(clazz: KClass<*>): Boolean
