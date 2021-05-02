package support.reflect

import support.InvocationHandler
import kotlin.reflect.KClass

actual object Proxy {
    actual fun newProxyInstance(unused: Any?, interfaces: Array<KClass<*>>, handler: InvocationHandler): Any {
        return java.lang.reflect.Proxy.newProxyInstance(
            ClassLoader.getSystemClassLoader(),
            interfaces.map { it.java }.toTypedArray(),
            handler
        )
    }
}
