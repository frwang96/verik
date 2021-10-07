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

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.property.Type

object ElementCopier {

    fun <E : EElement> copy(element: E): E {
        val referenceForwardingMap = ReferenceForwardingMap()
        val declarationCopierVisitor = DeclarationCopierVisitor(referenceForwardingMap)
        element.accept(declarationCopierVisitor)
        val copyContext = CopyContext(referenceForwardingMap)
        return copy(element, copyContext)
    }

    fun <E : EElement> copy(element: E, copyContext: CopyContext): E {
        val copiedElement = when (element) {
            is EKtBlockExpression -> copyKtBlockExpression(element, copyContext)
            is EKtReferenceExpression -> copyKtReferenceExpression(element, copyContext)
            is EKtCallExpression -> copyKtCallExpression(element, copyContext)
            is EConstantExpression -> copyConstantExpression(element)
            else -> {
                // TODO throw error if unable to copy
                // Messages.INTERNAL_ERROR.on(element, "Unable to copy element: $element")
                element
            }
        }
        @Suppress("UNCHECKED_CAST")
        return copiedElement as E
    }

    fun copy(type: Type, copyContext: CopyContext): Type {
        val arguments = type.arguments.map { copy(it, copyContext) }
        val reference = copyContext.referenceForwardingMap[type.reference] ?: type.reference
        return Type(reference, ArrayList(arguments))
    }

    @Suppress("unused")
    private fun copyKtBasicClass(basicClass: EKtBasicClass, copyContext: CopyContext): EKtBasicClass {
        val copiedBasicClass = copyContext.referenceForwardingMap.getAsDeclaration(basicClass)
            ?: return basicClass

        val superType = copy(basicClass.supertype, copyContext)
        val typeParameters = basicClass.typeParameters.map { copy(it, copyContext) }
        val declarations = basicClass.declarations.map { copy(it, copyContext) }
        val annotations = basicClass.annotations.map { copy(it, copyContext) }
        val primaryConstructor = basicClass.primaryConstructor?.let { copy(it, copyContext) }

        copiedBasicClass.init(
            superType,
            typeParameters,
            declarations,
            annotations,
            basicClass.isEnum,
            primaryConstructor
        )
        return copiedBasicClass
    }

    private fun copyKtBlockExpression(
        blockExpression: EKtBlockExpression,
        copyContext: CopyContext
    ): EKtBlockExpression {
        val type = blockExpression.type.copy()
        val statements = blockExpression.statements.map { copy(it, copyContext) }
        return EKtBlockExpression(blockExpression.location, type, ArrayList(statements))
    }

    private fun copyKtReferenceExpression(
        referenceExpression: EKtReferenceExpression,
        copyContext: CopyContext
    ): EKtReferenceExpression {
        val type = referenceExpression.type.copy()
        val receiver = referenceExpression.receiver?.let { copy(it, copyContext) }
        return EKtReferenceExpression(referenceExpression.location, type, referenceExpression.reference, receiver)
    }

    private fun copyKtCallExpression(callExpression: EKtCallExpression, copyContext: CopyContext): EKtCallExpression {
        val type = callExpression.type.copy()
        val receiver = callExpression.receiver?.let { copy(it, copyContext) }
        val valueArguments = callExpression.valueArguments.map { copy(it, copyContext) }
        val typeArguments = callExpression.typeArguments.map { it.copy() }
        return EKtCallExpression(
            callExpression.location,
            type,
            callExpression.reference,
            receiver,
            ArrayList(valueArguments),
            ArrayList(typeArguments)
        )
    }

    private fun copyConstantExpression(constantExpression: EConstantExpression): EConstantExpression {
        val type = constantExpression.type.copy()
        return EConstantExpression(constantExpression.location, type, constantExpression.value)
    }
}
