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
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.ast.element.sv.EModulePort
import io.verik.compiler.ast.element.sv.ETypeDefinition
import io.verik.compiler.ast.property.Type
import io.verik.compiler.message.Messages
import io.verik.compiler.target.common.TargetClassDeclaration

object TypeSerializer {

    fun serialize(type: Type, element: EElement): SerializedType {
        return when (val reference = type.reference) {
            is TargetClassDeclaration -> reference.serializeType(type.arguments, element)
            is EBasicPackage -> SerializedType(reference.name)
            is ETypeDefinition -> SerializedType(reference.name)
            is EAbstractContainerComponent -> SerializedType(reference.name)
            is EModulePort -> {
                val parentModuleInterface = reference.parentModuleInterface
                if (parentModuleInterface != null) {
                    SerializedType(parentModuleInterface.name)
                } else {
                    Messages.INTERNAL_ERROR.on(element, "Module port has no parent module interface")
                }
            }
            is EAbstractClass -> {
                val elementBasicPackage = element.getParentBasicPackage()
                val referenceBasicPackage = reference.getParentBasicPackage()
                if (referenceBasicPackage != null && referenceBasicPackage != elementBasicPackage) {
                    SerializedType("${referenceBasicPackage.name}::${reference.name}")
                } else SerializedType(reference.name)
            }
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Unable to serialize type: $type")
            }
        }
    }
}
