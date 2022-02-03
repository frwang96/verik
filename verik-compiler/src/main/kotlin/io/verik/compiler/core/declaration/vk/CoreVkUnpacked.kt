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

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePropertyDeclaration
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.resolve.TypeConstraint

object CoreVkUnpacked : CoreScope(Core.Vk.C_Unpacked) {

    val F_get_Int = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Int)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkPacked.F_get_Int.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkPacked.F_get_Int.transform(callExpression)
        }
    }

    val F_get_Ubit = object : TransformableCoreFunctionDeclaration(parent, "get", "fun get(Ubit)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkPacked.F_get_Ubit.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkPacked.F_get_Ubit.transform(callExpression)
        }
    }

    val F_set_Int_E = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(Int, E)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkPacked.F_set_Int_E.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkPacked.F_set_Int_E.transform(callExpression)
        }
    }

    val F_set_Ubit_E = object : TransformableCoreFunctionDeclaration(parent, "set", "fun set(E)") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return CoreVkPacked.F_set_Ubit_E.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            return CoreVkPacked.F_set_Ubit_E.transform(callExpression)
        }
    }

    val P_size = object : CorePropertyDeclaration(parent, "size") {

        override fun transform(referenceExpression: EReferenceExpression): EExpression {
            return CoreVkPacked.P_size.transform(referenceExpression)
        }
    }
}
