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

package verikc.lang.resolve

import verikc.base.ast.LineException
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rs.ast.RsExpressionFunction
import kotlin.math.abs

object LangResolverFunction {

    fun resolveNativeGetIntInt(expression: RsExpressionFunction, typeSymbol: Symbol): TypeGenerified {
        val startIndex = LangResolverUtil.evaluateToInt(expression.args[0])
        val endIndex = LangResolverUtil.evaluateToInt(expression.args[1])
        val width = abs(startIndex - endIndex) + 1
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveBitwise(expression: RsExpressionFunction, typeSymbol: Symbol): TypeGenerified {
        LangResolverUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        LangResolverUtil.matchWidth(expression.receiver, expression.args[0])
        val width = LangResolverUtil.bitToWidth(expression.receiver)
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveExt(expression: RsExpressionFunction, typeSymbol: Symbol): TypeGenerified {
        val width = LangResolverUtil.evaluateToInt(expression.args[0])
        val originalWidth = LangResolverUtil.bitToWidth(expression.receiver!!)
        if (width <= originalWidth) throw LineException(
            "extended width $width not longer than original width $originalWidth",
            expression.line
        )
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveTru(expression: RsExpressionFunction, typeSymbol: Symbol): TypeGenerified {
        val width = LangResolverUtil.evaluateToInt(expression.args[0])
        val originalWidth = LangResolverUtil.bitToWidth(expression.receiver!!)
        if (width >= originalWidth) throw LineException(
            "truncated width $width not shorter than original width $originalWidth",
            expression.line
        )
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveNativeAddSubMul(expression: RsExpressionFunction, typeSymbol: Symbol): TypeGenerified {
        LangResolverUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        val leftWidth = LangResolverUtil.bitToWidth(expression.receiver)
        val rightWidth = LangResolverUtil.bitToWidth(expression.args[0])
        val width = Integer.max(leftWidth, rightWidth)
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveAddSub(expression: RsExpressionFunction, typeSymbol: Symbol): TypeGenerified {
        LangResolverUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        val leftWidth = LangResolverUtil.bitToWidth(expression.receiver)
        val rightWidth = LangResolverUtil.bitToWidth(expression.args[0])
        val width = Integer.max(leftWidth, rightWidth) + 1
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveMul(expression: RsExpressionFunction, typeSymbol: Symbol): TypeGenerified {
        LangResolverUtil.inferWidthIfBit(expression.receiver!!, expression.args[0])
        val leftWidth = LangResolverUtil.bitToWidth(expression.receiver)
        val rightWidth = LangResolverUtil.bitToWidth(expression.args[0])
        val width = leftWidth + rightWidth
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveCat(expression: RsExpressionFunction): TypeGenerified {
        var totalWidth = 0
        expression.args.forEach {
            val width = when (it.getTypeGenerifiedNotNull().typeSymbol) {
                TYPE_BOOL -> 1
                TYPE_UBIT -> LangResolverUtil.bitToWidth(it).also { width ->
                    if (width == 0) throw LineException("could not infer width of ubit", it.line)
                }
                TYPE_SBIT -> LangResolverUtil.bitToWidth(it).also { width ->
                    if (width == 0) throw LineException("could not infer width of sbit", it.line)
                }
                else -> throw LineException("could not concatenate type ${it.getTypeGenerifiedNotNull()}", it.line)
            }
            totalWidth += width
        }
        return TYPE_UBIT.toTypeGenerified(totalWidth)
    }
}
