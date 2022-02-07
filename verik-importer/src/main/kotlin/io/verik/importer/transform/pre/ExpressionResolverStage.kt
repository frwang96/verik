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

package io.verik.importer.transform.pre

import io.verik.importer.ast.element.expression.ELiteralExpression
import io.verik.importer.ast.element.expression.EReferenceExpression
import io.verik.importer.common.TreeVisitor
import io.verik.importer.core.Cardinal
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object ExpressionResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ExpressionResolverVisitor)
    }

    private object ExpressionResolverVisitor : TreeVisitor() {

        override fun visitLiteralExpression(literalExpression: ELiteralExpression) {
            val value = literalExpression.value.toIntOrNull()
            if (value != null) {
                literalExpression.type = Cardinal.of(value).toType()
            } else {
                Messages.INTERNAL_ERROR.on(literalExpression, "Unable to parse literal: ${literalExpression.value}")
            }
        }

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            referenceExpression.type = referenceExpression.reference.toType()
        }
    }
}
