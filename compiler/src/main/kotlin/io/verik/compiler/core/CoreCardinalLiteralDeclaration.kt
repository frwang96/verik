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
import io.verik.compiler.ast.common.PackageName
import io.verik.compiler.main.messageCollector

class CoreCardinalLiteralDeclaration(
    val cardinal: Int
): CoreCardinalDeclaration() {

    init {
        if (cardinal < 1)
            messageCollector.error("Cardinal must be a positive integer: $cardinal", null)
    }

    override var name = Name("$cardinal")
    override var qualifiedName = Name("${PackageName.CORE.name}.$cardinal")

    override fun toString(): String {
        return "`$cardinal`"
    }

    override fun equals(other: Any?): Boolean {
        return (other is CoreCardinalLiteralDeclaration) && (other.cardinal == cardinal)
    }

    override fun hashCode(): Int {
        return cardinal
    }
}