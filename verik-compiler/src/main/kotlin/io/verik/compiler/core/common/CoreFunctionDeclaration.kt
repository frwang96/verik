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

import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.resolve.TypeConstraint
import io.verik.compiler.target.common.TargetFunctionDeclaration

sealed class CoreFunctionDeclaration : CoreDeclaration {

    open fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
        return listOf()
    }
}

open class BasicCoreFunctionDeclaration(
    override val parent: CoreDeclaration,
    override var name: String,
    override val signature: String,
    val targetFunctionDeclaration: TargetFunctionDeclaration?
) : CoreFunctionDeclaration()

abstract class TransformableCoreFunctionDeclaration(
    override val parent: CoreDeclaration,
    override var name: String,
    override val signature: String
) : CoreFunctionDeclaration() {

    abstract fun transform(callExpression: EKtCallExpression): EExpression
}

open class UnaryCoreFunctionDeclaration(
    override val parent: CoreDeclaration,
    override var name: String,
    override val signature: String,
    val kind: SvUnaryOperatorKind
) : CoreFunctionDeclaration()

open class BinaryCoreFunctionDeclaration(
    override val parent: CoreDeclaration,
    override var name: String,
    override val signature: String,
    val kind: SvBinaryOperatorKind
) : CoreFunctionDeclaration() {

    open fun evaluate(callExpression: EKtCallExpression): EConstantExpression? {
        return null
    }
}
