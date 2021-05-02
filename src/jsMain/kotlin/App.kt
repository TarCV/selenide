import kotlinx.coroutines.await
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.js.Promise

/*@JsModule("webdriver")
@JsNonModule
external actual fun remote()*/

suspend fun main(): Unit = coroutineScope  {
         val remote = awaitDynamic(js(
                 """
                require('webdriverio').remote({
                    logLevel: 'trace',
                    capabilities: {
                        browserName: 'chrome'
                    }
                })
            """
         ))
        awaitDynamic(remote.url("https://duckduckgo.com"))
        val title = awaitDynamic(remote.getTitle())
        console.log(title)
        awaitDynamic(remote.deleteSession())
}

private suspend fun awaitDynamic(remote: Any?) = (remote as Promise<Any>).await().asDynamic()
