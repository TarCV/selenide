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
    fun userCanCheckIfImageIsLoadedCorrectlyUsingCondition() = runBlockingTest {
        `$`("#valid-image img").shouldBe(Condition.image)
        `$`("#valid-image").shouldNotBe(Condition.image)
        `$`("h1").shouldNotBe(Condition.image)
    }

    @Test
    fun isImageConditionFailsForNonImages() = runBlockingTest {
        assertThat { `$`("h1").shouldBe(Condition.image) }.isFailure()
            .isInstanceOf(ElementShould::class.java)
    }

    @Test
    fun userCanCheckIfImageIsLoadedCorrectly() = runBlockingTest {
        Assertions.assertThat(`$`("#valid-image img").isImage())
            .isTrue
        Assertions.assertThat(`$`("#invalid-image img").isImage())
            .isFalse
    }

    @Test
    fun isImageIsOnlyApplicableForImages() = runBlockingTest {
        assertThat { `$`("h1").isImage() }.isFailure()
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
