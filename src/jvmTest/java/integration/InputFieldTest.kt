package integration

import kotlinx.coroutines.test.runBlockingTest
import okio.ExperimentalFileSystem
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalFileSystem
internal class InputFieldTest : ITest() {
    @BeforeEach
    fun setup() = runBlockingTest {
        openFile("html5_input.html" + System.currentTimeMillis())
    }

    @Test
    fun selenideClearTest() {
        val input = `$`("#id1")
        Assertions.assertThat(input.value).isNullOrEmpty()
        input.clear()
        input.setValue(",.123")
        input.clear()
        input.setValue("456")
        Assertions.assertThat(input.value).isEqualTo("456")
        input.clear()
        input.setValue(",.123")
        input.clear()
        input.setValue("456")
        Assertions.assertThat(input.value).isEqualTo("456")
        input.setValue("456")
        input.setValue("")
        Assertions.assertThat(input.value).isEqualTo("")
    }
}
