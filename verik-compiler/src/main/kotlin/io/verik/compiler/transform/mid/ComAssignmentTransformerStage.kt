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

package io.verik.compiler.transform.mid

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ComAssignmentTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ComAssignmentTransformerVisitor)
    }

    private object ComAssignmentTransformerVisitor : TreeVisitor() {

        override fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
            super.visitAbstractContainerComponent(abstractContainerComponent)
            val declarations = ArrayList<EDeclaration>()
            abstractContainerComponent.declarations.forEach { declaration ->
                declarations.add(declaration)
                splitComAssignment(declaration)?.let {
                    it.parent = abstractContainerComponent
                    declarations.add(it)
                }
            }
            abstractContainerComponent.declarations = declarations
        }

        private fun splitComAssignment(declaration: EDeclaration): EAlwaysComBlock? {
            if (declaration is ESvProperty && declaration.isComAssignment) {
                if (declaration.isMutable) {
                    val initializer = declaration.initializer
                    if (initializer != null) {
                        declaration.initializer = null
                        val referenceExpression = EReferenceExpression(
                            declaration.location,
                            declaration.type.copy(),
                            declaration,
                            null
                        )
                        val binaryExpression = EKtBinaryExpression(
                            declaration.location,
                            Core.Kt.C_Unit.toType(),
                            referenceExpression,
                            initializer,
                            KtBinaryOperatorKind.EQ
                        )
                        val blockExpression = EKtBlockExpression(
                            declaration.location,
                            Core.Kt.C_Unit.toType(),
                            arrayListOf(binaryExpression)
                        )
                        return EAlwaysComBlock(
                            declaration.location,
                            "<tmp>",
                            blockExpression
                        )
                    } else {
                        Messages.COM_ASSIGNMENT_NO_INITIALIZER.on(declaration)
                    }
                } else {
                    Messages.COM_ASSIGNMENT_NOT_MUTABLE.on(declaration)
                }
            }
            return null
        }
    }
}
