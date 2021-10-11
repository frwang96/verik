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
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.property.Type
import io.verik.compiler.message.Messages

class CopyContext {

    var typeParameterContext = TypeParameterContext.EMPTY

    private val referenceForwardingMap = ReferenceForwardingMap()

    fun bind(type: Type, element: EElement) {
        typeParameterContext.bind(type, element)
    }

    fun set(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        referenceForwardingMap.set(oldDeclaration, typeParameterContext, newDeclaration)
    }

    fun get(declaration: Declaration): Declaration? {
        return referenceForwardingMap.get(declaration, typeParameterContext)
    }

    fun get(declaration: EDeclaration, typeParameterContext: TypeParameterContext): Declaration? {
        return referenceForwardingMap.get(declaration, typeParameterContext)
    }

    fun getNotNull(declaration: EDeclaration): Declaration {
        return referenceForwardingMap.getNotNull(declaration, typeParameterContext)
    }

    fun getTypeParameterContexts(declaration: EDeclaration): List<TypeParameterContext> {
        return referenceForwardingMap.getTypeParameterContexts(declaration)
    }

    fun contains(declarationBinding: DeclarationBinding): Boolean {
        return referenceForwardingMap.contains(declarationBinding)
    }

    fun <E : EElement> copy(element: E): E {
        return ElementCopier.copy(element, this)
    }

    fun copyType(typedElement: ETypedElement): Type {
        return copyType(typedElement.type, typedElement)
    }

    fun copyType(type: Type, element: EElement): Type {
        return when (val reference = type.reference) {
            is EKtBasicClass -> {
                val boundType = type.copy()
                bind(boundType, element)
                val typeParameterContext = TypeParameterContext
                    .get(boundType.arguments, reference, element)
                    ?: return type
                val forwardedReference = referenceForwardingMap.get(reference, typeParameterContext)
                if (forwardedReference != null) {
                    forwardedReference.toType()
                } else {
                    if (type.arguments.isNotEmpty())
                        Messages.INTERNAL_ERROR.on(element, "Forwarded reference not found: ${reference.name}")
                    reference.toType()
                }
            }
            is ETypeParameter -> {
                val copyType = reference.toType()
                bind(copyType, element)
                copyType
            }
            else -> {
                val arguments = type.arguments.map { copyType(it, element) }
                Type(reference, ArrayList(arguments))
            }
        }
    }
}
