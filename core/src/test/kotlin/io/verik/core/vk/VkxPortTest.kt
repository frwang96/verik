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

package io.verik.core.vk

import io.verik.core.LineException
import io.verik.core.al.AlRuleParser
import io.verik.core.assert.assertThrowsMessage
import io.verik.core.kt.KtDeclaration
import io.verik.core.kt.resolve.KtSymbolIndexer
import io.verik.core.kt.resolve.KtSymbolMap
import io.verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class VkxPortTest {

    @Test
    fun `bool input`() {
        val rule = AlRuleParser.parseDeclaration("@input val x = _bool()")
        val declaration = KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1)))
        val property = VkxPort(declaration)
        val expected = VkxPort(
                1,
                "x",
                Symbol(1, 1, 1),
                VkxPortType.INPUT,
                VkxExpressionFunction(1, null, null, null, listOf(), null)
        )
        Assertions.assertEquals(expected, property)
    }

    @Test
    fun `bool input illegal type`() {
        val rule = AlRuleParser.parseDeclaration("@wire val x = _bool()")
        val declaration = KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1)))
        Assertions.assertFalse(VkxPort.isPort(declaration))
        assertThrowsMessage<LineException>("illegal port type") {
            VkxPort(declaration)
        }
    }

    @Test
    fun `uint output`() {
        val rule = AlRuleParser.parseDeclaration("@output val x = _uint(1)")
        val declaration = KtDeclaration(rule, KtSymbolMap(), KtSymbolIndexer(Symbol(1, 1)))
        val property = VkxPort(declaration)
        val expected = VkxPort(
                1,
                "x",
                Symbol(1, 1, 1),
                VkxPortType.OUTPUT,
                VkxExpressionFunction(1, null, null, null, listOf(VkxExpressionLiteral(1, null, null, "1")), null)
        )
        Assertions.assertEquals(expected, property)
    }
}
