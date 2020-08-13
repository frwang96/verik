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

package io.verik.core.vkx.resolve

import io.verik.core.al.AlRuleParser
import io.verik.core.lang.LangSymbol.TYPE_BOOL
import io.verik.core.lang.LangSymbol.TYPE_INT
import io.verik.core.lang.LangSymbol.TYPE_SINT
import io.verik.core.vkx.VkxType
import io.verik.core.vkx.resolveExpression
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class VkxExpressionResolverTest {

    @Test
    fun `resolve bool type function`() {
        val rule = AlRuleParser.parseExpression("_bool()")
        val expression = resolveExpression(rule)
        Assertions.assertEquals(
                VkxType(TYPE_BOOL, listOf()),
                expression.vkxType
        )
    }

    @Test
    fun `resolve sint type function`() {
        val rule = AlRuleParser.parseExpression("_sint(1)")
        val expression = resolveExpression(rule)
        Assertions.assertEquals(
                VkxType(TYPE_SINT, listOf(1)),
                expression.vkxType
        )
    }

    @Test
    fun `resolve int literal`() {
        val rule = AlRuleParser.parseExpression("0")
        val expression = resolveExpression(rule)
        Assertions.assertEquals(
                VkxType(TYPE_INT, listOf()),
                expression.vkxType
        )
    }
}