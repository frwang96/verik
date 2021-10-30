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

import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EReceiverExpression
import io.verik.compiler.ast.element.sv.EScopeExpression
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.target.common.CompositeTarget
import io.verik.compiler.target.common.ConstructorTargetFunctionDeclaration
import io.verik.compiler.target.common.TargetDeclaration

object ScopeExpressionInsertionTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val scopeExpressionInsertionTransformerVisitor = ScopeExpressionInsertionTransformerVisitor()
        projectContext.project.accept(scopeExpressionInsertionTransformerVisitor)
    }

    private class ScopeExpressionInsertionTransformerVisitor : TreeVisitor() {

        private var parentBasicPackage: EBasicPackage? = null

        private fun getScopeExpression(receiverExpression: EReceiverExpression): EScopeExpression? {
            when (val reference = receiverExpression.reference) {
                is TargetDeclaration -> {
                    when (reference) {
                        is ConstructorTargetFunctionDeclaration -> {
                            return EScopeExpression(receiverExpression.location, receiverExpression.type.copy())
                        }
                        is CompositeTarget -> {
                            val parent = reference.parent!!
                            return EScopeExpression(receiverExpression.location, parent.toType())
                        }
                    }
                }
                is EElement -> {
                    when (val parent = reference.parent) {
                        is EFile -> {
                            val basicPackage = parent.parent
                            if (basicPackage is EBasicPackage && basicPackage != parentBasicPackage)
                                return EScopeExpression(receiverExpression.location, basicPackage.toType())
                        }
                        is ESvBasicClass -> {
                            if (reference is ESvFunction && reference.isStatic)
                                return EScopeExpression(receiverExpression.location, parent.toType())
                        }
                    }
                }
            }
            return null
        }

        override fun visitBasicPackage(basicPackage: EBasicPackage) {
            parentBasicPackage = basicPackage
            super.visitBasicPackage(basicPackage)
            parentBasicPackage = null
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
