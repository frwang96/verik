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

package io.verik.compiler.core.common

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

object CoreTransformUtil {

    fun callExpressionSigned(expression: EExpression): EKtCallExpression {
        return EKtCallExpression(
            expression.location,
            Core.Vk.C_Sbit.toType(expression.type.getWidthAsType(expression)),
            Target.F_signed,
            null,
            arrayListOf(expression),
            ArrayList()
        )
    }

    fun callExpressionUnsigned(expression: EExpression): EKtCallExpression {
        return EKtCallExpression(
            expression.location,
            Core.Vk.C_Ubit.toType(expression.type.getWidthAsType(expression)),
            Target.F_unsigned,
            null,
            arrayListOf(expression),
            ArrayList()
        )
    }

    fun plusInt(expression: EExpression, value: Int, location: SourceLocation): EExpression {
        val constantExpression = ConstantBuilder.buildInt(location, value)
        return EKtCallExpression(
            location,
            Core.Kt.C_Int.toType(),
            Core.Kt.Int.F_plus_Int,
            ExpressionCopier.deepCopy(expression),
            arrayListOf(constantExpression),
            ArrayList()
        )
    }

    fun plusUbit(expression: EExpression, value: Int, location: SourceLocation): EExpression {
        val constantExpression = ConstantBuilder.buildBitConstant(
            location,
            expression.type.asBitWidth(expression),
            value
        )
        return EKtCallExpression(
            location,
            expression.type.copy(),
            Core.Vk.Ubit.F_plus_Ubit,
            ExpressionCopier.deepCopy(expression),
            arrayListOf(constantExpression),
            ArrayList()
        )
    }
}
