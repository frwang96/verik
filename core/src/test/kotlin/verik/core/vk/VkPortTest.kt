/*
 * Copyright 2020 Francis Wang
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

package verik.core.vk

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.assertThrowsMessage
import verik.core.kt.KtUtil
import verik.core.lang.LangSymbol.FUNCTION_BOOL
import verik.core.lang.LangSymbol.FUNCTION_UINT
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.base.LineException
import verik.core.base.Symbol

internal class VkPortTest {

    @Test
    fun `bool input`() {
        val rule = AlRuleParser.parseDeclaration("@input val x = _bool()")
        val port = VkUtil.parsePort(rule)
        val expected = VkPort(
                1,
                "x",
                Symbol(1, 1, 1),
                TYPE_BOOL,
                VkPortType.INPUT,
                VkExpressionFunction(
                        1,
                        TYPE_BOOL,
                        FUNCTION_BOOL,
                        null,
                        listOf()
                )
        )
        Assertions.assertEquals(expected, port)
    }

    @Test
    fun `bool input illegal type`() {
        val rule = AlRuleParser.parseDeclaration("@wire val x = _bool()")
        val declaration = KtUtil.resolveDeclaration(rule)
        Assertions.assertFalse(VkPort.isPort(declaration))
        assertThrowsMessage<LineException>("illegal port type") {
            VkPort(declaration)
        }
    }

    @Test
    fun `uint output`() {
        val rule = AlRuleParser.parseDeclaration("@output val x = _uint(1)")
        val port = VkUtil.parsePort(rule)
        val expected = VkPort(
                1,
                "x",
                Symbol(1, 1, 1),
                TYPE_UINT,
                VkPortType.OUTPUT,
                VkExpressionFunction(
                        1,
                        TYPE_UINT,
                        FUNCTION_UINT,
                        null,
                        listOf(VkExpressionLiteral(1, TYPE_INT, false, 1, 1))
                )
        )
        Assertions.assertEquals(expected, port)
    }
}
