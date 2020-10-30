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

import verik.core.base.Symbol
import verik.core.base.SymbolContext
import verik.core.kt.ast.*
import verik.core.kt.symbol.KtSymbolTable

abstract class KtResolverBase {

    fun resolve(compilationUnit: KtCompilationUnit, symbolTable: KtSymbolTable, symbolContext: SymbolContext) {
        symbolContext.processFiles {
            resolveFile(compilationUnit.file(it), symbolTable)
        }
    }

    fun resolveFile(file: KtFile, symbolTable: KtSymbolTable) {
        file.declarations.forEach { resolveDeclaration(it, file.file, symbolTable) }
    }

    fun resolveDeclaration(declaration: KtDeclaration, scope: Symbol, symbolTable: KtSymbolTable) {
        when (declaration) {
            is KtPrimaryType -> resolvePrimaryType(declaration, scope, symbolTable)
            is KtObjectType -> resolveObjectType(declaration, scope, symbolTable)
            is KtPrimaryFunction -> resolvePrimaryFunction(declaration, scope, symbolTable)
            is KtConstructorFunction -> resolveConstructorFunction(declaration, scope, symbolTable)
            is KtPrimaryProperty -> resolvePrimaryProperty(declaration, scope, symbolTable)
            is KtObjectProperty -> resolveObjectProperty(declaration, scope, symbolTable)
            is KtParameterProperty -> resolveParameterProperty(declaration, scope, symbolTable)
            is KtLambdaProperty -> resolveLambdaProperty(declaration, scope, symbolTable)
            is KtEnumProperty -> resolveEnumProperty(declaration, scope, symbolTable)
        }
    }

    protected open fun resolvePrimaryType(primaryType: KtPrimaryType, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveObjectType(objectType: KtObjectType, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolvePrimaryFunction(primaryFunction: KtPrimaryFunction, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveConstructorFunction(constructorFunction: KtConstructorFunction, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolvePrimaryProperty(primaryProperty: KtPrimaryProperty, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveObjectProperty(objectProperty: KtObjectProperty, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveParameterProperty(parameterProperty: KtParameterProperty, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveLambdaProperty(lambdaProperty: KtLambdaProperty, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveEnumProperty(enumProperty: KtEnumProperty, scope: Symbol, symbolTable: KtSymbolTable) {}
}