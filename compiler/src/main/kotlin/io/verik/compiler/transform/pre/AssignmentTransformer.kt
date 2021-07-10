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

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.kt.VkKtBinaryExpression
import io.verik.compiler.ast.element.sv.VkSvBinaryExpression
import io.verik.compiler.ast.property.KtOperatorKind
import io.verik.compiler.ast.property.SvOperatorKind
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object AssignmentTransformer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(AssignmentVisitor)
        }
    }

    object AssignmentVisitor : TreeVisitor() {

        override fun visitKtBinaryExpression(ktBinaryExpression: VkKtBinaryExpression) {
            super.visitKtBinaryExpression(ktBinaryExpression)
            if (ktBinaryExpression.kind == KtOperatorKind.EQ) {
                ktBinaryExpression.replace(
                    VkSvBinaryExpression(
                        ktBinaryExpression.location,
                        ktBinaryExpression.type,
                        ktBinaryExpression.left,
                        ktBinaryExpression.right,
                        SvOperatorKind.ASSIGN
                    )
                )
            }
        }
    }
}