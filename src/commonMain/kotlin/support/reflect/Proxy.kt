package support.reflect

import support.InvocationHandler
import kotlin.reflect.KClass

expect object Proxy {
    fun newProxyInstance(unused: Any?, interfaces: Array<KClass<*>>, handler: InvocationHandler): Any
}
