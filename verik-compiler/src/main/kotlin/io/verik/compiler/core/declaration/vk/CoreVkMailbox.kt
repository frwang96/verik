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

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.target.common.Target

object CoreVkMailbox : CoreScope(Core.Vk.C_Mailbox) {

    val F_put_T = BasicCoreFunctionDeclaration(parent, "put", "fun put(T)", Target.Mailbox.F_put)

    val F_get = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val property = EProperty.temporary(
                callExpression.location,
                callExpression.receiver!!.type.arguments[0].copy(),
                initializer = null,
                isMutable = true
            )
            val propertyStatement = EPropertyStatement(property.location, property)
            val referenceExpression = EReferenceExpression.of(property)
            val newCallExpression = ExpressionCopier.shallowCopy(callExpression)
            newCallExpression.reference = Target.Mailbox.F_get
            referenceExpression.parent = newCallExpression
            newCallExpression.valueArguments.add(referenceExpression)
            val statements = arrayListOf(
                propertyStatement,
                newCallExpression,
                ExpressionCopier.deepCopy(referenceExpression)
            )
            return EBlockExpression(
                callExpression.location,
                callExpression.location,
                callExpression.type.copy(),
                statements
            )
        }
    }
}
