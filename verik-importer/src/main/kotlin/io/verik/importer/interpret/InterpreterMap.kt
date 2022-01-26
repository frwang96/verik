/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.interpret

import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.common.Declaration
import io.verik.importer.message.Messages

class InterpreterMap {

    private val interpreterMap = HashMap<SvDeclaration, KtDeclaration>()

    fun addDeclaration(declaration: SvDeclaration, mappedDeclaration: KtDeclaration) {
        if (declaration in interpreterMap) {
            Messages.INTERNAL_ERROR.on(
                declaration,
                "Declaration has already been added to interpreter map: ${declaration.name}"
            )
        }
        interpreterMap[declaration] = mappedDeclaration
    }

    fun getDeclaration(declaration: Declaration): Declaration {
        return if (declaration is SvDeclaration) {
            val mappedDeclaration = interpreterMap[declaration]
                ?: Messages.INTERNAL_ERROR.on(
                    declaration,
                    "Unable to find declaration in interpreter map: ${declaration.name}"
                )
            mappedDeclaration
        } else {
            declaration
        }
    }
}
