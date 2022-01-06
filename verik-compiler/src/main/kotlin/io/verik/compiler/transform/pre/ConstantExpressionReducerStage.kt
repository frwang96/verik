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

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ConstantExpressionReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ConstantExpressionReducerVisitor)
    }

    private object ConstantExpressionReducerVisitor : TreeVisitor() {

        override fun visitConstantExpression(constantExpression: EConstantExpression) {
            super.visitConstantExpression(constantExpression)
            when (constantExpression.type.reference) {
                Core.Kt.C_Boolean ->
                    constantExpression.replace(ConstantNormalizer.normalizeBoolean(constantExpression))
                Core.Kt.C_Int ->
                    constantExpression.replace(ConstantNormalizer.normalizeInt(constantExpression))
            }
        }
    }
}
