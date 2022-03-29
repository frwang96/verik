/*
 * SPDX-License-Identifier: Apache-2.0
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

/**
 * Core special functions from the Verik core package.
 */
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

    val F_cp_Any_String = object : TransformableCoreFunctionDeclaration(parent, "cp", "fun cp(Any, vararg String)") {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cc_CoverPoint_CoverPoint_String = object : TransformableCoreFunctionDeclaration(
        parent,
        "cc",
        "fun cc(CoverPoint, CoverPoint, vararg String)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cc_CoverPoint_CoverPoint_CoverPoint_String = object : TransformableCoreFunctionDeclaration(
        parent,
        "cc",
        "fun cc(CoverPoint, CoverPoint, CoverPoint, vararg String)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_optional_Boolean_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "optional",
        "fun optional(Boolean, Function)"
    ) {

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }

    val F_cluster_Int_Function = object : TransformableCoreFunctionDeclaration(
        parent,
        "cluster",
        "fun cluster(Int, Function)"
    ) {

        override fun getTypeConstraints(callExpression: ECallExpression): List<TypeConstraint> {
            return listOf(
                TypeConstraint(
                    TypeConstraintKind.EQ_INOUT,
                    TypeAdapter.ofElement(callExpression, 1),
                    TypeAdapter.ofTypeArgument(callExpression, 0)
                )
            )
        }

        override fun transform(callExpression: ECallExpression): EExpression {
            Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, name)
            return ENothingExpression(callExpression.location)
        }
    }
}
