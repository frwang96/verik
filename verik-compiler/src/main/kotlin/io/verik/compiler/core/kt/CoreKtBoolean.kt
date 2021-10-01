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
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtBinaryFunctionDeclaration
import io.verik.compiler.core.common.CoreKtUnaryFunctionDeclaration
import io.verik.compiler.core.common.CoreScope

object CoreKtBoolean : CoreScope(Core.Kt.C_Boolean) {

    val F_not = object : CoreKtUnaryFunctionDeclaration(parent, "not") {

        override fun getOperatorKind(): SvUnaryOperatorKind {
            return SvUnaryOperatorKind.EXCL
        }
    }

    val F_and_Boolean = object : CoreKtBinaryFunctionDeclaration(parent, "and", Core.Kt.C_Boolean) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.ANDAND
        }
    }

    val F_or_Boolean = object : CoreKtBinaryFunctionDeclaration(parent, "or", Core.Kt.C_Boolean) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.OROR
        }
    }

    val F_xor_Boolean = object : CoreKtBinaryFunctionDeclaration(parent, "xor", Core.Kt.C_Boolean) {

        override fun getOperatorKind(): SvBinaryOperatorKind {
            return SvBinaryOperatorKind.XOR
        }
    }
}
