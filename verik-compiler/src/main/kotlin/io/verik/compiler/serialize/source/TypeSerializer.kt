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

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.declaration.sv.EModuleInterface
import io.verik.compiler.ast.element.declaration.sv.EModulePort
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ETypeDefinition
import io.verik.compiler.message.Messages
import io.verik.compiler.target.common.TargetClassDeclaration
import io.verik.compiler.target.common.TargetPackage

object TypeSerializer {

    fun serialize(type: Type, element: EElement): SerializedType {
        return when (val reference = type.reference) {
            is TargetPackage -> SerializedType(reference.name)
            is TargetClassDeclaration -> reference.serializeType(type.arguments, element)
            is EPackage -> SerializedType(reference.name)
            is ETypeDefinition -> SerializedType(reference.name)
            is EAbstractContainerComponent -> {
                SerializedType(reference.name, null, reference is EModuleInterface)
            }
            is EModulePort -> {
                val parentModuleInterface = reference.parentModuleInterface
                if (parentModuleInterface != null) {
                    SerializedType(parentModuleInterface.name)
                } else {
                    Messages.INTERNAL_ERROR.on(element, "Module port has no parent module interface")
                }
            }
            is EAbstractClass -> serializeAbstractClass(reference, element)
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Unable to serialize type: $type")
            }
        }
    }

    private fun serializeAbstractClass(abstractClass: EAbstractClass, element: EElement): SerializedType {
        val elementPackage = element.getParentPackage()
        val referencePackage = abstractClass.getParentPackage()
        val base = if (!referencePackage.packageType.isRoot() && referencePackage != elementPackage) {
            "${referencePackage.name}::${abstractClass.name}"
        } else abstractClass.name
        return if (abstractClass is ESvClass &&
            abstractClass.isImported() &&
            abstractClass.typeParameters.isNotEmpty()
        ) {
            val typeArgumentString = abstractClass.typeParameters.joinToString {
                val serializedType = serialize(it.type, element)
                serializedType.checkNoVariableDimension(element)
                ".${it.name}(${serializedType.base})"
            }
            SerializedType("$base #($typeArgumentString)")
        } else {
            SerializedType(base)
        }
    }
}
