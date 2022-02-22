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

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReceiverExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.property.ExpressionType
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object SafeAccessReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(SafeAccessReducerVisitor)
    }

    private object SafeAccessReducerVisitor : TreeVisitor() {

        override fun visitReceiverExpression(receiverExpression: EReceiverExpression) {
            super.visitReceiverExpression(receiverExpression)
            if (receiverExpression.isSafeAccess) {
                val receiver = receiverExpression.receiver!!
                val property = EProperty.temporary(
                    receiverExpression.location,
                    receiver.type.copy(),
                    ExpressionCopier.deepCopy(receiver),
                    false
                )
                val propertyStatement = EPropertyStatement(receiverExpression.location, property)
                val referenceExpression = EReferenceExpression.of(property)
                val conditionBinaryExpression = EKtBinaryExpression(
                    receiverExpression.location,
                    Core.Kt.C_Boolean.toType(),
                    referenceExpression,
                    ConstantBuilder.buildNull(receiverExpression.location),
                    KtBinaryOperatorKind.EXCL_EQ
                )
                val newReferenceExpression = ExpressionCopier.deepCopy(referenceExpression)
                val newReceiverExpression = ExpressionCopier.shallowCopy(receiverExpression)
                newReferenceExpression.parent = newReceiverExpression
                newReceiverExpression.isSafeAccess = false
                newReceiverExpression.receiver = newReferenceExpression
                if (receiverExpression.getExpressionType() == ExpressionType.STATEMENT) {
                    val ifExpression = EIfExpression(
                        receiverExpression.location,
                        Core.Kt.C_Unit.toType(),
                        conditionBinaryExpression,
                        EBlockExpression.wrap(newReceiverExpression),
                        null
                    )
                    EBlockExpression.extract(
                        receiverExpression,
                        listOf(propertyStatement, ifExpression)
                    )
                } else {
                    Messages.INTERNAL_ERROR.on(receiverExpression, "Unable to reduce smart access")
                }
            }
        }
    }
}
