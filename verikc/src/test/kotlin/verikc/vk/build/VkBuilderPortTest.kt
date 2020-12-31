/*
 * Copyright (c) 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package verikc.vk.build

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.assertThrowsMessage
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.PortType
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verikc.lang.LangSymbol.FUNCTION_TYPE_UBIT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.line
import verikc.vk.VkBuildUtil
import verikc.vk.ast.VkExpressionFunction
import verikc.vk.ast.VkExpressionLiteral
import verikc.vk.ast.VkPort

internal class VkBuilderPortTest {

    @Test
    fun `bool input`() {
        val string = "@input val x = _bool()"
        val expected = VkPort(
            line(4),
            "x",
            Symbol(6),
            TYPE_BOOL,
            PortType.INPUT,
            VkExpressionFunction(
                line(4),
                TYPE_BOOL,
                FUNCTION_TYPE_BOOL,
                null,
                listOf()
            )
        )
        Assertions.assertEquals(expected, VkBuildUtil.buildPort("", string))
    }

    @Test
    fun `bool illegal type`() {
        val string = "@input @output val x = _bool()"
        assertThrowsMessage<LineException>("illegal port type") {
            VkBuildUtil.buildPort("", string)
        }
    }

    @Test
    fun `ubit output`() {
        val string = "@output val x = _ubit(1)"
        val expected = VkPort(
            line(4),
            "x",
            Symbol(6),
            TYPE_UBIT,
            PortType.OUTPUT,
            VkExpressionFunction(
                line(4),
                TYPE_UBIT,
                FUNCTION_TYPE_UBIT,
                null,
                listOf(VkExpressionLiteral(line(4), TYPE_INT, LiteralValue.fromInt(1)))
            )
        )
        Assertions.assertEquals(expected, VkBuildUtil.buildPort("", string))
    }
}