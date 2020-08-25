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

    fun resolveDeclaration(declaration: KtDeclaration, parent: Symbol, symbolTable: KtSymbolTable) {
        when (declaration) {
            is KtDeclarationType -> resolveType(declaration, parent, symbolTable)
            is KtDeclarationFunction -> resolveFunction(declaration, parent, symbolTable)
            is KtDeclarationPrimaryProperty -> resolvePrimaryProperty(declaration, parent, symbolTable)
            is KtDeclarationParameter -> resolveParameter(declaration, parent, symbolTable)
            is KtDeclarationEnumEntry -> resolveEnumEntry(declaration, parent, symbolTable)
        }
    }

    protected open fun resolveType(type: KtDeclarationType, parent: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveFunction(function: KtDeclarationFunction, parent: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolvePrimaryProperty(primaryProperty: KtDeclarationPrimaryProperty, parent: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveParameter(parameter: KtDeclarationParameter, parent: Symbol, symbolTable: KtSymbolTable) {}

    protected open fun resolveEnumEntry(enumEntry: KtDeclarationEnumEntry, parent: Symbol, symbolTable: KtSymbolTable) {}
}