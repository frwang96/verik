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

package verikc.lang.generify

import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.gex.ast.GexExpressionFunction
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import kotlin.math.abs

object LangGenerifierFunction {

    fun generifyNativeGet(expression: GexExpressionFunction): TypeGenerified {
        val startIndex = LangGenerifierUtil.intLiteralToInt(expression.args[0])
        val endIndex = LangGenerifierUtil.intLiteralToInt(expression.args[1])
        val width = abs(startIndex - endIndex) + 1
        return expression.typeSymbol.toTypeGenerifiedInstance(width)
    }

    fun generifyBitwise(expression: GexExpressionFunction): TypeGenerified {
        LangGenerifierUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        LangGenerifierUtil.matchWidth(expression.receiver, expression.args[0])
        val width = LangGenerifierUtil.bitToWidth(expression.receiver)
        return expression.typeSymbol.toTypeGenerifiedInstance(width)
    }

    fun generifyExt(expression: GexExpressionFunction): TypeGenerified {
        val width = LangGenerifierUtil.intLiteralToInt(expression.args[0])
        val originalWidth = LangGenerifierUtil.bitToWidth(expression.receiver!!)
        if (width <= originalWidth) throw LineException(
            "extended width $width not longer than original width $originalWidth",
            expression.line
        )
        return expression.typeSymbol.toTypeGenerifiedInstance(width)
    }

    fun generifyTru(expression: GexExpressionFunction): TypeGenerified {
        val width = LangGenerifierUtil.intLiteralToInt(expression.args[0])
        val originalWidth = LangGenerifierUtil.bitToWidth(expression.receiver!!)
        if (width >= originalWidth) throw LineException(
            "truncated width $width not shorter than original width $originalWidth",
            expression.line
        )
        return expression.typeSymbol.toTypeGenerifiedInstance(width)
    }

    fun generifyNativeAddSubMul(expression: GexExpressionFunction): TypeGenerified {
        LangGenerifierUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        val leftWidth = LangGenerifierUtil.bitToWidth(expression.receiver)
        val rightWidth = LangGenerifierUtil.bitToWidth(expression.args[0])
        val width = Integer.max(leftWidth, rightWidth)
        return expression.typeSymbol.toTypeGenerifiedInstance(width)
    }

    fun generifyAddSub(expression: GexExpressionFunction): TypeGenerified {
        LangGenerifierUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        val leftWidth = LangGenerifierUtil.bitToWidth(expression.receiver)
        val rightWidth = LangGenerifierUtil.bitToWidth(expression.args[0])
        val width = Integer.max(leftWidth, rightWidth) + 1
        return expression.typeSymbol.toTypeGenerifiedInstance(width)
    }

    fun generifyMul(expression: GexExpressionFunction): TypeGenerified {
        LangGenerifierUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        val leftWidth = LangGenerifierUtil.bitToWidth(expression.receiver)
        val rightWidth = LangGenerifierUtil.bitToWidth(expression.args[0])
        val width = leftWidth + rightWidth
        return expression.typeSymbol.toTypeGenerifiedInstance(width)
    }

    fun generifyCat(expression: GexExpressionFunction): TypeGenerified {
        var totalWidth = 0
        expression.args.forEach {
            val width = when (it.getTypeGenerifiedNotNull().typeSymbol) {
                TYPE_BOOL -> 1
                TYPE_UBIT -> LangGenerifierUtil.bitToWidth(it).also { width ->
                    if (width == 0) throw LineException("could not infer width of ubit", it.line)
                }
                TYPE_SBIT -> LangGenerifierUtil.bitToWidth(it).also { width ->
                    if (width == 0) throw LineException("could not infer width of sbit", it.line)
                }
                else -> throw LineException("could not concatenate type ${it.getTypeGenerifiedNotNull()}", it.line)
            }
            totalWidth += width
        }
        return TYPE_UBIT.toTypeGenerifiedInstance(totalWidth)
    }
}
