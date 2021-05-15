package integration

import assertk.assertThat
import assertk.assertions.isFailure
import assertk.assertions.isInstanceOf
import com.codeborne.selenide.Condition
import com.codeborne.selenide.ex.ElementShould
import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalFileSystem
internal class ImageTest : ITest() {
    @BeforeEach
    fun openTestPageWithImages() = runBlockingTest {
        openFile("page_with_images.html")
    }

    @Test
    fun userCanCheckIfImageIsLoadedCorrectlyUsingCondition() {
        `$`("#valid-image img").shouldBe(Condition.image)
        `$`("#valid-image").shouldNotBe(Condition.image)
        `$`("h1").shouldNotBe(Condition.image)
    }

    @get:Test
    val isImageConditionFailsForNonImages: Unit
        get() {
            assertThat { `$`("h1").shouldBe(Condition.image) }.isFailure()
                .isInstanceOf(ElementShould::class.java)
        }

    @Test
    fun userCanCheckIfImageIsLoadedCorrectly() {
        Assertions.assertThat(`$`("#valid-image img").isImage)
            .isTrue
        Assertions.assertThat(`$`("#invalid-image img").isImage)
            .isFalse
    }

    @get:Test
    val isImageIsOnlyApplicableForImages: Unit
        get() {
            assertThat { `$`("h1").isImage }.isFailure()
                .isInstanceOf(IllegalArgumentException::class.java)
        }
}
