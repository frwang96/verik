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

package verikc.base.ast

sealed class TypeArgument

data class TypeArgumentInt(
    val value: Int
): TypeArgument() {

    override fun toString(): String {
        return value.toString()
    }
}

data class TypeArgumentTypeGenerified(
    val typeGenerified: TypeGenerified
): TypeArgument() {

    override fun toString(): String {
        return typeGenerified.toString()
    }
}