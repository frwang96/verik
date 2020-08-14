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

package io.verik.core.vkx

import io.verik.core.al.AlRuleParser
import io.verik.core.assertThrowsMessage
import io.verik.core.kt.parseDeclaration
import io.verik.core.kt.resolve.KtExpressionResolver
import io.verik.core.lang.LangSymbol
import io.verik.core.lang.LangSymbol.TYPE_BOOL
import io.verik.core.lang.LangSymbol.TYPE_INT
import io.verik.core.lang.LangSymbol.TYPE_UINT
import io.verik.core.main.LineException
import io.verik.core.svx.SvxPort
import io.verik.core.svx.SvxPortType
import io.verik.core.svx.SvxType
import io.verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class VkxPortTest {

    @Test
    fun `bool input`() {
        val rule = AlRuleParser.parseDeclaration("@input val x = _bool()")
        val port = resolvePort(rule)
        val expected = VkxPort(
                1,
                "x",
                Symbol(1, 1, 1),
                VkxPortType.INPUT,
                VkxExpressionFunction(
                        1,
                        TYPE_BOOL,
                        VkxType(TYPE_BOOL, listOf()),
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
        val port = resolvePort(rule)
        val expected = VkxPort(
                1,
                "x",
                Symbol(1, 1, 1),
                VkxPortType.OUTPUT,
                VkxExpressionFunction(
                        1,
                        TYPE_UINT,
                        VkxType(TYPE_UINT, listOf(1)),
                        null,
                        listOf(VkxExpressionLiteral(
                                1,
                                TYPE_INT,
                                VkxType(TYPE_INT, listOf()),
                                "1"
                        )),
                        LangSymbol.FUN_UINT_TYPE
                )
        )
        Assertions.assertEquals(expected, port)
    }

    @Test
    fun `extract bool output`() {
        val rule = AlRuleParser.parseDeclaration("@output val x = _bool()")
        val port = resolvePort(rule).extract()
        val expected = SvxPort(
                1,
                SvxPortType.OUTPUT,
                SvxType("logic", "", ""),
                "x"
        )
        Assertions.assertEquals(expected, port)
    }

    @Test
    fun `extract uint input`() {
        val rule = AlRuleParser.parseDeclaration("@input val x = _uint(8)")
        val port = resolvePort(rule).extract()
        val expected = SvxPort(
                1,
                SvxPortType.INPUT,
                SvxType("logic", "[7:0]", ""),
                "x"
        )
        Assertions.assertEquals(expected, port)
    }
}
