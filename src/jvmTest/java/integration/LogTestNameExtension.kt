package integration

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.opentest4j.TestAbortedException
import org.slf4j.LoggerFactory

internal class LogTestNameExtension : BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
    override fun beforeAll(context: ExtensionContext) {
        LoggerFactory.getLogger(context.displayName).info("Starting tests @ {}", BaseIntegrationTest.browser)
    }

    override fun afterAll(context: ExtensionContext) {
        LoggerFactory.getLogger(context.displayName)
            .info("Finished tests @ {} - {}", BaseIntegrationTest.browser, verdict(context))
    }

    override fun beforeEach(context: ExtensionContext) {
        LoggerFactory.getLogger(context.requiredTestClass.name).info("starting {} ...", context.displayName)
    }

    override fun afterEach(context: ExtensionContext) {
        LoggerFactory.getLogger(context.requiredTestClass.name)
            .info("finished {} - {}", context.displayName, verdict(context))
    }

    private fun verdict(context: ExtensionContext): String {
        return if (context.executionException.isPresent) if (context.executionException.get() is TestAbortedException) "skipped" else "NOK" else "OK"
    }
}
