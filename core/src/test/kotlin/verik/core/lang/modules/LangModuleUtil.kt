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

import verik.core.base.Symbol
import verik.core.kt.KtDeclarationPrimaryProperty
import verik.core.kt.KtUtil
import verik.core.kt.resolve.KtResolverExpression
import verik.core.kt.symbol.KtSymbolTable
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.rf.*
import verik.core.rf.reify.RfReifierExpression
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.build.SvSourceBuilder
import verik.core.vk.VkExpression

object LangModuleUtil {

    fun buildExpressionWithContext(string: String): String {
        val ktExpression = KtUtil.parseExpression(string)
        KtResolverExpression.resolve(ktExpression, Symbol(1, 1, 0), getContextKtSymbolTable())

        val rfExpression = RfExpression(VkExpression(ktExpression))
        val rfSymbolTable = getContextRfSymbolTable()
        RfReifierExpression.reify(rfExpression, rfSymbolTable)

        return rfExpression.extractAsExpression(rfSymbolTable).build()
    }

    fun buildStatementWithContext(string: String): String {
        val ktExpression = KtUtil.parseExpression(string)
        KtResolverExpression.resolve(ktExpression, Symbol(1, 1, 0), getContextKtSymbolTable())

        val rfExpression = RfExpression(VkExpression(ktExpression))
        val rfSymbolTable = getContextRfSymbolTable()
        RfReifierExpression.reify(rfExpression, rfSymbolTable)

        val svStatement = rfExpression.extract(rfSymbolTable)
        val builder = SvSourceBuilder()
        svStatement.build(builder)

        return builder.toString()
    }

    private fun getContextKtSymbolTable(): KtSymbolTable {
        val symbolTable = KtUtil.getSymbolTable()
        symbolTable.addProperty(
                KtDeclarationPrimaryProperty(
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
                KtDeclarationPrimaryProperty(
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
                KtDeclarationPrimaryProperty(
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
                KtDeclarationPrimaryProperty(
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
                RfReifiedType(TYPE_BOOL, RfTypeClass.INSTANCE, listOf()),
                RfUtil.EXPRESSION_NULL
        ))
        symbolTable.addProperty(RfPrimaryProperty(
                0,
                "b",
                Symbol(1, 1, 2),
                TYPE_BOOL,
                RfReifiedType(TYPE_BOOL, RfTypeClass.INSTANCE, listOf()),
                RfUtil.EXPRESSION_NULL
        ))
        symbolTable.addProperty(RfPrimaryProperty(
                0,
                "x",
                Symbol(1, 1, 3),
                TYPE_UINT,
                RfReifiedType(TYPE_UINT, RfTypeClass.INSTANCE, listOf(8)),
                RfUtil.EXPRESSION_NULL
        ))
        symbolTable.addProperty(RfPrimaryProperty(
                0,
                "y",
                Symbol(1, 1, 4),
                TYPE_UINT,
                RfReifiedType(TYPE_UINT, RfTypeClass.INSTANCE, listOf(8)),
                RfUtil.EXPRESSION_NULL
        ))
        return symbolTable
    }
}