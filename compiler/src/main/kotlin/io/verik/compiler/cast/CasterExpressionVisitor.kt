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

import io.verik.compiler.ast.common.Name
import io.verik.compiler.ast.common.NullDeclaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.*
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.getMessageLocation
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

class CasterExpressionVisitor(
    projectContext: ProjectContext,
    private val declarationMap: DeclarationMap
): KtVisitor<VkExpression, Unit>() {

    private val bindingContext = projectContext.bindingContext

    inline fun <reified T: VkExpression> getExpression(expression: KtExpression): T? {
        return expression.accept(this, Unit).cast(expression)
    }

    private fun getType(expression: KtExpression): Type {
        return TypeCaster.castType(declarationMap, expression.getType(bindingContext)!!, expression)
    }

    override fun visitBlockExpression(expression: KtBlockExpression, data: Unit?): VkExpression {
        val location = expression.getMessageLocation()
        val type = getType(expression)
        val statements = expression.statements.mapNotNull { getExpression(it) }
        return VkBlockExpression(location, type, ArrayList(statements))
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression, data: Unit?): VkExpression {
        val descriptor = bindingContext.getSliceContents(BindingContext.REFERENCE_TARGET)[expression]!!
        val location = expression.getMessageLocation()
        val type = getType(expression)
        val name = Name(descriptor.name.toString())
        return VkReferenceExpression(location, type, NullDeclaration, name)
    }

    override fun visitCallExpression(expression: KtCallExpression, data: Unit?): VkExpression {
        val descriptor = bindingContext
            .getSliceContents(BindingContext.REFERENCE_TARGET)[expression.calleeExpression]!!
        val location = expression.getMessageLocation()
        val type = getType(expression)
        val name = Name(descriptor.name.toString())
        return VkCallExpression(location, type, NullDeclaration, name)
    }

    override fun visitConstantExpression(expression: KtConstantExpression, data: Unit?): VkExpression {
        val location = expression.getMessageLocation()
        val type = getType(expression)
        val value = ConstantExpressionCaster.cast(expression.text, type, location)
        return VkConstantExpression(location, type, value)
    }
}