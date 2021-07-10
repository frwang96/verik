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
import io.verik.compiler.ast.property.KOperatorKind
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object AssignmentOperatorReducer : ProjectPass {

    private val assignmentOperatorMap = HashMap<KOperatorKind, KOperatorKind>()

    init {
        assignmentOperatorMap[KOperatorKind.PLUS_EQ] = KOperatorKind.PLUS
        assignmentOperatorMap[KOperatorKind.MINUS_EQ] = KOperatorKind.MINUS
    }

    override fun pass(projectContext: ProjectContext) {
        projectContext.verikFiles.forEach {
            it.accept(AssignmentOperatorVisitor)
        }
    }

    object AssignmentOperatorVisitor : TreeVisitor() {

        override fun visitKBinaryExpression(binaryExpression: KBinaryExpression) {
            super.visitKBinaryExpression(binaryExpression)
            val kind = assignmentOperatorMap[binaryExpression.kind]
            if (kind != null) {
                val copyExpression = binaryExpression.left.copy()
                    ?: return
                val assignmentExpression = KBinaryExpression(
                    binaryExpression.location,
                    binaryExpression.type,
                    binaryExpression.left,
                    KBinaryExpression(
                        binaryExpression.location,
                        copyExpression.type,
                        copyExpression,
                        binaryExpression.right,
                        kind
                    ),
                    KOperatorKind.EQ
                )
                binaryExpression.replace(assignmentExpression)
            }
        }
    }
}