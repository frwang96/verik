/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.rs.pass

import verikc.base.ast.ExpressionClass
import verikc.base.ast.ExpressionClass.TYPE
import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.util.LangIdentifierUtil
import verikc.rs.ast.*
import verikc.rs.table.RsSymbolTable

object RsPassRegisterFunction: RsPassBase() {

    override fun passType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        registerFunction(type.typeConstructor, TYPE, scopeSymbol, symbolTable)
        type.instanceConstructor?.let { registerFunction(it, VALUE, scopeSymbol, symbolTable) }
        type.functions.forEach { registerFunction(it, VALUE, type.symbol, symbolTable) }
    }

    override fun passTypeAlias(typeAlias: RsTypeAlias, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        registerFunction(typeAlias.typeConstructor, TYPE, scopeSymbol, symbolTable)
    }

    override fun passFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        registerFunction(function, VALUE, scopeSymbol, symbolTable)
    }

    private fun registerFunction(
        function: RsFunction,
        expressionClass: ExpressionClass,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {
        val argTypeSymbols = function.parameterProperties.map { getTypeSymbol(it, scopeSymbol, symbolTable) }
        symbolTable.addFunction(function, argTypeSymbols, expressionClass, scopeSymbol)
    }

    private fun getTypeSymbol(property: RsProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable): Symbol {
        return when {
            property.typeIdentifier != null -> {
                symbolTable.resolveType(property.typeIdentifier, scopeSymbol, property.line).typeSymbol
            }
            property.expression != null -> {
                val typeIdentifier = if (property.expression is RsExpressionFunction) {
                    LangIdentifierUtil.typeIdentifier(property.expression.identifier)
                        ?: throw LineException("type constructor expression expected", property.expression.line)
                } else throw LineException("type constructor expression expected", property.expression.line)
                symbolTable.resolveType(typeIdentifier, scopeSymbol, property.line).typeSymbol
            }
            else -> throw LineException("could not determine type symbol of property", property.line)
        }
    }
}