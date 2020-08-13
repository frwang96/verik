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

package io.verik.core.kt.resolve

import io.verik.core.al.AlRuleParser
import io.verik.core.kt.*
import io.verik.core.lang.LangSymbol.FUN_FINISH
import io.verik.core.lang.LangSymbol.TYPE_UNIT
import io.verik.core.symbol.Symbol
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class KtResolverTest {

    @Test
    fun `resolve declaration function`() {
        val rule = AlRuleParser.parseDeclaration("""
            fun f() {
                finish()
            }
        """.trimIndent())
        val declaration = parseDeclaration(rule)
        KtResolver.resolveDeclaration(declaration)
        val expression = KtExpressionFunction(
                2,
                TYPE_UNIT,
                null,
                "finish",
                listOf(),
                FUN_FINISH
        )
        val expected = KtDeclarationFunction(
                1,
                "f",
                Symbol(1, 1, 1),
                listOf(),
                listOf(),
                "Unit",
                KtBlock(1, listOf(KtStatement(2, expression))),
                TYPE_UNIT
        )
        assertEquals(expected, declaration)
    }
}