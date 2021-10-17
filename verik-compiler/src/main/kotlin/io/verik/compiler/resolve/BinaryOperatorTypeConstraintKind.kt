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

package io.verik.compiler.resolve

import io.verik.compiler.ast.property.Type
import io.verik.compiler.core.common.Core

enum class BinaryOperatorTypeConstraintKind {
    MAX,
    MAX_INC,
    ADD;

    fun resolve(left: Type, right: Type): Type {
        return when (this) {
            MAX -> Core.Vk.N_MAX.toType(left.copy(), right.copy())
            MAX_INC -> Core.Vk.N_INC.toType(Core.Vk.N_MAX.toType(left.copy(), right.copy()))
            ADD -> Core.Vk.N_ADD.toType(left.copy(), right.copy())
        }
    }

    fun evaluate(leftValue: Int, rightValue: Int): Int {
        return when (this) {
            MAX -> Integer.max(leftValue, rightValue)
            MAX_INC -> Integer.max(leftValue, rightValue) + 1
            ADD -> leftValue + rightValue
        }
    }
}
