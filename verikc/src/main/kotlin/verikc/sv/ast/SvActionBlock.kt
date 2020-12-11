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

package verikc.sv.ast

import verikc.base.ast.ActionBlockType
import verikc.base.ast.Line
import verikc.sv.build.SvBuildable
import verikc.sv.build.SvSimpleExpressionBuilder
import verikc.sv.build.SvSourceBuilder

data class SvActionBlock(
        val line: Line,
        val actionBlockType: ActionBlockType,
        val eventExpressions: List<SvExpression>,
        val block: SvBlock
): SvBuildable {

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
                SvSimpleExpressionBuilder.build(it)
            }
            builder.append("@($eventExpressionsString) ")
        }
        block.build(builder)
    }
}
