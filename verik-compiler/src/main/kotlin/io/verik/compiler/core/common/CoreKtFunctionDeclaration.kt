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
import io.verik.compiler.ast.property.SvBinaryOperatorKind
import io.verik.compiler.ast.property.SvUnaryOperatorKind
import io.verik.compiler.specialize.TypeConstraint

sealed class CoreKtAbstractFunctionDeclaration private constructor(
    override var name: String,
    override val qualifiedName: String,
    val parameterClassNames: List<String>
) : CoreAbstractFunctionDeclaration() {

    open fun getTypeConstraints(callExpression: EKtCallExpression): List<TypeConstraint> {
        return listOf()
    }

    constructor(
        parent: String,
        name: String,
        vararg parameterClassDeclarations: CoreClassDeclaration
    ) : this(
        name,
        "$parent.$name",
        parameterClassDeclarations.map { it.name }
    )
}

class CoreKtBasicFunctionDeclaration(
    parent: String,
    name: String,
    vararg parameterClassDeclarations: CoreClassDeclaration
) : CoreKtAbstractFunctionDeclaration(parent, name, *parameterClassDeclarations)

abstract class CoreKtTransformableFunctionDeclaration(
    parent: String,
    name: String,
    vararg parameterClassDeclarations: CoreClassDeclaration
) : CoreKtAbstractFunctionDeclaration(parent, name, *parameterClassDeclarations) {

    abstract fun transform(callExpression: EKtCallExpression): EExpression
}

abstract class CoreKtUnaryFunctionDeclaration(
    parent: String,
    name: String,
    vararg parameterClassDeclarations: CoreClassDeclaration
) : CoreKtAbstractFunctionDeclaration(parent, name, *parameterClassDeclarations) {

    abstract fun getOperatorKind(): SvUnaryOperatorKind
}

abstract class CoreKtBinaryFunctionDeclaration(
    parent: String,
    name: String,
    vararg parameterClassDeclarations: CoreClassDeclaration
) : CoreKtAbstractFunctionDeclaration(parent, name, *parameterClassDeclarations) {

    open fun evaluate(callExpression: EKtCallExpression): String? {
        return null
    }

    abstract fun getOperatorKind(): SvBinaryOperatorKind
}
