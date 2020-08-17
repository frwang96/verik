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

package verik.core.kt.resolve

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verik.core.al.AlRuleParser
import verik.core.kt.KtDeclarationBaseProperty
import verik.core.kt.KtExpressionLiteral
import verik.core.kt.KtExpressionProperty
import verik.core.kt.KtUtil
import verik.core.kt.symbol.KtSymbolTable
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.main.symbol.Symbol

internal class KtResolverExpressionTest {

    @Test
    fun `bool type function`() {
        val rule = AlRuleParser.parseExpression("_bool()")
        val expression = KtUtil.resolveExpression(rule)
        assertEquals(TYPE_BOOL, expression.type)
    }

    @Test
    fun `uint type function`() {
        val rule = AlRuleParser.parseExpression("_uint(1)")
        val expression = KtUtil.resolveExpression(rule)
        assertEquals(TYPE_UINT, expression.type)
    }

    @Test
    fun `bool literal`() {
        val rule = AlRuleParser.parseExpression("true")
        val expression = KtUtil.resolveExpression(rule)
        assertEquals(TYPE_BOOL, expression.type)
    }

    @Test
    fun `int literal`() {
        val rule = AlRuleParser.parseExpression("0")
        val expression = KtUtil.resolveExpression(rule)
        assertEquals(TYPE_INT, expression.type)
    }

    @Test
    fun `symbol simple`() {
        val property = KtDeclarationBaseProperty(
                0,
                "x",
                Symbol(1, 1, 1),
                TYPE_INT,
                listOf(),
                KtExpressionLiteral(1, null, "0")
        )
        val symbolTable = KtSymbolTable()
        symbolTable.addPkg(Symbol(1, 0, 0))
        symbolTable.addFile(Symbol(1, 1, 0))
        symbolTable.addProperty(property, Symbol(1, 1, 0), 0)
        val rule = AlRuleParser.parseExpression("x")
        val expression = KtUtil.resolveExpression(rule, Symbol(1, 1, 0), symbolTable)
        assertEquals(
                (expression as KtExpressionProperty).property,
                property.symbol
        )
    }
}