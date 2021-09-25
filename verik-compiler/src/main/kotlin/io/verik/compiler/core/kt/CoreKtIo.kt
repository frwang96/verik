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

package io.verik.compiler.core.kt

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtTransformableFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope

object CoreKtIo : CoreScope(CorePackage.KT_IO) {

    val F_print = object : CoreKtTransformableFunctionDeclaration(parent, "print") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_write
            return callExpression
        }
    }

    val F_print_Any = object : CoreKtTransformableFunctionDeclaration(parent, "print", Core.Kt.C_Any) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_write
            return callExpression
        }
    }

    val F_print_Int = object : CoreKtTransformableFunctionDeclaration(parent, "print", Core.Kt.C_Int) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_write
            return callExpression
        }
    }

    val F_println = object : CoreKtTransformableFunctionDeclaration(parent, "println") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_display
            return callExpression
        }
    }

    val F_println_Any = object : CoreKtTransformableFunctionDeclaration(parent, "println", Core.Kt.C_Any) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_display
            return callExpression
        }
    }

    val F_println_Int = object : CoreKtTransformableFunctionDeclaration(parent, "println", Core.Kt.C_Int) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_display
            return callExpression
        }
    }
}
