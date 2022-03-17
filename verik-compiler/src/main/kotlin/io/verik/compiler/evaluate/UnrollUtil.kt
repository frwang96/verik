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

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.declaration.common.EAbstractValueParameter
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor

object UnrollUtil {

    fun substituteValueParameter(
        expression: EExpression,
        valueParameter: EAbstractValueParameter,
        substitutedExpression: EExpression
    ) {
        val valueParameterSubstitutorVisitor = ValueParameterSubstitutorVisitor(valueParameter, substitutedExpression)
        expression.accept(valueParameterSubstitutorVisitor)
    }

    private class ValueParameterSubstitutorVisitor(
        private val valueParameter: EAbstractValueParameter,
        private val expression: EExpression
    ) : TreeVisitor() {

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            if (referenceExpression.reference == valueParameter) {
                val copiedExpression = ExpressionCopier.deepCopy(expression, referenceExpression.location)
                referenceExpression.replace(copiedExpression)
            }
        }
    }
}
