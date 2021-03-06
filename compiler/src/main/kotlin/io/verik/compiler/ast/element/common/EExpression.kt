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

import io.verik.compiler.ast.interfaces.ExpressionContainer
import io.verik.compiler.ast.property.SvSerializationType
import io.verik.compiler.ast.property.Type
import io.verik.compiler.main.m

abstract class EExpression : EElement() {

    abstract var type: Type

    abstract val serializationType: SvSerializationType

    abstract fun copy(): EExpression

    fun replace(expression: EExpression) {
        val parent = parentNotNull()
        if (parent is ExpressionContainer)
            parent.replaceChild(this, expression)
        else
            m.error("Could not replace ${this::class.simpleName} in ${parent::class.simpleName}", this)
    }
}