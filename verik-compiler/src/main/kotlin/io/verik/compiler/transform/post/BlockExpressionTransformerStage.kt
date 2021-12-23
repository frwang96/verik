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

import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.sv.EAbstractProceduralBlock
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object BlockExpressionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(BlockExpressionTransformerVisitor)
    }

    private object BlockExpressionTransformerVisitor : TreeVisitor() {

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            var decorated = true
            var name: String? = null
            when (val parent = blockExpression.parent) {
                is ESvFunction -> decorated = false
                is ETask -> decorated = false
                is EAbstractProceduralBlock -> name = parent.name
            }
            blockExpression.replace(
                ESvBlockExpression(
                    blockExpression.location,
                    blockExpression.endLocation,
                    blockExpression.statements,
                    decorated,
                    name
                )
            )
        }
    }
}
