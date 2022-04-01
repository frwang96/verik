/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.EModule
import io.verik.compiler.ast.element.declaration.sv.ESvAbstractFunction
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReceiverExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.sv.EScopeExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.target.common.CompositeTargetFunctionDeclaration
import io.verik.compiler.target.common.Target
import io.verik.compiler.target.common.TargetDeclaration

/**
 * Stage that inserts scope expressions as necessary.
 */
object ScopeExpressionInsertionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val scopeExpressionInsertionTransformerVisitor = ScopeExpressionInsertionTransformerVisitor()
        projectContext.project.accept(scopeExpressionInsertionTransformerVisitor)
    }

    private class ScopeExpressionInsertionTransformerVisitor : TreeVisitor() {

        private var parentPackage: EPackage? = null
        private var parentClass: EAbstractClass? = null

        private fun getScopeExpression(receiverExpression: EReceiverExpression): EExpression? {
            return when (val reference = receiverExpression.reference) {
                is TargetDeclaration -> {
                    if (reference is CompositeTargetFunctionDeclaration) {
                        val scope = if (reference.isConstructor) {
                            receiverExpression.type.copy()
                        } else {
                            reference.parent.toType()
                        }
                        EScopeExpression(receiverExpression.location, scope, listOf())
                    } else null
                }
                is EElement -> {
                    return when (val parent = reference.parent) {
                        is EFile -> getScopeExpressionFromFile(receiverExpression, parent)
                        is ESvClass -> getScopeExpressionFromClass(receiverExpression, parent)
                        is EModule -> getScopeExpressionFromModule(receiverExpression, parent)
                        else -> null
                    }
                }
                else -> null
            }
        }

        private fun getScopeExpressionFromFile(
            receiverExpression: EReceiverExpression,
            file: EFile,
        ): EScopeExpression? {
            val pkg = file.getParentPackage()
            return if (!pkg.kind.isRoot() && pkg != parentPackage) {
                EScopeExpression(receiverExpression.location, pkg.toType(), listOf())
            } else null
        }

        private fun getScopeExpressionFromClass(
            receiverExpression: EReceiverExpression,
            cls: ESvClass,
        ): EScopeExpression? {
            return if (cls != parentClass) {
                when (val reference = receiverExpression.reference) {
                    is ESvAbstractFunction -> {
                        if (reference.isStatic) {
                            val typeParameters = if (reference.isImported()) reference.typeParameters else listOf()
                            EScopeExpression(receiverExpression.location, cls.toType(), typeParameters)
                        } else null
                    }
                    is EProperty -> {
                        if (reference.isStatic) {
                            EScopeExpression(receiverExpression.location, cls.toType(), listOf())
                        } else null
                    }
                    else -> null
                }
            } else null
        }

        private fun getScopeExpressionFromModule(
            receiverExpression: EReceiverExpression,
            module: EModule
        ): EReferenceExpression? {
            return if (module != parentClass) {
                val referenceExpression = EReferenceExpression(
                    receiverExpression.location,
                    Target.C_Void.toType(),
                    Target.P_root,
                    null,
                    false
                )
                EReferenceExpression(
                    receiverExpression.location,
                    module.toType(),
                    module,
                    referenceExpression,
                    false
                )
            } else null
        }

        override fun visitPackage(pkg: EPackage) {
            if (!pkg.kind.isImported()) {
                parentPackage = pkg
                super.visitPackage(pkg)
                parentPackage = null
            }
        }

        override fun visitAbstractClass(abstractClass: EAbstractClass) {
            val previousParentClass = parentClass
            parentClass = abstractClass
            super.visitAbstractClass(abstractClass)
            parentClass = previousParentClass
        }

        override fun visitReceiverExpression(receiverExpression: EReceiverExpression) {
            super.visitReceiverExpression(receiverExpression)
            if (receiverExpression.receiver == null) {
                val scopeExpression = getScopeExpression(receiverExpression)
                if (scopeExpression != null) {
                    scopeExpression.parent = receiverExpression
                    receiverExpression.receiver = scopeExpression
                }
            }
        }
    }
}
