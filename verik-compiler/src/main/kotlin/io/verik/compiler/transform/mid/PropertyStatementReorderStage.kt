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

package io.verik.compiler.transform.mid

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.sv.ESvBinaryExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext

object PropertyStatementReorderStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PropertyStatementReorderVisitor)
    }

    private object PropertyStatementReorderVisitor : TreeVisitor() {

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            val propertyStatements = ArrayList<EPropertyStatement>()
            val statements = ArrayList<EExpression>()
            var reorder = false
            blockExpression.statements.forEach {
                if (!reorder) {
                    if (it is EPropertyStatement) {
                        propertyStatements.add(it)
                    } else {
                        statements.add(it)
                        reorder = true
                    }
                } else {
                    if (it is EPropertyStatement) {
                        val initializer = it.property.initializer
                        if (initializer != null) {
                            val statement = ESvBinaryExpression(
                                it.location,
                                Core.Kt.C_Unit.toType(),
                                EReferenceExpression(it.location, it.property.type.copy(), it.property, null),
                                initializer,
                                SvBinaryOperatorKind.ASSIGN
                            )
                            statement.parent = blockExpression
                            it.property.initializer = null
                            propertyStatements.add(it)
                            statements.add(statement)
                        } else {
                            propertyStatements.add(it)
                        }
                    } else {
                        statements.add(it)
                    }
                }
            }
            blockExpression.statements = ArrayList(propertyStatements + statements)
        }
    }
}
