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

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.element.kt.EWhenExpression
import io.verik.compiler.ast.property.SvSerializationType

abstract class EExpression : ETypedElement() {

    abstract val serializationType: SvSerializationType

    fun replace(expression: EExpression) {
        parentNotNull().replaceChildAsExpressionContainer(this, expression)
    }

    fun isSubexpression(): Boolean {
        if ((this is EIfExpression || this is EWhenExpression) &&
            (this.parent is EIfExpression || this.parent is EWhenExpression)
        ) {
            return (this.parent as EExpression).isSubexpression()
        }
        return this.parent !is EAbstractBlockExpression
    }
}
