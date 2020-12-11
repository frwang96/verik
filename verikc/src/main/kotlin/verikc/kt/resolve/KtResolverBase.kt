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

package verikc.kt.resolve

import verikc.base.ast.Symbol
import verikc.kt.ast.*
import verikc.kt.symbol.KtSymbolTable

abstract class KtResolverBase {

    fun resolve(compilationUnit: KtCompilationUnit, symbolTable: KtSymbolTable) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                resolveFile(file, symbolTable)
            }
        }
    }

    fun resolveFile(file: KtFile, symbolTable: KtSymbolTable) {
        file.declarations.forEach { resolveDeclaration(it, file.fileSymbol, symbolTable) }
    }

    fun resolveDeclaration(declaration: KtDeclaration, scopeSymbol: Symbol, symbolTable: KtSymbolTable) {
        when (declaration) {
            is KtPrimaryType -> resolvePrimaryType(declaration, scopeSymbol, symbolTable)
            is KtObjectType -> resolveObjectType(declaration, scopeSymbol, symbolTable)
            is KtPrimaryFunction -> resolvePrimaryFunction(declaration, scopeSymbol, symbolTable)
            is KtConstructorFunction -> resolveConstructorFunction(declaration, scopeSymbol, symbolTable)
            is KtPrimaryProperty -> resolvePrimaryProperty(declaration, scopeSymbol, symbolTable)
            is KtObjectProperty -> resolveObjectProperty(declaration, scopeSymbol, symbolTable)
            is KtParameterProperty -> resolveParameterProperty(declaration, scopeSymbol, symbolTable)
            is KtLambdaProperty -> resolveLambdaProperty(declaration, scopeSymbol, symbolTable)
            is KtEnumProperty -> resolveEnumProperty(declaration, scopeSymbol, symbolTable)
        }
    }

    protected open fun resolvePrimaryType(
        primaryType: KtPrimaryType,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {}

    protected open fun resolveObjectType(
        objectType: KtObjectType,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {}

    protected open fun resolvePrimaryFunction(
        primaryFunction: KtPrimaryFunction,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
    }

    protected open fun resolveConstructorFunction(
        constructorFunction: KtConstructorFunction,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
    }

    protected open fun resolvePrimaryProperty(
        primaryProperty: KtPrimaryProperty,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
    }

    protected open fun resolveObjectProperty(
        objectProperty: KtObjectProperty,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
    }

    protected open fun resolveParameterProperty(
        parameterProperty: KtParameterProperty,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
    }

    protected open fun resolveLambdaProperty(
        lambdaProperty: KtLambdaProperty,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {
    }

    protected open fun resolveEnumProperty(
        enumProperty: KtEnumProperty,
        scopeSymbol: Symbol,
        symbolTable: KtSymbolTable
    ) {}
}
