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

import verik.core.kt.KtDeclaration
import verik.core.kt.KtDeclarationFunction
import verik.core.kt.KtDeclarationType
import verik.core.kt.KtFile
import verik.core.lang.Lang
import verik.core.main.LineException

object KtFunctionResolver {

    fun resolveFile(file: KtFile) {
        file.declarations.forEach { resolveDeclaration(it) }
    }

    fun resolveDeclaration(declaration: KtDeclaration) {
        if (declaration is KtDeclarationType) {
            declaration.declarations.forEach { resolveDeclaration(it) }
        }
        if (declaration is KtDeclarationFunction) {
            resolveFunction(declaration)
        }
    }

    private fun resolveFunction(function: KtDeclarationFunction) {
        function.type = Lang.typeTable.resolve(function.typeIdentifier)
                ?: throw LineException("could not resolve return type ${function.identifier}", function)
    }
}