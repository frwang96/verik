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

package verikc.rf.reify

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.Line
import verikc.base.ast.PortType
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_REIFIED_UNIT
import verikc.lang.LangSymbol.TYPE_STRING
import verikc.rf.RfUtil
import verikc.rf.RfxUtil
import verikc.rf.ast.RfExpressionProperty
import verikc.rf.ast.RfExpressionString
import verikc.rf.ast.RfPort
import verikc.rf.symbol.RfSymbolTable

internal class RfReifierExpressionTest {

    @Test
    fun `function finish`() {
        val string = """
            finish()
        """.trimIndent()
        assertEquals(
            TYPE_REIFIED_UNIT,
            RfxUtil.reifyExpression(string).typeReified
        )
    }

    @Test
    fun `operator forever`() {
        val string = """
            forever {}
        """.trimIndent()
        assertEquals(
            TYPE_REIFIED_UNIT,
            RfxUtil.reifyExpression(string).typeReified
        )
    }

    @Test
    fun `property bool`() {
        val expression = RfExpressionProperty(
            Line(0),
            TYPE_BOOL,
            null,
            Symbol(3),
            null
        )
        val symbolTable = RfSymbolTable()
        symbolTable.addProperty(
            RfPort(
                Line(0),
                "x",
                Symbol(3),
                TYPE_BOOL,
                TypeReified(TYPE_BOOL, INSTANCE, listOf()),
                PortType.INPUT,
                RfUtil.EXPRESSION_NULL
            )
        )
        RfReifierExpression.reify(expression, symbolTable)
        assertEquals(
            TypeReified(TYPE_BOOL, INSTANCE, listOf()),
            expression.typeReified
        )
    }

    @Test
    fun `string literal`() {
        val expression = RfExpressionString(
            Line(0),
            TYPE_STRING,
            null,
            listOf()
        )
        RfReifierExpression.reify(expression, RfSymbolTable())
        assertEquals(
            TypeReified(TYPE_STRING, INSTANCE, listOf()),
            expression.typeReified
        )
    }
}
