/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.element.sv.ECaseStatement
import io.verik.compiler.ast.property.CaseEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.BooleanConstantKind
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object CaseStatementTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(CaseStatementTransformerVisitor)
    }

    private object CaseStatementTransformerVisitor : TreeVisitor() {

        override fun visitWhenExpression(whenExpression: EWhenExpression) {
            super.visitWhenExpression(whenExpression)
            if (whenExpression.entries.isEmpty()) {
                val blockExpression = EBlockExpression.empty(whenExpression.location)
                whenExpression.replace(blockExpression)
                return
            }
            val subject = whenExpression.subject
                ?: ConstantBuilder.buildBoolean(whenExpression, BooleanConstantKind.TRUE)
            val entries = whenExpression.entries.map {
                CaseEntry(it.conditions, it.body)
            }
            val caseStatement = ECaseStatement(
                whenExpression.location,
                whenExpression.endLocation,
                whenExpression.type,
                subject,
                entries
            )
            whenExpression.replace(caseStatement)
        }
    }
}
