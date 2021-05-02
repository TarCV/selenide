package support.reflect

import support.InvocationHandler
import kotlin.reflect.KClass

actual object Proxy {
    actual fun newProxyInstance(unused: Any?, interfaces: Array<KClass<*>>, handler: InvocationHandler): Any = TODO()
}
