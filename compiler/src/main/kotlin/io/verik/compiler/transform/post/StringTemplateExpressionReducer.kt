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

import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.VkLiteralStringTemplateEntry
import io.verik.compiler.ast.element.VkStringExpression
import io.verik.compiler.ast.element.VkStringTemplateExpression
import io.verik.compiler.main.ProjectContext

object StringTemplateExpressionReducer {

    fun transform(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(StringTemplateExpressionVisitor)
        }
    }

    object StringTemplateExpressionVisitor : TreeVisitor() {

        override fun visitStringTemplateExpression(stringTemplateExpression: VkStringTemplateExpression) {
            super.visitStringTemplateExpression(stringTemplateExpression)
            val builder = StringBuilder()
            for (entry in stringTemplateExpression.entries) {
                when (entry) {
                    is VkLiteralStringTemplateEntry -> builder.append(entry.text)
                }
            }
            val constantExpression = VkStringExpression(
                stringTemplateExpression.location,
                builder.toString()
            )
            stringTemplateExpression.replace(constantExpression)
        }
    }
}