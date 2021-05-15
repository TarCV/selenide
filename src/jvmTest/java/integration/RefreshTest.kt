package integration

import com.codeborne.selenide.Condition
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class RefreshTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun canRefreshPage() = runBlockingTest {
        `$`(By.name("rememberMe")).shouldNotBe(Condition.selected)
        `$`(By.name("rememberMe")).click()
        `$`(By.name("rememberMe")).shouldBe(Condition.selected)
        driver().refresh()
        `$`(By.name("rememberMe")).shouldNotBe(Condition.selected)
    }
}
