package support

import kotlinx.coroutines.runBlocking
import java.lang.reflect.Method
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KFunction
import kotlin.reflect.javaType

actual interface InvocationHandler: java.lang.reflect.InvocationHandler {
    actual suspend fun invoke(proxy: Any, method: KFunction<*>, args: Array<out Any?>?): Any

    @ExperimentalStdlibApi
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any = runBlocking {
        val kmethod = Reflection.getOrCreateKotlinClass(method.declaringClass)
            .members
            .filter { member ->
                if (member !is KFunction<*>) {
                    return@filter false
                }
                val kParameters = member.parameters.map { it.type.javaType }
                val jvmParameters = method.parameters.map { it.type }
                member.name == method.name && kParameters == jvmParameters
            }
        invoke(proxy, kmethod as KFunction<*>, args)
    }
}
