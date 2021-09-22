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

    val F_PRINT = object : CoreKtTransformableFunctionDeclaration(parent, "print") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_WRITE
            return callExpression
        }
    }

    val F_PRINT_ANY = object : CoreKtTransformableFunctionDeclaration(parent, "print", Core.Kt.C_ANY) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_WRITE
            return callExpression
        }
    }

    val F_PRINT_INT = object : CoreKtTransformableFunctionDeclaration(parent, "print", Core.Kt.C_INT) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_WRITE
            return callExpression
        }
    }

    val F_PRINTLN = object : CoreKtTransformableFunctionDeclaration(parent, "println") {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_DISPLAY
            return callExpression
        }
    }

    val F_PRINTLN_ANY = object : CoreKtTransformableFunctionDeclaration(parent, "println", Core.Kt.C_ANY) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_DISPLAY
            return callExpression
        }
    }

    val F_PRINTLN_INT = object : CoreKtTransformableFunctionDeclaration(parent, "println", Core.Kt.C_INT) {

        override fun transform(callExpression: EKtCallExpression): EExpression {
            callExpression.reference = Core.Sv.F_DISPLAY
            return callExpression
        }
    }
}
