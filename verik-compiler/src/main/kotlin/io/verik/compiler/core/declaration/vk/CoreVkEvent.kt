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

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.sv.ESvUnaryExpression
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration

object CoreVkEvent : CoreScope(Core.Vk.C_Event) {

    val F_trigger = object : TransformableCoreFunctionDeclaration(parent, "trigger", "fun trigger()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            return ESvUnaryExpression(
                callExpression.location,
                callExpression.type,
                callExpression.receiver!!,
                SvUnaryOperatorKind.TRIGGER
            )
        }
    }
}
