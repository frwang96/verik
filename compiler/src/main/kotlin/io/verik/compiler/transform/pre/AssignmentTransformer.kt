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

import io.verik.compiler.ast.element.kt.KBinaryExpression
import io.verik.compiler.ast.element.sv.SBinaryExpression
import io.verik.compiler.ast.property.KtOperatorKind
import io.verik.compiler.ast.property.SvOperatorKind
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object AssignmentTransformer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        projectContext.verikFiles.forEach {
            it.accept(AssignmentVisitor)
        }
    }

    object AssignmentVisitor : TreeVisitor() {

        override fun visitKBinaryExpression(binaryExpression: KBinaryExpression) {
            super.visitKBinaryExpression(binaryExpression)
            if (binaryExpression.kind == KtOperatorKind.EQ) {
                binaryExpression.replace(
                    SBinaryExpression(
                        binaryExpression.location,
                        binaryExpression.type,
                        binaryExpression.left,
                        binaryExpression.right,
                        SvOperatorKind.ASSIGN
                    )
                )
            }
        }
    }
}