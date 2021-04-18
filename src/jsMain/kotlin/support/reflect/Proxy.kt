package support.reflect

import kotlin.reflect.KClass

object Proxy {
    fun newProxyInstance(unused: Any?, interfaces: Array<KClass<*>>, handler: Any): Any = TODO()
}
