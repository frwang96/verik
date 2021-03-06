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
import io.verik.compiler.ast.element.sv.EProceduralBlock
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.property.Name
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object BlockExpressionTransformer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        projectContext.files.forEach {
            it.accept(BlockExpressionVisitor)
        }
    }

    object BlockExpressionVisitor : TreeVisitor() {

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            var decorated = true
            var name: Name? = null
            when (val parent = blockExpression.parent) {
                is ESvFunction -> decorated = false
                is EProceduralBlock -> name = parent.name
            }
            blockExpression.replace(
                ESvBlockExpression(
                    blockExpression.location,
                    blockExpression.statements,
                    decorated,
                    name
                )
            )
        }
    }
}