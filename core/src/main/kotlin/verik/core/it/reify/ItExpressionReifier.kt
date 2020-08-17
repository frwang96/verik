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

package verik.core.it.reify

import verik.core.it.*
import verik.core.it.symbol.ItSymbolTable
import verik.core.lang.Lang
import verik.core.lang.LangSymbol.TYPE_BOOL
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.main.LineException

object ItExpressionReifier {

    fun reifyFile(file: ItFile, symbolTable: ItSymbolTable) {
        file.declarations.forEach { reifyDeclaration(it, symbolTable) }
    }

    fun reifyDeclaration(declaration: ItDeclaration, symbolTable: ItSymbolTable) {
        when (declaration) {
            is ItModule -> reifyModule(declaration, symbolTable)
            is ItActionBlock -> reifyActionBlock(declaration, symbolTable)
        }
    }

    private fun reifyModule(module: ItModule, symbolTable: ItSymbolTable) {
        module.actionBlocks.map { reifyDeclaration(it, symbolTable) }
    }

    private fun reifyActionBlock(actionBlock: ItActionBlock, symbolTable: ItSymbolTable) {
        actionBlock.block.statements.map { reifyExpression(it.expression, symbolTable) }
    }

    fun reifyExpression(expression: ItExpression, symbolTable: ItSymbolTable) {
        when (expression) {
            is ItExpressionFunction -> reifyExpressionFunction(expression, symbolTable)
            is ItExpressionOperator -> throw LineException("reification of operator expression not supported", expression)
            is ItExpressionProperty -> reifyExpressionProperty(expression, symbolTable)
            is ItExpressionString -> throw LineException("reification of string expression not supported", expression)
            is ItExpressionLiteral -> reifyExpressionLiteral(expression)
        }
        if (expression.typeReified == null) {
            throw LineException("could not reify expression", expression)
        }
    }

    private fun reifyExpressionFunction(expression: ItExpressionFunction, symbolTable: ItSymbolTable) {
        expression.target?.let { reifyExpression(it, symbolTable) }
        expression.args.map { reifyExpression(it, symbolTable) }
        Lang.functionTable.reify(expression)
    }

    private fun reifyExpressionProperty(expression: ItExpressionProperty, symbolTable: ItSymbolTable) {
        if (expression.target != null) {
            throw LineException("reification of property with target expression not supported", expression)
        }
        val resolvedProperty = symbolTable.getProperty(expression)
        val typeReified = resolvedProperty.typeReified
                ?: throw LineException("property has not been reified", expression)
        expression.typeReified = typeReified
    }

    private fun reifyExpressionLiteral(expression: ItExpressionLiteral) {
        expression.typeReified = when (expression.type) {
            TYPE_BOOL -> ItTypeReified(TYPE_BOOL, ItTypeClass.INSTANCE, listOf())
            TYPE_INT -> ItTypeReified(TYPE_INT, ItTypeClass.INT, listOf())
            else -> throw LineException("bool or Int type expected", expression)
        }
    }
}