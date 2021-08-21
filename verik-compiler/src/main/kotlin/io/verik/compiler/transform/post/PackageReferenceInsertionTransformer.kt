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
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object PackageReferenceInsertionTransformer : PostTransformerStage() {

    override fun process(projectContext: ProjectContext) {
        val packageReferenceInsertionVisitor = PackageReferenceInsertionVisitor()
        projectContext.project.accept(packageReferenceInsertionVisitor)
    }

    class PackageReferenceInsertionVisitor : TreeVisitor() {

        private var parentBasicPackage: EBasicPackage? = null

        private fun getPackageReferenceExpression(reference: Declaration, element: EElement): EKtReferenceExpression? {
            if (reference is EElement) {
                val file = reference.parent
                if (file is EFile) {
                    val basicPackage = file.parent
                    if (basicPackage is EBasicPackage && basicPackage != parentBasicPackage) {
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
                val packageReferenceExpression = getPackageReferenceExpression(
                    referenceExpression.reference,
                    referenceExpression
                )
                if (packageReferenceExpression != null) {
                    packageReferenceExpression.parent = referenceExpression
                    referenceExpression.receiver = packageReferenceExpression
                }
            }
        }

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            if (callExpression.receiver == null) {
                val packageReferenceExpression = getPackageReferenceExpression(
                    callExpression.reference,
                    callExpression
                )
                if (packageReferenceExpression != null) {
                    packageReferenceExpression.parent = callExpression
                    callExpression.receiver = packageReferenceExpression
                }
            }
        }
    }
}