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

package verikc.lang.modules

import verikc.base.ast.Line
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.base.symbol.Symbol
import verikc.kt.KtxUtil
import verikc.kt.resolve.KtResolverExpression
import verikc.kt.symbol.KtSymbolTable
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.ps.ast.PsExpression
import verikc.ps.ast.PsPrimaryProperty
import verikc.ps.symbol.PsSymbolTable
import verikc.rf.RfUtil
import verikc.rf.ast.RfExpression
import verikc.rf.ast.RfPrimaryProperty
import verikc.rf.reify.RfReifierExpression
import verikc.rf.symbol.RfSymbolTable
import verikc.sv.build.SvSourceBuilder
import verikc.vk.ast.VkExpression

object LangModuleUtil {

    fun buildExpressionWithContext(string: String): String {
        val ktExpression = KtxUtil.parseExpression(string)
        KtResolverExpression.resolve(ktExpression, Symbol(2), getContextKtSymbolTable())

        val rfExpression = RfExpression(VkExpression(ktExpression))
        RfReifierExpression.reify(rfExpression, getContextRfSymbolTable())

        val psExpression = PsExpression(rfExpression)
        val svExpression = psExpression.extract(getContextPsSymbolTable())

        val builder = SvSourceBuilder()
        svExpression.build(builder)
        return builder.toString()
    }

    private fun getContextKtSymbolTable(): KtSymbolTable {
        val string = """
            val a = _bool()
            val b = _bool()
            val x = _ubit(8)
            val y = _ubit(8)
        """.trimIndent()
        return KtxUtil.buildSymbolTable(string)
    }

    private fun getContextRfSymbolTable(): RfSymbolTable {
        val symbolTable = RfSymbolTable()
        symbolTable.addProperty(
            RfPrimaryProperty(
                Line(0),
                "a",
                Symbol(3),
                TYPE_BOOL,
                TypeReified(TYPE_BOOL, INSTANCE, listOf()),
                RfUtil.EXPRESSION_NULL
            )
        )
        symbolTable.addProperty(
            RfPrimaryProperty(
                Line(0),
                "b",
                Symbol(4),
                TYPE_BOOL,
                TypeReified(TYPE_BOOL, INSTANCE, listOf()),
                RfUtil.EXPRESSION_NULL
            )
        )
        symbolTable.addProperty(
            RfPrimaryProperty(
                Line(0),
                "x",
                Symbol(5),
                TYPE_UBIT,
                TypeReified(TYPE_UBIT, INSTANCE, listOf(8)),
                RfUtil.EXPRESSION_NULL
            )
        )
        symbolTable.addProperty(
            RfPrimaryProperty(
                Line(0),
                "y",
                Symbol(6),
                TYPE_UBIT,
                TypeReified(TYPE_UBIT, INSTANCE, listOf(8)),
                RfUtil.EXPRESSION_NULL
            )
        )
        return symbolTable
    }

    private fun getContextPsSymbolTable(): PsSymbolTable {
        val symbolTable = PsSymbolTable()
        symbolTable.addProperty(
            PsPrimaryProperty(
                Line(0),
                "a",
                Symbol(3),
                TypeReified(TYPE_BOOL, INSTANCE, listOf())
            )
        )
        symbolTable.addProperty(
            PsPrimaryProperty(
                Line(0),
                "b",
                Symbol(4),
                TypeReified(TYPE_BOOL, INSTANCE, listOf())
            )
        )
        symbolTable.addProperty(
            PsPrimaryProperty(
                Line(0),
                "x",
                Symbol(5),
                TypeReified(TYPE_UBIT, INSTANCE, listOf(8))
            )
        )
        symbolTable.addProperty(
            PsPrimaryProperty(
                Line(0),
                "y",
                Symbol(6),
                TypeReified(TYPE_UBIT, INSTANCE, listOf(8))
            )
        )
        return symbolTable
    }
}
