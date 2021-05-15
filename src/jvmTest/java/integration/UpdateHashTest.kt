package integration

import com.codeborne.selenide.Condition.Companion.exactText
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalFileSystem
internal class UpdateHashTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("page_with_hash.html")
    }

    @Test
    fun userCanUpdateHashWithoutReloadingThePage() = runBlockingTest {
        `$`("h2").shouldHave(exactText("Current hash is: ''"))
        driver().updateHash("some-page")
        `$`("h2").shouldHave(exactText("Current hash is: '#some-page'"))
    }

    @Test
    fun hashCanStartWithSharp() = runBlockingTest {
        `$`("h2").shouldHave(exactText("Current hash is: ''"))
        driver().updateHash("#some-page")
        `$`("h2").shouldHave(exactText("Current hash is: '#some-page'"))
    }
}
