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

package verik.core.lang.modules

import verik.core.base.ast.ReifiedType
import verik.core.base.ast.Symbol
import verik.core.base.ast.TypeClass.INSTANCE
import verik.core.kt.KtUtil
import verik.core.kt.ast.KtPrimaryProperty
import verik.core.kt.resolve.KtResolverExpression
import verik.core.kt.symbol.KtSymbolTable
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.ps.ast.PsExpression
import verik.core.ps.ast.PsPrimaryProperty
import verik.core.ps.symbol.PsSymbolTable
import verik.core.rf.RfUtil
import verik.core.rf.ast.RfExpression
import verik.core.rf.ast.RfPrimaryProperty
import verik.core.rf.reify.RfReifierExpression
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.build.SvSimpleExpressionBuilder
import verik.core.sv.build.SvSourceBuilder
import verik.core.vk.ast.VkExpression

object LangModuleUtil {

    fun buildExpressionWithContext(string: String): String {
        val ktExpression = KtUtil.parseExpression(string)
        KtResolverExpression.resolve(ktExpression, Symbol(1, 1, 0), getContextKtSymbolTable())

        val rfExpression = RfExpression(VkExpression(ktExpression))
        RfReifierExpression.reify(rfExpression, getContextRfSymbolTable())

        val psExpression = PsExpression(rfExpression)
        return SvSimpleExpressionBuilder.build(psExpression.extractAsExpression(getContextPsSymbolTable()))
    }

    fun buildStatementWithContext(string: String): String {
        val ktExpression = KtUtil.parseExpression(string)
        KtResolverExpression.resolve(ktExpression, Symbol(1, 1, 0), getContextKtSymbolTable())

        val rfExpression = RfExpression(VkExpression(ktExpression))
        RfReifierExpression.reify(rfExpression, getContextRfSymbolTable())

        val psExpression = PsExpression(rfExpression)
        val svStatement = psExpression.extract(getContextPsSymbolTable())
        val builder = SvSourceBuilder()
        svStatement.build(builder)
        return builder.toString()
    }

    private fun getContextKtSymbolTable(): KtSymbolTable {
        val symbolTable = KtUtil.getSymbolTable()
        symbolTable.addProperty(
                KtPrimaryProperty(
                        0,
                        "a",
                        Symbol(1, 1, 1),
                        TYPE_BOOL,
                        listOf(),
                        KtUtil.EXPRESSION_NULL
                ),
                Symbol(1, 1, 0)
        )
        symbolTable.addProperty(
                KtPrimaryProperty(
                        0,
                        "b",
                        Symbol(1, 1, 2),
                        TYPE_BOOL,
                        listOf(),
                        KtUtil.EXPRESSION_NULL
                ),
                Symbol(1, 1, 0)
        )
        symbolTable.addProperty(
                KtPrimaryProperty(
                        0,
                        "x",
                        Symbol(1, 1, 3),
                        TYPE_UINT,
                        listOf(),
                        KtUtil.EXPRESSION_NULL
                ),
                Symbol(1, 1, 0)
        )
        symbolTable.addProperty(
                KtPrimaryProperty(
                        0,
                        "y",
                        Symbol(1, 1, 4),
                        TYPE_UINT,
                        listOf(),
                        KtUtil.EXPRESSION_NULL
                ),
                Symbol(1, 1, 0)
        )
        return symbolTable
    }

    private fun getContextRfSymbolTable(): RfSymbolTable {
        val symbolTable = RfSymbolTable()
        symbolTable.addProperty(RfPrimaryProperty(
                0,
                "a",
                Symbol(1, 1, 1),
                TYPE_BOOL,
                ReifiedType(TYPE_BOOL, INSTANCE, listOf()),
                RfUtil.EXPRESSION_NULL
        ))
        symbolTable.addProperty(RfPrimaryProperty(
                0,
                "b",
                Symbol(1, 1, 2),
                TYPE_BOOL,
                ReifiedType(TYPE_BOOL, INSTANCE, listOf()),
                RfUtil.EXPRESSION_NULL
        ))
        symbolTable.addProperty(RfPrimaryProperty(
                0,
                "x",
                Symbol(1, 1, 3),
                TYPE_UINT,
                ReifiedType(TYPE_UINT, INSTANCE, listOf(8)),
                RfUtil.EXPRESSION_NULL
        ))
        symbolTable.addProperty(RfPrimaryProperty(
                0,
                "y",
                Symbol(1, 1, 4),
                TYPE_UINT,
                ReifiedType(TYPE_UINT, INSTANCE, listOf(8)),
                RfUtil.EXPRESSION_NULL
        ))
        return symbolTable
    }

    private fun getContextPsSymbolTable(): PsSymbolTable {
        val symbolTable = PsSymbolTable()
        symbolTable.addProperty(PsPrimaryProperty(
                0,
                "a",
                Symbol(1, 1, 1),
                ReifiedType(TYPE_BOOL, INSTANCE, listOf())
        ))
        symbolTable.addProperty(PsPrimaryProperty(
                0,
                "b",
                Symbol(1, 1, 2),
                ReifiedType(TYPE_BOOL, INSTANCE, listOf())
        ))
        symbolTable.addProperty(PsPrimaryProperty(
                0,
                "x",
                Symbol(1, 1, 3),
                ReifiedType(TYPE_UINT, INSTANCE, listOf(8))
        ))
        symbolTable.addProperty(PsPrimaryProperty(
                0,
                "y",
                Symbol(1, 1, 4),
                ReifiedType(TYPE_UINT, INSTANCE, listOf(8))
        ))
        return symbolTable
    }
}