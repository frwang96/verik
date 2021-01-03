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

package verikc.lang.reify

import verikc.base.ast.LineException
import verikc.base.ast.TypeReified
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rf.ast.RfExpressionFunction
import java.lang.Integer.max
import kotlin.math.abs

object LangReifierFunction {

    fun reifyNativeAddBit(expression: RfExpressionFunction): TypeReified {
        LangReifierUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        val leftWidth = LangReifierUtil.bitToWidth(expression.receiver)
        val rightWidth = LangReifierUtil.bitToWidth(expression.args[0])
        val width = max(leftWidth, rightWidth)
        return expression.typeSymbol.toTypeReifiedInstance(width)
    }

    fun reifyAddBit(expression: RfExpressionFunction): TypeReified {
        LangReifierUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        val leftWidth = LangReifierUtil.bitToWidth(expression.receiver)
        val rightWidth = LangReifierUtil.bitToWidth(expression.args[0])
        val width = max(leftWidth, rightWidth) + 1
        return expression.typeSymbol.toTypeReifiedInstance(width)
    }

    fun reifyMulBit(expression: RfExpressionFunction): TypeReified {
        LangReifierUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        val leftWidth = LangReifierUtil.bitToWidth(expression.receiver)
        val rightWidth = LangReifierUtil.bitToWidth(expression.args[0])
        val width = leftWidth + rightWidth
        return expression.typeSymbol.toTypeReifiedInstance(width)
    }

    fun reifyNativeGet(expression: RfExpressionFunction): TypeReified {
        val startIndex = LangReifierUtil.intLiteralToInt(expression.args[0])
        val endIndex = LangReifierUtil.intLiteralToInt(expression.args[1])
        val width = abs(startIndex - endIndex) + 1
        return expression.typeSymbol.toTypeReifiedInstance(width)
    }

    fun reifyBitwise(expression: RfExpressionFunction): TypeReified {
        LangReifierUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        LangReifierUtil.matchWidth(expression.receiver, expression.args[0])
        val width = LangReifierUtil.bitToWidth(expression.receiver)
        return expression.typeSymbol.toTypeReifiedInstance(width)
    }

    fun reifyExt(expression: RfExpressionFunction): TypeReified {
        val width = LangReifierUtil.intLiteralToInt(expression.args[0])
        val originalWidth = LangReifierUtil.bitToWidth(expression.receiver!!)
        if (width <= originalWidth) throw LineException(
            "extended width $width not longer than original width $originalWidth",
            expression.line
        )
        return expression.typeSymbol.toTypeReifiedInstance(width)
    }

    fun reifyTru(expression: RfExpressionFunction): TypeReified {
        val width = LangReifierUtil.intLiteralToInt(expression.args[0])
        val originalWidth = LangReifierUtil.bitToWidth(expression.receiver!!)
        if (width >= originalWidth) throw LineException(
            "truncated width $width not shorter than original width $originalWidth",
            expression.line
        )
        return expression.typeSymbol.toTypeReifiedInstance(width)
    }

    fun reifyCat(expression: RfExpressionFunction): TypeReified {
        var totalWidth = 0
        expression.args.forEach {
            val width = when (it.getTypeReifiedNotNull().typeSymbol) {
                TYPE_BOOL -> 1
                TYPE_UBIT -> LangReifierUtil.bitToWidth(it).also { width ->
                    if (width == 0) throw LineException("could not infer width of ubit", it.line)
                }
                TYPE_SBIT -> LangReifierUtil.bitToWidth(it).also { width ->
                    if (width == 0) throw LineException("could not infer width of sbit", it.line)
                }
                else -> throw LineException("could not concatenate type ${it.getTypeReifiedNotNull()}", it.line)
            }
            totalWidth += width
        }
        return TYPE_UBIT.toTypeReifiedInstance(totalWidth)
    }
}
