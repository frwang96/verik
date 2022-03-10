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

import io.verik.compiler.ast.element.declaration.sv.EAbstractComponentInstantiation
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.ENothingExpression
import io.verik.compiler.ast.element.expression.sv.EInjectedExpression
import io.verik.compiler.ast.element.expression.sv.EScopeExpression
import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.TransformableCoreFunctionDeclaration
import io.verik.compiler.message.Messages
import io.verik.compiler.resolve.TypeAdapter
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.resolve.TypeConstraintKind

object CoreVkSpecial : CoreScope(CorePackage.VK) {

    val F_imp = object : TransformableCoreFunctionDeclaration(parent, "imp", "fun imp()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            if (!callExpression.isImported()) {
                Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            }
            return ENothingExpression(callExpression.location)
        }
    }

    val F_inj_String = BasicCoreFunctionDeclaration(parent, "inj", "fun inj(String)", null)

    val F_inji_String = object : BasicCoreFunctionDeclaration(parent, "inji", "fun inji(String)", null) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_imp.getTypeConstraints(callExpression)
        }
    }

    val F_t = object : TransformableCoreFunctionDeclaration(parent, "t", "fun t()") {

        override fun transform(callExpression: ECallExpression): EExpression {
            val parent = callExpression.parent
            if (parent !is EInjectedExpression) {
                Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            }
            return EScopeExpression(callExpression.location, callExpression.typeArguments[0], listOf())
        }
    }

    val F_nc = object : TransformableCoreFunctionDeclaration(parent, "nc", "fun nc()") {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return F_imp.getTypeConstraints(callExpression)
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            if (callExpression.parent !is EAbstractComponentInstantiation) {
                Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            }
            return ENothingExpression(callExpression.location)
        }
    }

    val F_c_Boolean = object : TransformableCoreFunctionDeclaration(parent, "c", "fun c(vararg Boolean)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_c_String = object : TransformableCoreFunctionDeclaration(parent, "c", "fun c(vararg String)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cp_Any = object : TransformableCoreFunctionDeclaration(parent, "cp", "fun cp(Any)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cp_Any_Function = object : TransformableCoreFunctionDeclaration(parent, "cp", "fun cp(Any, Function)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cc_Any = object : TransformableCoreFunctionDeclaration(parent, "cc", "fun cc(vararg CoverPoint)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cc_Any_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "cc",
        "fun cc(vararg CoverPoint, Function)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_optional_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "optional",
        "fun optional(Function)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cluster_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "cluster",
        "fun cluster(Function)"
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 0),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                ),
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 1),
                    TypeAdapter.ofTypeArgument(callExpression, 1)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }
}
