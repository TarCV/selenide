package support.reflect

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaGetter

actual fun <T: Any> KClass<T>.newInstance(): T {
    return this.java.getDeclaredConstructor().newInstance()
}

actual fun <T: Any> KClass<T>.newInstance(arg1: Any): T {
    return this.java.getDeclaredConstructor(arg1::class.java).newInstance(arg1)
}
actual fun <R> KCallable<R>.invokeAsync(target: Any?, vararg args: Any?): R {
    val allArgs = (listOf(target) + args.toList()).toTypedArray()
    return this.call(allArgs)
}

actual fun KProperty<*>.isDeclaringClassAssignableTo(clazz: KClass<*>): Boolean {
    return this.javaGetter?.let {
        clazz.java.isAssignableFrom(it.declaringClass)
    } ?: false
}
