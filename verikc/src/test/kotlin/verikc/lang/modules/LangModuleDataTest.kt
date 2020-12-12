package verikc.lang.modules

import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.assertThrowsMessage
import verikc.base.ast.LineException

internal class LangModuleDataTest {

    @Test
    fun `function ubit int illegal`() {
        val string = "ubit(0)"
        assertThrowsMessage<LineException>("could not infer width of ubit") {
            LangModuleUtil.buildExpressionWithContext(string)
        }
    }

    @Test
    fun `function ubit int`() {
        val string = "x + ubit(0)"
        val expected = "x + 8'h00;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `function ubit int int`() {
        val string = "ubit(8, 0)"
        val expected = "8'h00;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }

    @Test
    fun `function sbit int illegal`() {
        val string = "sbit(0)"
        assertThrowsMessage<LineException>("could not infer width of sbit") {
            LangModuleUtil.buildExpressionWithContext(string)
        }
    }

    @Test
    fun `function sbit int int`() {
        val string = "sbit(8, 0)"
        val expected = "8'sh00;"
        assertStringEquals(expected, LangModuleUtil.buildExpressionWithContext(string))
    }
}