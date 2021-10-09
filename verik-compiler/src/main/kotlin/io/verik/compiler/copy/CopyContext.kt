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
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.property.Type

class CopyContext {

    var typeParameterContext = TypeParameterContext.EMPTY

    private val referenceForwardingMap = ReferenceForwardingMap()

    fun set(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        referenceForwardingMap.set(oldDeclaration, typeParameterContext, newDeclaration)
    }

    fun get(declaration: Declaration): Declaration? {
        return referenceForwardingMap.get(declaration, typeParameterContext)
    }

    fun getNotNull(declaration: EDeclaration): Declaration {
        return referenceForwardingMap.getNotNull(declaration, typeParameterContext)
    }

    fun contains(declarationBinding: DeclarationBinding): Boolean {
        return referenceForwardingMap.contains(declarationBinding)
    }

    fun <E : EElement> copy(element: E): E {
        return ElementCopier.copy(element, this)
    }

    fun copy(type: Type): Type {
        val arguments = type.arguments.map { copy(it) }
        val reference = referenceForwardingMap.get(type.reference, typeParameterContext) ?: type.reference
        return Type(reference, ArrayList(arguments))
    }
}
