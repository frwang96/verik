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

package io.verik.compiler.common

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.message.Messages

object ElementCopier {

    fun <E : EElement> copy(element: E): E {
        val copyElement = when (element) {
            is EKtBlockExpression -> copyKtBlockExpression(element)
            is EKtReferenceExpression -> copyKtReferenceExpression(element)
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Unable to copy element: $element")
                element
            }
        }
        @Suppress("UNCHECKED_CAST")
        return copyElement as E
    }

    private fun copyKtBlockExpression(blockExpression: EKtBlockExpression): EKtBlockExpression {
        val type = blockExpression.type.copy()
        val statements = blockExpression.statements.map { copy(it) }
        return EKtBlockExpression(blockExpression.location, type, ArrayList(statements))
    }

    private fun copyKtReferenceExpression(referenceExpression: EKtReferenceExpression): EKtReferenceExpression {
        val type = referenceExpression.type.copy()
        val receiver = referenceExpression.receiver?.let { copy(it) }
        return EKtReferenceExpression(referenceExpression.location, type, referenceExpression.reference, receiver)
    }
}
