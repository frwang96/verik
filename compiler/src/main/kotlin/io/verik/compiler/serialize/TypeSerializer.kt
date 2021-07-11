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

package io.verik.compiler.serialize

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.property.Type
import io.verik.compiler.core.Core
import io.verik.compiler.core.CoreCardinalConstantDeclaration
import io.verik.compiler.main.m

object TypeSerializer {

    fun serialize(declaration: EDeclaration): String {
        val type = declaration.type
        return when (type.reference) {
            Core.Kt.UNIT -> "void"
            Core.Kt.INT -> "int"
            Core.Kt.BOOLEAN -> "logic"
            Core.Kt.STRING -> "string"
            Core.Vk.UBIT -> "logic ${serializeCardinalLittleEndian(type.arguments[0], declaration)}"
            else -> {
                m.error("Unable to serialize type: $type", declaration)
                "void"
            }
        }
    }

    private fun serializeCardinalLittleEndian(type: Type, element: EElement): String {
        val reference = type.reference
        return if (reference is CoreCardinalConstantDeclaration) {
            val cardinal = reference.cardinal
            "[${cardinal - 1}:0]"
        } else {
            m.error("Could not get value of cardinal: $type", element)
            "[0:0]"
        }
    }
}