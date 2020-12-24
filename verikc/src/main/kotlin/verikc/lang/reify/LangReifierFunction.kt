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

package verikc.lang.reify

import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeReified
import verikc.lang.BitType
import verikc.rf.ast.RfExpressionFunction
import java.lang.Integer.max
import kotlin.math.abs

object LangReifierFunction {

    fun reifyNativeAddBit(expression: RfExpressionFunction, bitType: BitType): TypeReified {
        LangReifierUtil.inferWidth(expression.receiver!!, expression.args[0], bitType)
        val leftWidth = LangReifierUtil.bitToWidth(expression.receiver, bitType)
        val rightWidth = LangReifierUtil.bitToWidth(expression.args[0], bitType)
        val width = max(leftWidth, rightWidth)
        return TypeReified(bitType.symbol(), INSTANCE, listOf(width))
    }

    fun reifyNativeGet(expression: RfExpressionFunction, bitType: BitType): TypeReified {
        val startIndex = LangReifierUtil.intLiteralToInt(expression.args[0])
        val endIndex = LangReifierUtil.intLiteralToInt(expression.args[1])
        val width = abs(startIndex - endIndex) + 1
        return TypeReified(bitType.symbol(), INSTANCE, listOf(width))
    }
}
