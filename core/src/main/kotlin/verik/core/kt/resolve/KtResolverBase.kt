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
import verik.core.kt.*
import verik.core.kt.symbol.KtSymbolTable

abstract class KtResolverBase {

    fun resolveFile(file: KtFile, symbolTable: KtSymbolTable) {
        file.declarations.forEach { resolveDeclaration(it, file.file, symbolTable) }
    }

    fun resolveDeclaration(declaration: KtDeclaration, scope: Symbol, symbolTable: KtSymbolTable) {
        when (declaration) {
            is KtDeclarationType -> resolveType(declaration, scope, symbolTable)
            is KtDeclarationFunction -> resolveFunction(declaration, scope, symbolTable)
            is KtDeclarationPrimaryProperty -> resolvePrimaryProperty(declaration, scope, symbolTable)
            is KtDeclarationLambdaProperty -> resolveLambdaProperty(declaration, scope, symbolTable)
            is KtDeclarationParameter -> resolveParameter(declaration, scope, symbolTable)
            is KtDeclarationEnumEntry -> resolveEnumEntry(declaration, scope, symbolTable)
        }
    }

    protected open fun resolveType(type: KtDeclarationType, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveFunction(function: KtDeclarationFunction, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolvePrimaryProperty(primaryProperty: KtDeclarationPrimaryProperty, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveLambdaProperty(lambdaProperty: KtDeclarationLambdaProperty, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveParameter(parameter: KtDeclarationParameter, scope: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveEnumEntry(enumEntry: KtDeclarationEnumEntry, scope: Symbol, symbolTable: KtSymbolTable) {}
}