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
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object ScopeReferenceInsertionTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val scopeReferenceInsertionTransformerVisitor = ScopeReferenceInsertionTransformerVisitor()
        projectContext.project.accept(scopeReferenceInsertionTransformerVisitor)
    }

    private class ScopeReferenceInsertionTransformerVisitor : TreeVisitor() {

        private var parentBasicPackage: EBasicPackage? = null

        private fun getScopeReferenceExpression(reference: Declaration, element: EElement): EKtReferenceExpression? {
            if (reference is EElement) {
                when (val parent = reference.parent) {
                    is ESvBasicClass -> {
                        if (reference is ESvFunction && reference.isScopeStatic) {
                            return EKtReferenceExpression(
                                element.location,
                                NullDeclaration.toType(),
                                parent,
                                getScopeReferenceExpression(parent, element)
                            )
                        }
                    }
                    is EFile -> {
                        val basicPackage = parent.parent
                        if (basicPackage is EBasicPackage && basicPackage != parentBasicPackage)
                            return EKtReferenceExpression(element.location, NullDeclaration.toType(), basicPackage, null)
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

        override fun visitKtReferenceExpression(referenceExpression: EKtReferenceExpression) {
            super.visitKtReferenceExpression(referenceExpression)
            if (referenceExpression.receiver == null) {
                val scopeReferenceExpression = getScopeReferenceExpression(
                    referenceExpression.reference,
                    referenceExpression
                )
                if (scopeReferenceExpression != null) {
                    scopeReferenceExpression.parent = referenceExpression
                    referenceExpression.receiver = scopeReferenceExpression
                }
            }
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            if (callExpression.receiver == null) {
                val scopeReferenceExpression = getScopeReferenceExpression(
                    callExpression.reference,
                    callExpression
                )
                if (scopeReferenceExpression != null) {
                    scopeReferenceExpression.parent = callExpression
                    callExpression.receiver = scopeReferenceExpression
                }
            }
        }
    }
}
