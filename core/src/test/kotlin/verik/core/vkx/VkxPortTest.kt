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

package verik.core.vkx

import verik.core.al.AlRuleParser
import verik.core.assertThrowsMessage
import verik.core.kt.parseDeclaration
import verik.core.kt.resolve.KtExpressionResolver
import verik.core.lang.LangSymbol
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.main.LineException
import verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class VkxPortTest {

    @Test
    fun `bool input`() {
        val rule = AlRuleParser.parseDeclaration("@input val x = _bool()")
        val port = parsePort(rule)
        val expected = VkxPort(
                1,
                "x",
                Symbol(1, 1, 1),
                VkxPortType.INPUT,
                VkxExpressionFunction(
                        1,
                        TYPE_BOOL,
                        null,
                        listOf(),
                        LangSymbol.FUN_BOOL_TYPE
                )
        )
        Assertions.assertEquals(expected, port)
    }

    @Test
    fun `bool input illegal type`() {
        val rule = AlRuleParser.parseDeclaration("@wire val x = _bool()")
        val declaration = parseDeclaration(rule)
        KtExpressionResolver.resolveDeclaration(declaration)
        Assertions.assertFalse(VkxPort.isPort(declaration))
        assertThrowsMessage<LineException>("illegal port type") {
            VkxPort(declaration)
        }
    }

    @Test
    fun `uint output`() {
        val rule = AlRuleParser.parseDeclaration("@output val x = _uint(1)")
        val port = parsePort(rule)
        val expected = VkxPort(
                1,
                "x",
                Symbol(1, 1, 1),
                VkxPortType.OUTPUT,
                VkxExpressionFunction(
                        1,
                        TYPE_UINT,
                        null,
                        listOf(VkxExpressionLiteral(
                                1,
                                TYPE_INT,
                                "1"
                        )),
                        LangSymbol.FUN_UINT_TYPE
                )
        )
        Assertions.assertEquals(expected, port)
    }
}
