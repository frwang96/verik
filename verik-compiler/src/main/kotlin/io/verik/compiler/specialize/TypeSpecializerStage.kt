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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import java.lang.Integer.max
import java.lang.Integer.min

object TypeSpecializerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeCheckerVisitor)
    }

    object TypeCheckerVisitor : TreeVisitor() {

        private fun specialize(type: Type, element: EElement) {
            type.arguments.forEach { specialize(it, element) }
            // TODO handle type alias with type parameters
            if (type.isCardinalType()) {
                var reference = type.reference
                while (reference is ETypeAlias || reference is CoreCardinalFunctionDeclaration) {
                    when (reference) {
                        is ETypeAlias -> {
                            type.reference = reference.type.reference
                            type.arguments = ArrayList(reference.type.arguments.map { it.copy() })
                            type.arguments.forEach { specialize(it, element) }
                        }
                        is CoreCardinalFunctionDeclaration ->
                            specializeCardinalFunction(type, reference, element)
                    }
                    reference = type.reference
                }
            }
        }

        private fun specializeCardinalFunction(
            type: Type,
            reference: CoreCardinalFunctionDeclaration,
            element: EElement
        ) {
            val arguments = type.arguments.map { it.asCardinalValue(element) }
            val value = when (reference) {
                Core.Vk.N_ADD ->
                    arguments[0] + arguments[1]
                Core.Vk.N_SUB ->
                    arguments[0] - arguments[1]
                Core.Vk.N_MUL ->
                    arguments[0] * arguments[1]
                Core.Vk.N_MAX ->
                    max(arguments[0], arguments[1])
                Core.Vk.N_MIN ->
                    min(arguments[0], arguments[1])
                Core.Vk.N_INC ->
                    arguments[0] + 1
                Core.Vk.N_DEC ->
                    arguments[0] - 1
                Core.Vk.N_LOG ->
                    if (arguments[0] <= 0) 0 else (32 - (arguments[0] - 1).countLeadingZeroBits())
                Core.Vk.N_WIDTH ->
                    if (arguments[0] < 0) 0 else (32 - arguments[0].countLeadingZeroBits())
                Core.Vk.N_EXP -> {
                    if (arguments[0] >= 31)
                        Messages.CARDINAL_OUT_OF_RANGE.on(element, type)
                    1 shl arguments[0]
                }
                else -> {
                    Messages.INTERNAL_ERROR.on(element, "Unrecognized cardinal function: $reference")
                    1
                }
            }
            type.reference = Core.Vk.cardinalOf(value)
            type.arguments = arrayListOf()
        }

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            if (!typedElement.type.isSpecialized())
                specialize(typedElement.type, typedElement)
            if (typedElement is EKtCallExpression)
                typedElement.typeArguments.forEach { specialize(it, typedElement) }
        }
    }
}
