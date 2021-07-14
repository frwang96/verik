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

package io.verik.compiler.ast.element.sv

import io.verik.compiler.ast.element.common.EAbstractBlockExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.ENullExpression
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.SourceLocation

class EForeverStatement(
    override val location: SourceLocation,
    override var bodyBlockExpression: EAbstractBlockExpression
) : ELoopStatement() {

    override var type = Core.Kt.UNIT.toType()

    init {
        bodyBlockExpression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitForeverStatement(this)
    }

    override fun copy(): EExpression {
        // TODO change bodyBlockExpression to expression
        val copyBodyBlockExpression = bodyBlockExpression.copy().cast<EAbstractBlockExpression>()
            ?: return ENullExpression(location)
        return  EForeverStatement(location, copyBodyBlockExpression)
    }
}