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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EReceiverExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtEnumEntry
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.ast.element.kt.EKtProperty
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Annotations
import org.jetbrains.kotlin.backend.common.push

class DeclarationSpecializeIndexerVisitor(
    private val declarationBindingQueue: ArrayDeque<DeclarationBinding>,
    private val specializerContext: SpecializerContext
) : TreeVisitor() {

    private fun addType(type: Type, element: EElement) {
        val specializedType = TypeSpecializer.specialize(type, specializerContext, element, false)
        addSpecializedType(specializedType, element)
    }

    private fun addSpecializedType(type: Type, element: EElement) {
        type.arguments.forEach { addSpecializedType(it, element) }
        val reference = type.reference
        if (reference is EKtBasicClass && reference.isSpecializable()) {
            val typeParameterContext = TypeParameterContext.getFromTypeArguments(type.arguments, reference, element)
            declarationBindingQueue.push(DeclarationBinding(reference, typeParameterContext))
        }
    }

    private fun addParentObject(receiverExpression: EReceiverExpression) {
        val reference = receiverExpression.reference
        if (reference is EDeclaration) {
            val parent = reference.parent
            if (parent is EKtBasicClass && parent.isObject) {
                declarationBindingQueue.push(DeclarationBinding(parent, TypeParameterContext.EMPTY))
            }
        }
    }

    override fun visitTypedElement(typedElement: ETypedElement) {
        super.visitTypedElement(typedElement)
        addType(typedElement.type, typedElement)
        if (typedElement is EReferenceExpression) {
            val reference = typedElement.reference
            if (reference is EDeclaration && reference.isSpecializable()) {
                val typeParameterContext = TypeParameterContext.getFromReceiver(typedElement, specializerContext)
                declarationBindingQueue.push(DeclarationBinding(reference, typeParameterContext))
            }
            addParentObject(typedElement)
        }
        if (typedElement is EKtCallExpression) {
            typedElement.typeArguments.forEach {
                addType(it, typedElement)
            }
            val reference = typedElement.reference
            if (reference is EKtAbstractFunction && reference.isSpecializable()) {
                val receiverTypeParameterContext = TypeParameterContext.getFromReceiver(
                    typedElement,
                    specializerContext
                )
                val callExpressionTypeParameterContext = TypeParameterContext.getFromTypeArguments(
                    typedElement.typeArguments,
                    reference,
                    typedElement
                )
                val typeParameterContext = TypeParameterContext(
                    receiverTypeParameterContext.typeParameterBindings +
                        callExpressionTypeParameterContext.typeParameterBindings
                )
                declarationBindingQueue.push(DeclarationBinding(reference, typeParameterContext))
            }
            addParentObject(typedElement)
        }
    }

    override fun visitKtBasicClass(basicClass: EKtBasicClass) {
        val superTypeCallEntry = basicClass.superTypeCallEntry
        if (superTypeCallEntry != null) {
            val reference = superTypeCallEntry.reference
            if (reference is EDeclaration)
                declarationBindingQueue.push(DeclarationBinding(reference, TypeParameterContext.EMPTY))
        }

        val typeParameterContext = specializerContext.typeParameterContext
        if (specializerContext.enableDeadCodeElimination) {
            basicClass.declarations.forEach {
                when (it) {
                    is EKtFunction -> {
                        if (it.typeParameters.isEmpty()) {
                            if (it.hasAnnotation(Annotations.COM) ||
                                it.hasAnnotation(Annotations.SEQ) ||
                                it.hasAnnotation(Annotations.RUN)
                            ) {
                                declarationBindingQueue.push(DeclarationBinding(it, typeParameterContext))
                            }
                        }
                    }
                    is EKtProperty -> {
                        if (it.hasAnnotation(Annotations.MAKE)) {
                            declarationBindingQueue.push(DeclarationBinding(it, typeParameterContext))
                        }
                    }
                }
            }
        } else {
            basicClass.declarations.forEach {
                if (it !is TypeParameterized || it.typeParameters.isEmpty())
                    declarationBindingQueue.push(DeclarationBinding(it, typeParameterContext))
            }
        }

        basicClass.typeParameters.forEach { it.accept(this) }
        basicClass.annotations.forEach { it.accept(this) }
        basicClass.primaryConstructor?.accept(this)
        val specializedBasicClass = EKtBasicClass(basicClass.location, basicClass.name)
        specializerContext[basicClass] = specializedBasicClass
    }

    override fun visitKtFunction(function: EKtFunction) {
        super.visitKtFunction(function)
        val specializedFunction = EKtFunction(function.location, function.name)
        specializerContext[function] = specializedFunction
    }

    override fun visitPrimaryConstructor(primaryConstructor: EPrimaryConstructor) {
        super.visitPrimaryConstructor(primaryConstructor)
        val specializedPrimaryConstructor = EPrimaryConstructor(primaryConstructor.location)
        specializerContext[primaryConstructor] = specializedPrimaryConstructor
    }

    override fun visitKtProperty(property: EKtProperty) {
        super.visitKtProperty(property)
        val specializedProperty = EKtProperty(property.location, property.name)
        specializerContext[property] = specializedProperty
    }

    override fun visitKtEnumEntry(enumEntry: EKtEnumEntry) {
        super.visitKtEnumEntry(enumEntry)
        val specializedEnumEntry = EKtEnumEntry(enumEntry.location, enumEntry.name)
        specializerContext[enumEntry] = specializedEnumEntry
    }

    override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
        super.visitKtValueParameter(valueParameter)
        val specializedValueParameter = EKtValueParameter(valueParameter.location, valueParameter.name)
        specializerContext[valueParameter] = specializedValueParameter
    }
}
