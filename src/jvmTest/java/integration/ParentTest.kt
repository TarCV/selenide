package integration

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.By

@ExperimentalCoroutinesApi
@ExperimentalFileSystem
internal class ParentTest : ITest() {
    @BeforeEach
    fun openTestPageWithJQuery() = runBlockingTest {
        openFile("page_with_selects_without_jquery.html")
    }

    @Test
    fun canGetImmediateParentElement() {
        Assertions.assertThat(`$`("#theHiddenElement").parent())
            .isEqualTo(`$`("body"))
        Assertions.assertThat(`$`("h2").parent())
            .isEqualTo(`$`("#domain-container"))
        Assertions.assertThat(`$`(By.name("domain")).parent())
            .isEqualTo(`$`("#dropdown-list-container"))
        Assertions.assertThat(`$`("#multirowTableSecondRow"))
            .isEqualTo(`$`(".second_row").parent())
    }

    @Test
    fun canGetClosestMatchingAncestorByTagName() {
        Assertions.assertThat(`$`("#theHiddenElement").closest("body"))
            .isEqualTo(`$`("body"))
        Assertions.assertThat(`$`("h2").closest("body"))
            .isEqualTo(`$`("body"))
        Assertions.assertThat(`$`(By.name("domain")).closest("div"))
            .isEqualTo(`$`("#dropdown-list-container"))
    }

    @Test
    fun canGetClosestMatchingAncestorByClassName() {
        Assertions.assertThat(`$`(By.name("domain")).closest(".container"))
            .isEqualTo(`$`("#dropdown-list-container"))
        Assertions.assertThat(`$`(".second_row").closest("tr"))
            .isEqualTo(`$`("#multirowTableSecondRow"))
        Assertions.assertThat(`$`(".second_row").closest("table"))
            .isEqualTo(`$`("#multirowTable"))
        Assertions.assertThat(`$`(".second_row").closest(".table"))
            .isEqualTo(`$`("#multirowTable"))
        Assertions.assertThat(`$`(".second_row").closest(".multirow_table"))
            .isEqualTo(`$`("#multirowTable"))
    }
}
