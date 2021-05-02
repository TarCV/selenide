import com.codeborne.selenide.impl.FileHelper
import okio.ExperimentalFileSystem
import kotlin.test.Test
import kotlin.test.asserter

@ExperimentalFileSystem
class FileHelperTest {
    @Test
    fun relativize() {
        val relativeTo = Paths.get("a", "b", "c", "d")
        val otherPath = Paths.get("a", "b", "d", "e")
        val expectedResult = Paths.get("..", "..", "d", "e")

        asserter.assertEquals(null, expectedResult, FileHelper.relativize(relativeTo, otherPath))
    }
}
