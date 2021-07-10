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

import io.verik.compiler.ast.element.common.VkCallExpression
import io.verik.compiler.ast.element.common.VkDotQualifiedExpression
import io.verik.compiler.ast.element.sv.VkSvBinaryExpression
import io.verik.compiler.ast.property.SvOperatorKind
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.CoreFunction
import io.verik.compiler.core.CoreKtFunctionDeclaration
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object BinaryExpressionTransformer : ProjectPass {

    private val operatorKindMap = HashMap<CoreKtFunctionDeclaration, SvOperatorKind>()

    init {
        operatorKindMap[CoreFunction.Kotlin.Int.PLUS_INT] = SvOperatorKind.PLUS
        operatorKindMap[CoreFunction.Kotlin.Int.TIMES_INT] = SvOperatorKind.MUL
    }

    override fun pass(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(BinaryExpressionVisitor)
        }
    }

    object BinaryExpressionVisitor : TreeVisitor() {

        override fun visitDotQualifiedExpression(dotQualifiedExpression: VkDotQualifiedExpression) {
            super.visitDotQualifiedExpression(dotQualifiedExpression)
            val selector = dotQualifiedExpression.selector
            if (selector is VkCallExpression) {
                val reference = selector.reference
                val kind = operatorKindMap[reference]
                if (kind != null) {
                    if (selector.valueArguments.size != 1)
                        m.error("Single value argument expected for call expression $reference", selector)
                    dotQualifiedExpression.replace(
                        VkSvBinaryExpression(
                            dotQualifiedExpression.location,
                            dotQualifiedExpression.type,
                            dotQualifiedExpression.receiver,
                            selector.valueArguments[0].expression,
                            kind
                        )
                    )
                }
            }
        }
    }
}