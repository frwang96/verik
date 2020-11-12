/*
 * Copyright 2020 Francis Wang
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

package verik.core.sv.ast

import verik.core.base.ast.ActionBlockType
import verik.core.base.ast.Line
import verik.core.sv.build.SvBuildable
import verik.core.sv.build.SvExpressionBuilder
import verik.core.sv.build.SvSourceBuilder

data class SvActionBlock(
        override val line: Int,
        val actionBlockType: ActionBlockType,
        val eventExpressions: List<SvExpression>,
        val block: SvBlock
): Line, SvBuildable {

    override fun build(builder: SvSourceBuilder) {
        when (actionBlockType) {
            ActionBlockType.COM -> {
                builder.append("always_comb ")
            }
            ActionBlockType.SEQ -> {
                builder.append("always_ff ")
            }
            ActionBlockType.RUN -> {
                builder.append("initial ")
            }
        }
        if (eventExpressions.isNotEmpty()) {
            val eventExpressionsString = eventExpressions.joinToString {
                SvExpressionBuilder.buildString(it)
            }
            builder.append("@($eventExpressionsString) ")
        }
        block.build(builder)
    }
}