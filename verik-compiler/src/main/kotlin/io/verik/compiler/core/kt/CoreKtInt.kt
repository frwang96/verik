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

import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtBinaryFunctionDeclaration
import io.verik.compiler.core.common.CoreScope

object CoreKtInt : CoreScope(Core.Kt.C_INT) {

    val F_TIMES_INT = object : CoreKtBinaryFunctionDeclaration(parent, "times", Core.Kt.C_INT) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.MUL
        }
    }

    val F_PLUS_INT = object : CoreKtBinaryFunctionDeclaration(parent, "plus", Core.Kt.C_INT) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.PLUS
        }
    }

    val F_MINUS_INT = object : CoreKtBinaryFunctionDeclaration(parent, "minus", Core.Kt.C_INT) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.MINUS
        }
    }
}
