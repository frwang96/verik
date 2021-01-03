/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.tx.build

import verikc.sv.ast.SvBlock

object TxBuilderBlock {

    fun build(block: SvBlock, builder: TxSourceBuilder) {
        builder.label(block.line)
        builder.appendln("begin")
        indent(builder) {
            for (expression in block.expressions) {
                builder.label(expression.line)
                TxBuilderExpressionBase.build(expression, builder)
            }
        }
        builder.appendln("end")
    }
}
