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

package io.verik.compiler.copy

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.message.Messages

class ReferenceForwardingMap {

    private val referenceForwardingMap = HashMap<DeclarationBinding, EDeclaration>()

    fun set(oldDeclaration: EDeclaration, typeParameterContext: TypeParameterContext, newDeclaration: EDeclaration) {
        referenceForwardingMap[DeclarationBinding(oldDeclaration, typeParameterContext)] = newDeclaration
    }

    fun get(declaration: Declaration, typeParameterContext: TypeParameterContext): Declaration? {
        return if (declaration is EDeclaration) {
            referenceForwardingMap[DeclarationBinding(declaration, typeParameterContext)]
        } else null
    }

    fun getNotNull(declaration: EDeclaration, typeParameterContext: TypeParameterContext): Declaration {
        val forwardedDeclaration = referenceForwardingMap[DeclarationBinding(declaration, typeParameterContext)]
        return if (forwardedDeclaration != null) {
            forwardedDeclaration
        } else {
            Messages.INTERNAL_ERROR.on(declaration, "Forwarded declaration not found: ${declaration.name}")
            declaration
        }
    }

    fun getTypeParameterContexts(declaration: EDeclaration): List<TypeParameterContext> {
        val typeParameterContexts = ArrayList<TypeParameterContext>()
        referenceForwardingMap.keys.forEach {
            if (it.declaration == declaration)
                typeParameterContexts.add(it.typeParameterContext)
        }
        return typeParameterContexts
    }

    fun contains(declarationBinding: DeclarationBinding): Boolean {
        return declarationBinding in referenceForwardingMap
    }
}
