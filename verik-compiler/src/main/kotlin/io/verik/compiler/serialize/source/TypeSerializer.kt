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

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EModuleInterface
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.property.Type
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.utils.addToStdlib.cast

object TypeSerializer {

    fun serialize(type: Type, element: EElement): SerializedType {
        return when (val reference = type.reference) {
            is EModule -> SerializedType(reference.name)
            is EModuleInterface -> SerializedType(reference.name)
            is EAbstractClass -> SerializedType(serializePackageDeclaration(reference))
            Core.Kt.C_Unit -> SerializedType("void")
            Core.Kt.C_Int -> SerializedType("int")
            Core.Kt.C_Boolean -> SerializedType("logic")
            Core.Kt.C_String -> SerializedType("string")
            Core.Vk.C_Ubit -> {
                val value = type.arguments[0].asCardinalValue(element)
                SerializedType("logic", "[${value - 1}:0]", null)
            }
            Core.Vk.C_Packed -> {
                val serializedType = serialize(type.arguments[1], element)
                var packedDimension = "[${type.arguments[0].asCardinalValue(element) - 1}:0]"
                if (serializedType.packedDimension != null)
                    packedDimension += serializedType.packedDimension
                SerializedType(serializedType.base, packedDimension, serializedType.unpackedDimension)
            }
            Core.Vk.C_Unpacked -> {
                val serializedType = serialize(type.arguments[1], element)
                var unpackedDimension = "[${type.arguments[0].asCardinalValue(element) - 1}:0]"
                if (serializedType.unpackedDimension != null)
                    unpackedDimension += serializedType.unpackedDimension
                SerializedType(serializedType.base, serializedType.packedDimension, unpackedDimension)
            }
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Unable to serialize type: $type")
                SerializedType("void")
            }
        }
    }

    // TODO more general handling of scope resolution
    private fun serializePackageDeclaration(declaration: Declaration): String {
        val element = declaration as EElement
        val file = element.parent.cast<EFile>()
        val basicPackage = file.parent.cast<EBasicPackage>()
        return "${basicPackage.name}::${declaration.name}"
    }
}
