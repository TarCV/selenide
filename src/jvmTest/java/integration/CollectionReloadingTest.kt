package integration

import com.codeborne.selenide.CollectionCondition.Companion.size
import com.codeborne.selenide.CollectionCondition.Companion.sizeGreaterThan
import com.codeborne.selenide.Condition.Companion.text
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class CollectionReloadingTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("collection_with_delays.html")
        setTimeout(4000)
    }

    @Test
    fun reloadsCollectionOnEveryCall() {
        val collection = `$$`("#collection li")
        collection.get(0).shouldHave(text("Element #0"))
        collection.get(10).shouldHave(text("Element #10"))
    }

    @Test
    fun canTakeSnapshotOfCollection() = runBlockingTest {
        val collection = `$$`("#collection li")
        val snapshot = collection.snapshot()
        val currentSize = snapshot.getSize()
        collection.shouldHave(sizeGreaterThan(currentSize))
        snapshot.shouldHave(size(currentSize))
    }
}
