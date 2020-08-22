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

import verik.core.kt.KtFile
import verik.core.kt.symbol.KtSymbolTable

object KtResolver {

    fun resolve(file: KtFile, symbolTable: KtSymbolTable) {
        KtResolverType.resolveFile(file, symbolTable)
        KtResolverFunction.resolveFile(file, symbolTable)
        KtResolverProperty.resolveFile(file, symbolTable)
        KtResolverStatement.resolveFile(file, symbolTable)
    }
}