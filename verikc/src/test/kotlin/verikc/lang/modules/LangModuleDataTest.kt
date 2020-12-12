package verikc.lang.modules

import org.junit.jupiter.api.Test
import verikc.assertStringEquals

internal class LangModuleDataTest {

    @Test
    fun `function ubit int int`() {
        val string = "ubit(8, 0)"
        val expected = "8'h00;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `function sbit int int`() {
        val string = "sbit(8, 0)"
        val expected = "8'sh00;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }
}