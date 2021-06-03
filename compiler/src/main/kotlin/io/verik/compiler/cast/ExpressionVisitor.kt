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

package io.verik.compiler.cast

import io.verik.compiler.ast.element.VkBlockExpression
import io.verik.compiler.ast.element.VkExpression
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.getMessageLocation
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtVisitor
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

class ExpressionVisitor(
    projectContext: ProjectContext,
    private val declarationMap: DeclarationMap
): KtVisitor<VkExpression, Unit>() {

    private val bindingContext = projectContext.bindingContext

    override fun visitBlockExpression(expression: KtBlockExpression, data: Unit?): VkExpression {
        val location = expression.getMessageLocation()
        val type = TypeCaster.castType(declarationMap, expression.getType(bindingContext)!!, expression)
        return VkBlockExpression(location, type, arrayListOf())
    }
}