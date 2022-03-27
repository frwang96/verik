/*
 * SPDX-License-Identifier: Apache-2.0
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
                    SerializedType(parentModuleInterface.name, null, true)
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
        val base = if (!referencePackage.kind.isRoot() && referencePackage != elementPackage) {
            "${referencePackage.name}::${abstractClass.name}"
        } else abstractClass.name
        return if (abstractClass is ESvClass &&
            abstractClass.isImported() &&
            abstractClass.typeParameters.isNotEmpty()
        ) {
            val typeArgumentString = abstractClass.typeParameters.joinToString {
                val serializedType = serialize(it.type, element)
                serializedType.checkNoVariableDimension(element)
                if (serializedType.isVirtual) {
                    ".${it.name}(virtual ${serializedType.base})"
                } else {
                    ".${it.name}(${serializedType.base})"
                }
            }
            SerializedType("$base #($typeArgumentString)")
        } else {
            SerializedType(base)
        }
    }
}
