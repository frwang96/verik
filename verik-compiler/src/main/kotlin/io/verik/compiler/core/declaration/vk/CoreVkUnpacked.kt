/*
 * SPDX-License-Identifier: Apache-2.0
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

    val P_size = object : CorePropertyDeclaration(parent, "size") {

        override fun transform(referenceExpression: EReferenceExpression): EExpression {
            return CoreVkPacked.P_size.transform(referenceExpression)
        }
    }

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
}
