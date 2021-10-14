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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.kt.EKtCallExpression

sealed class TypeConstraint

class TypeEqualsTypeConstraint(
    val inner: TypeAdapter,
    val outer: TypeAdapter
) : TypeConstraint()

class UnaryOperatorTypeConstraint(
    val inner: TypeAdapter,
    val outer: TypeAdapter,
    val isInnerToOuter: Boolean,
    val kind: UnaryOperatorTypeConstraintKind
) : TypeConstraint()

class BinaryOperatorTypeConstraint(
    val left: TypeAdapter,
    val right: TypeAdapter,
    val outer: TypeAdapter,
    val kind: BinaryOperatorTypeConstraintKind
) : TypeConstraint()

class ComparisonTypeConstraint(
    val inner: TypeAdapter,
    val outer: TypeAdapter,
    val kind: ComparisonTypeConstraintKind
) : TypeConstraint()

class ConcatenationTypeConstraint(
    val callExpression: EKtCallExpression
) : TypeConstraint()

class ReplicationTypeConstraint(
    val callExpression: EKtCallExpression
) : TypeConstraint()
