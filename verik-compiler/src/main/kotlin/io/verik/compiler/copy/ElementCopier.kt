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

package io.verik.compiler.copy

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.message.Messages

object ElementCopier {

    fun <E : EElement> copy(element: E): E {
        val referenceForwardingMap = ReferenceForwardingMap()
        val declarationCopierVisitor = DeclarationCopierVisitor(referenceForwardingMap)
        element.accept(declarationCopierVisitor)
        val copyContext = CopyContext(referenceForwardingMap)
        return copy(element, copyContext)
    }

    fun <E : EElement> copy(element: E, copyContext: CopyContext): E {
        return when (element) {
            is EDeclaration -> DeclarationCopier.copyDeclaration(element, copyContext)
            is EExpression -> ExpressionCopier.copyExpression(element, copyContext)
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Unable to copy element: $element")
                element
            }
        }
    }
}
