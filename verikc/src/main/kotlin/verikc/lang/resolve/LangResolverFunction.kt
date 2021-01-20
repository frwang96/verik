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
import verikc.lang.LangSymbol.TYPE_UNIT
import verikc.rs.table.RsFunctionResolverRequest
import kotlin.math.abs

object LangResolverFunction {

    fun resolveAssign(request: RsFunctionResolverRequest): TypeGenerified {
        LangResolverCommon.inferWidthIfBit(request.expression.receiver!!, request.expression.args[0])
        LangResolverCommon.matchTypes(request.expression.receiver, request.expression.args[0])
        return TYPE_UNIT.toTypeGenerified()
    }

    fun resolveNativeGetIntInt(request: RsFunctionResolverRequest, typeSymbol: Symbol): TypeGenerified {
        val startIndex = LangResolverCommon.evaluateToInt(request.expression.args[0], request.symbolTable)
        val endIndex = LangResolverCommon.evaluateToInt(request.expression.args[1], request.symbolTable)
        val width = abs(startIndex - endIndex) + 1
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveBitwise(request: RsFunctionResolverRequest, typeSymbol: Symbol): TypeGenerified {
        LangResolverCommon.inferWidthIfBit(request.expression.receiver!!, request.expression.args[0])
        LangResolverCommon.matchWidth(request.expression.receiver, request.expression.args[0])
        val width = LangResolverCommon.bitToWidth(request.expression.receiver)
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveExt(request: RsFunctionResolverRequest, typeSymbol: Symbol): TypeGenerified {
        val width = LangResolverCommon.evaluateToInt(request.expression.args[0], request.symbolTable)
        val originalWidth = LangResolverCommon.bitToWidth(request.expression.receiver!!)
        if (width <= originalWidth) throw LineException(
            "extended width $width not longer than original width $originalWidth",
            request.expression.line
        )
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveTru(request: RsFunctionResolverRequest, typeSymbol: Symbol): TypeGenerified {
        val width = LangResolverCommon.evaluateToInt(request.expression.args[0], request.symbolTable)
        val originalWidth = LangResolverCommon.bitToWidth(request.expression.receiver!!)
        if (width >= originalWidth) throw LineException(
            "truncated width $width not shorter than original width $originalWidth",
            request.expression.line
        )
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveNativeAddSubMul(request: RsFunctionResolverRequest, typeSymbol: Symbol): TypeGenerified {
        LangResolverCommon.inferWidthIfBit(request.expression.receiver!!, request.expression.args[0])
        val leftWidth = LangResolverCommon.bitToWidth(request.expression.receiver)
        val rightWidth = LangResolverCommon.bitToWidth(request.expression.args[0])
        val width = Integer.max(leftWidth, rightWidth)
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveAddSub(request: RsFunctionResolverRequest, typeSymbol: Symbol): TypeGenerified {
        LangResolverCommon.inferWidthIfBit(request.expression.receiver!!, request.expression.args[0])
        val leftWidth = LangResolverCommon.bitToWidth(request.expression.receiver)
        val rightWidth = LangResolverCommon.bitToWidth(request.expression.args[0])
        val width = Integer.max(leftWidth, rightWidth) + 1
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveMul(request: RsFunctionResolverRequest, typeSymbol: Symbol): TypeGenerified {
        LangResolverCommon.inferWidthIfBit(request.expression.receiver!!, request.expression.args[0])
        val leftWidth = LangResolverCommon.bitToWidth(request.expression.receiver)
        val rightWidth = LangResolverCommon.bitToWidth(request.expression.args[0])
        val width = leftWidth + rightWidth
        return typeSymbol.toTypeGenerified(width)
    }

    fun resolveCat(request: RsFunctionResolverRequest): TypeGenerified {
        var totalWidth = 0
        request.expression.args.forEach {
            val width = when (it.getTypeGenerifiedNotNull().typeSymbol) {
                TYPE_BOOL -> 1
                TYPE_UBIT -> LangResolverCommon.bitToWidth(it).also { width ->
                    if (width == 0) throw LineException("could not infer width of ubit", it.line)
                }
                TYPE_SBIT -> LangResolverCommon.bitToWidth(it).also { width ->
                    if (width == 0) throw LineException("could not infer width of sbit", it.line)
                }
                else -> throw LineException("could not concatenate type ${it.getTypeGenerifiedNotNull()}", it.line)
            }
            totalWidth += width
        }
        return TYPE_UBIT.toTypeGenerified(totalWidth)
    }
}
