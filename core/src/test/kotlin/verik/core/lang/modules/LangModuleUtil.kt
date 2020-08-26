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
import verik.core.it.*
import verik.core.it.reify.ItReifierExpression
import verik.core.it.symbol.ItSymbolTable
import verik.core.kt.KtDeclarationPrimaryProperty
import verik.core.kt.KtUtil
import verik.core.kt.resolve.KtResolverExpression
import verik.core.kt.symbol.KtSymbolTable
import verik.core.kt.symbol.KtSymbolTableBuilder
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_UINT
import verik.core.sv.build.SvSourceBuilder
import verik.core.vk.VkExpression

object LangModuleUtil {

    fun buildExpressionWithContext(string: String): String {
        val ktExpression = KtUtil.parseExpression(string)
        KtResolverExpression.resolve(ktExpression, Symbol(1, 1, 0), getContextKtSymbolTable())

        val itExpression = ItExpression(VkExpression(ktExpression))
        val itSymbolTable = getContextItSymbolTable()
        ItReifierExpression.reify(itExpression, itSymbolTable)

        return itExpression.extractAsExpression(itSymbolTable).build()
    }

    fun buildStatementWithContext(string: String): String {
        val ktExpression = KtUtil.parseExpression(string)
        KtResolverExpression.resolve(ktExpression, Symbol(1, 1, 0), getContextKtSymbolTable())

        val itExpression = ItExpression(VkExpression(ktExpression))
        val itSymbolTable = getContextItSymbolTable()
        ItReifierExpression.reify(itExpression, itSymbolTable)

        val svStatement = itExpression.extract(itSymbolTable)
        val builder = SvSourceBuilder()
        svStatement.build(builder)

        return builder.toString()
    }

    private fun getContextKtSymbolTable(): KtSymbolTable {
        val ktSymbolTable = KtSymbolTableBuilder.build(KtUtil.getSymbolContext())
        ktSymbolTable.addProperty(
                KtDeclarationPrimaryProperty(
                        0,
                        "a",
                        Symbol(1, 1, 1),
                        TYPE_BOOL,
                        listOf(),
                        KtUtil.EXPRESSION_NULL
                ),
                Symbol(1, 1, 0),
                0
        )
        ktSymbolTable.addProperty(
                KtDeclarationPrimaryProperty(
                        0,
                        "b",
                        Symbol(1, 1, 2),
                        TYPE_BOOL,
                        listOf(),
                        KtUtil.EXPRESSION_NULL
                ),
                Symbol(1, 1, 0),
                0
        )
        ktSymbolTable.addProperty(
                KtDeclarationPrimaryProperty(
                        0,
                        "x",
                        Symbol(1, 1, 3),
                        TYPE_UINT,
                        listOf(),
                        KtUtil.EXPRESSION_NULL
                ),
                Symbol(1, 1, 0),
                0
        )
        ktSymbolTable.addProperty(
                KtDeclarationPrimaryProperty(
                        0,
                        "y",
                        Symbol(1, 1, 4),
                        TYPE_UINT,
                        listOf(),
                        KtUtil.EXPRESSION_NULL
                ),
                Symbol(1, 1, 0),
                0
        )
        return ktSymbolTable
    }

    private fun getContextItSymbolTable(): ItSymbolTable {
        val itSymbolTable = ItSymbolTable()
        itSymbolTable.addProperty(ItPrimaryProperty(
                0,
                "a",
                Symbol(1, 1, 1),
                TYPE_BOOL,
                ItReifiedType(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()),
                ItUtil.EXPRESSION_NULL
        ))
        itSymbolTable.addProperty(ItPrimaryProperty(
                0,
                "b",
                Symbol(1, 1, 2),
                TYPE_BOOL,
                ItReifiedType(TYPE_BOOL, ItTypeClass.INSTANCE, listOf()),
                ItUtil.EXPRESSION_NULL
        ))
        itSymbolTable.addProperty(ItPrimaryProperty(
                0,
                "x",
                Symbol(1, 1, 3),
                TYPE_UINT,
                ItReifiedType(TYPE_UINT, ItTypeClass.INSTANCE, listOf(8)),
                ItUtil.EXPRESSION_NULL
        ))
        itSymbolTable.addProperty(ItPrimaryProperty(
                0,
                "y",
                Symbol(1, 1, 4),
                TYPE_UINT,
                ItReifiedType(TYPE_UINT, ItTypeClass.INSTANCE, listOf(8)),
                ItUtil.EXPRESSION_NULL
        ))
        return itSymbolTable
    }
}