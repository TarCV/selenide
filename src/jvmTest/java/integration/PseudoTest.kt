package integration

import com.codeborne.selenide.Condition.Companion.pseudo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class PseudoTest : ITest() {
    @BeforeEach
    fun openTestPage() = runBlockingTest {
        openFile("page_with_pseudo_elements.html")
    }

    @Test
    fun shouldHavePseudo() {
        `$`("h1").shouldHave(pseudo(":first-letter", "color", "rgb(255, 0, 0)"))
        `$`("h2").shouldNotHave(pseudo(":first-letter", "color", "rgb(255, 0, 0)"))
        `$`("abbr").shouldHave(pseudo(":before", "content", "\"beforeContent\""))
        `$`("p").shouldNotHave(pseudo(":before", "content", "\"beforeContent\""))
        `$`("abbr").shouldHave(pseudo(":before", "\"beforeContent\""))
        `$`("p").shouldNotHave(pseudo(":before", "\"beforeContent\""))
    }

    @get:Test
    val pseudo: Unit
        get() {
            Assertions.assertThat(`$`("h1").pseudo(":first-letter", "color")).isEqualTo("rgb(255, 0, 0)")
            Assertions.assertThat(`$`("abbr").pseudo(":before", "content")).isEqualTo("\"beforeContent\"")
            Assertions.assertThat(`$`("p").pseudo(":after", "content")).isEqualTo("none")
            Assertions.assertThat(`$`("abbr").pseudo(":before")).isEqualTo("\"beforeContent\"")
            Assertions.assertThat(`$`("p").pseudo(":after")).isEqualTo("none")
        }
}
