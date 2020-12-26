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

import verikc.base.ast.LineException
import verikc.base.ast.TypeReified
import verikc.lang.BitType
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rf.ast.RfExpressionFunction
import java.lang.Integer.max
import kotlin.math.abs

object LangReifierFunction {

    fun reifyNativeAddBit(expression: RfExpressionFunction, bitType: BitType): TypeReified {
        LangReifierUtil.inferWidth(expression.receiver!!, expression.args[0], bitType)
        val leftWidth = LangReifierUtil.bitToWidth(expression.receiver, bitType)
        val rightWidth = LangReifierUtil.bitToWidth(expression.args[0], bitType)
        val width = max(leftWidth, rightWidth)
        return bitType.symbol().toTypeReifiedInstance(width)
    }

    fun reifyAddBit(expression: RfExpressionFunction, bitType: BitType): TypeReified {
        LangReifierUtil.inferWidth(expression.receiver!!, expression.args[0], bitType)
        val leftWidth = LangReifierUtil.bitToWidth(expression.receiver, bitType)
        val rightWidth = LangReifierUtil.bitToWidth(expression.args[0], bitType)
        val width = max(leftWidth, rightWidth) + 1
        return bitType.symbol().toTypeReifiedInstance(width)
    }

    fun reifyMulBit(expression: RfExpressionFunction, bitType: BitType): TypeReified {
        LangReifierUtil.inferWidth(expression.receiver!!, expression.args[0], bitType)
        val leftWidth = LangReifierUtil.bitToWidth(expression.receiver, bitType)
        val rightWidth = LangReifierUtil.bitToWidth(expression.args[0], bitType)
        val width = leftWidth + rightWidth
        return bitType.symbol().toTypeReifiedInstance(width)
    }

    fun reifyNativeGet(expression: RfExpressionFunction, bitType: BitType): TypeReified {
        val startIndex = LangReifierUtil.intLiteralToInt(expression.args[0])
        val endIndex = LangReifierUtil.intLiteralToInt(expression.args[1])
        val width = abs(startIndex - endIndex) + 1
        return bitType.symbol().toTypeReifiedInstance(width)
    }

    fun reifyCat(expression: RfExpressionFunction): TypeReified {
        var totalWidth = 0
        expression.args.forEach {
            val width = when (it.getTypeReifiedNotNull().typeSymbol) {
                TYPE_BOOL -> 1
                TYPE_UBIT -> LangReifierUtil.bitToWidth(it, BitType.UBIT).also { width ->
                    if (width == 0) throw LineException("could not infer width of ubit", it.line)
                }
                TYPE_SBIT -> LangReifierUtil.bitToWidth(it, BitType.SBIT).also { width ->
                    if (width == 0) throw LineException("could not infer width of sbit", it.line)
                }
                else -> throw LineException("could not concatenate type ${it.getTypeReifiedNotNull()}", it.line)
            }
            totalWidth += width
        }
        return TYPE_UBIT.toTypeReifiedInstance(totalWidth)
    }
}
