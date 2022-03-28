/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.declaration.sv.EAlwaysComBlock
import io.verik.compiler.ast.element.declaration.sv.EAlwaysSeqBlock
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.expression.sv.EEventControlExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ProceduralAssignmentTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ProceduralAssignmentTransformerVisitor)
    }

    private object ProceduralAssignmentTransformerVisitor : TreeVisitor() {

        override fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
            super.visitAbstractContainerComponent(abstractContainerComponent)
            val declarations = ArrayList<EDeclaration>()
            abstractContainerComponent.declarations.forEach { declaration ->
                declarations.add(declaration)
                if (declaration is EProperty) {
                    if (declaration.hasAnnotationEntry(AnnotationEntries.COM)) {
                        val alwaysComBlock = splitAlwaysComBlock(declaration)
                        if (alwaysComBlock != null) {
                            alwaysComBlock.parent = abstractContainerComponent
                            declarations.add(alwaysComBlock)
                        }
                    }
                    if (declaration.hasAnnotationEntry(AnnotationEntries.SEQ)) {
                        val alwaysSeqBlock = splitAlwaysSeqBlock(declaration)
                        if (alwaysSeqBlock != null) {
                            alwaysSeqBlock.parent = abstractContainerComponent
                            declarations.add(alwaysSeqBlock)
                        }
                    }
                }
            }
            abstractContainerComponent.declarations = declarations
        }

        private fun splitAlwaysComBlock(property: EProperty): EAlwaysComBlock? {
            val initializer = property.initializer
            return if (initializer != null) {
                property.initializer = null
                val referenceExpression = EReferenceExpression.of(property.location, property)
                val binaryExpression = EKtBinaryExpression(
                    property.location,
                    Core.Kt.C_Unit.toType(),
                    referenceExpression,
                    initializer,
                    KtBinaryOperatorKind.EQ
                )
                val blockExpression = EBlockExpression(
                    property.location,
                    property.location,
                    Core.Kt.C_Unit.toType(),
                    arrayListOf(binaryExpression)
                )
                EAlwaysComBlock(
                    property.location,
                    "<tmp>",
                    listOf(),
                    null,
                    blockExpression
                )
            } else {
                Messages.COM_ASSIGNMENT_NO_INITIALIZER.on(property)
                null
            }
        }

        private fun splitAlwaysSeqBlock(property: EProperty): EAlwaysSeqBlock? {
            val initializer = property.initializer
            return if (initializer is ECallExpression && initializer.reference == Core.Vk.F_oni_Event_Function) {
                property.initializer = null
                val referenceExpression = EReferenceExpression.of(property.location, property)
                val body = initializer.valueArguments.last().cast<EFunctionLiteralExpression>().body
                if (body.isEmpty()) {
                    Messages.SEQ_ASSIGNMENT_NO_ONR_EXPRESSION.on(property)
                    return null
                }
                val binaryExpression = EKtBinaryExpression(
                    property.location,
                    Core.Kt.C_Unit.toType(),
                    referenceExpression,
                    body.statements.removeLast(),
                    KtBinaryOperatorKind.EQ
                )
                binaryExpression.parent = body
                body.statements.add(binaryExpression)
                val eventExpressions = initializer.valueArguments.dropLast(1)
                if (eventExpressions.isEmpty()) {
                    Messages.CALL_EXPRESSION_INSUFFICIENT_ARGUMENTS.on(initializer, initializer.reference.name)
                }
                val eventControlExpression = EEventControlExpression(property.location, ArrayList(eventExpressions))
                EAlwaysSeqBlock(
                    property.location,
                    "<tmp>",
                    listOf(),
                    null,
                    body,
                    eventControlExpression
                )
            } else {
                Messages.SEQ_ASSIGNMENT_NO_ONR_EXPRESSION.on(property)
                null
            }
        }
    }
}
