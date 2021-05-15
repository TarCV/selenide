package integration

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.InvocationInterceptor
import org.junit.jupiter.api.extension.ReflectiveInvocationContext
import java.lang.reflect.Method
import java.util.Locale

class UseLocaleExtension(private val language: String) : InvocationInterceptor {
    @Throws(Throwable::class)
    override fun interceptTestMethod(
        invocation: InvocationInterceptor.Invocation<Void>, invocationContext: ReflectiveInvocationContext<Method>,
        extensionContext: ExtensionContext
    ) {
        val previous = Locale.getDefault()
        Locale.setDefault(Locale(language))
        try {
            invocation.proceed()
        } finally {
            Locale.setDefault(previous)
        }
    }
}
