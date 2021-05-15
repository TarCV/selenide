package integration

import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@ExperimentalFileSystem
internal class ZoomTest : ITest() {
    @Test
    fun canZoomInAndOut() = runBlockingTest {
        openFile("page_with_big_divs.html")
        val initialX = `$`("#wide_div").location.getX()
        assertBetween(`$`("#wide_div").location.getY(), 70, 85) // FF: 81, Chrome: 79
        driver().zoom(1.1)
        assertBetween(`$`("#wide_div").location.getY(), 80, 100) // FF: 87, Chrome: 85, JenkinsFF: 91
        assertThat(`$`("#wide_div").location.getX()).isEqualTo(initialX)
        driver().zoom(2.0)
        assertBetween(`$`("#wide_div").location.getY(), 130, 160) // FF: 141, Chrome: 138
        assertThat(`$`("#wide_div").location.getX()).isEqualTo(initialX)
        driver().zoom(0.5)
        assertBetween(`$`("#wide_div").location.getY(), 50, 70) // FF: 51, Chrome: 50
        assertThat(`$`("#wide_div").location.getX()).isEqualTo(initialX)
    }

    companion object {
        private fun assertBetween(n: Int, lower: Int, upper: Int) {
            Assertions.assertThat(n >= lower)
                .withFailMessage("$n should be between $lower and $upper")
                .isTrue
            Assertions.assertThat(n <= upper)
                .withFailMessage("$n should be between $lower and $upper")
                .isTrue
        }
    }
}
