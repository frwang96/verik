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

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.expression.kt.EKtForStatement
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ForStatementReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ForStatementReducerVisitor)
    }

    private object ForStatementReducerVisitor : TreeVisitor() {

        override fun visitKtForStatement(forStatement: EKtForStatement) {
            super.visitKtForStatement(forStatement)
            val functionLiteralExpression = EFunctionLiteralExpression(
                forStatement.body.location,
                arrayListOf(forStatement.valueParameter),
                forStatement.body
            )
            val callExpression = ECallExpression(
                forStatement.location,
                forStatement.type,
                Core.Kt.Collections.F_forEach_Function,
                forStatement.range,
                false,
                arrayListOf(functionLiteralExpression),
                arrayListOf(forStatement.valueParameter.type.copy())
            )
            forStatement.replace(callExpression)
        }
    }
}
