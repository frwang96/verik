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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EPackage
import io.verik.compiler.ast.element.common.EReceiverExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EScopeExpression
import io.verik.compiler.ast.element.sv.ESvClass
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.target.common.CompositeTargetFunctionDeclaration
import io.verik.compiler.target.common.Target
import io.verik.compiler.target.common.TargetDeclaration

object ScopeExpressionInsertionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val scopeExpressionInsertionTransformerVisitor = ScopeExpressionInsertionTransformerVisitor()
        projectContext.project.accept(scopeExpressionInsertionTransformerVisitor)
    }

    private class ScopeExpressionInsertionTransformerVisitor : TreeVisitor() {

        private var parentPackage: EPackage? = null
        private var parentClass: EAbstractClass? = null

        private fun getScopeExpression(receiverExpression: EReceiverExpression): EExpression? {
            when (val reference = receiverExpression.reference) {
                is TargetDeclaration -> {
                    if (reference is CompositeTargetFunctionDeclaration) {
                        val scope = if (reference.isConstructor) {
                            receiverExpression.type.copy()
                        } else {
                            reference.parent.toType()
                        }
                        return EScopeExpression(receiverExpression.location, scope)
                    }
                }
                is EElement -> {
                    when (val parent = reference.parent) {
                        is EFile -> {
                            val `package` = parent.parent
                            if (`package` is EPackage &&
                                `package`.packageType.isRegular() &&
                                `package` != parentPackage
                            ) {
                                return EScopeExpression(receiverExpression.location, `package`.toType())
                            }
                        }
                        is ESvClass -> {
                            if (parent != parentClass) {
                                if (reference is ESvFunction && reference.isStatic)
                                    return EScopeExpression(receiverExpression.location, parent.toType())
                                if (reference is ESvProperty && reference.isStatic == true)
                                    return EScopeExpression(receiverExpression.location, parent.toType())
                            }
                        }
                        is EModule -> {
                            if (parent.isSimulationTop && parent != parentClass) {
                                val referenceExpression = EReferenceExpression(
                                    receiverExpression.location,
                                    Target.C_Void.toType(),
                                    Target.P_root,
                                    null
                                )
                                return EReferenceExpression(
                                    receiverExpression.location,
                                    parent.toType(),
                                    parent,
                                    referenceExpression
                                )
                            }
                        }
                    }
                }
            }
            return null
        }

        override fun visitPackage(`package`: EPackage) {
            if (`package`.packageType.isNative()) {
                parentPackage = `package`
                super.visitPackage(`package`)
                parentPackage = null
            }
        }

        override fun visitAbstractClass(abstractClass: EAbstractClass) {
            parentClass = abstractClass
            super.visitAbstractClass(abstractClass)
            parentClass = null
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
