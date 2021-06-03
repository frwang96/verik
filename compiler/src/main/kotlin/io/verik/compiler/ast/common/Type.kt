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

package io.verik.compiler.ast.common

import io.verik.compiler.ast.element.VkBaseClass
import io.verik.compiler.ast.element.VkTypeParameter
import io.verik.compiler.core.CoreCardinalDeclaration
import io.verik.compiler.core.CoreClass
import io.verik.compiler.core.CoreClassDeclaration
import io.verik.compiler.main.m

class Type(
    override var reference: Declaration,
    val arguments: ArrayList<Type>
): Reference {

    fun toClassType(): Type {
        return when (val reference = reference) {
            is VkBaseClass -> this
            is VkTypeParameter -> reference.type
            is CoreClassDeclaration -> this
            is CoreCardinalDeclaration -> CoreClass.CARDINAL.toNoArgumentsType()
            else -> m.fatal("Unexpected reference declaration: $reference", null)
        }
    }

    override fun toString(): String {
        return if (arguments.isNotEmpty()) {
            "$reference<${arguments.joinToString()}>"
        } else "$reference"
    }

    override fun equals(other: Any?): Boolean {
        return (other is Type) && (other.reference == reference) && (other.arguments == arguments)
    }

    override fun hashCode(): Int {
        var result = reference.hashCode()
        result = 31 * result + arguments.hashCode()
        return result
    }

    companion object {

        val NULL = Type(NullDeclaration, arrayListOf())
    }
}