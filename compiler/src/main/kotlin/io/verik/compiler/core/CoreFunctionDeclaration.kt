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

package io.verik.compiler.core

import io.verik.compiler.ast.common.Name

class CoreFunctionDeclaration private constructor(
    override var name: Name,
    override val qualifiedName: Name,
    val parameterTypeNames: List<Name>
): CoreDeclaration() {

    companion object {

        operator fun invoke(parent: String, name: String, vararg parameterTypeNames: String): CoreFunctionDeclaration {
            return CoreFunctionDeclaration(
                Name(name),
                Name("$parent.$name"),
                parameterTypeNames.map { Name(it) }
            )
        }
    }
}