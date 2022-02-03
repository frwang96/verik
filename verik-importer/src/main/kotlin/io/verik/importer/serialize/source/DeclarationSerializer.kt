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

package io.verik.importer.serialize.source

import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EEnum
import io.verik.importer.ast.element.declaration.EEnumEntry
import io.verik.importer.ast.element.declaration.EKtClass
import io.verik.importer.ast.element.declaration.EKtConstructor
import io.verik.importer.ast.element.declaration.EKtFunction
import io.verik.importer.ast.element.declaration.EKtValueParameter
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.declaration.ETypeParameter
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.core.Core

object DeclarationSerializer {

    fun serializeClass(`class`: EKtClass, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(`class`, serializeContext)
        if (`class`.isOpen) {
            serializeContext.append("open ")
        }
        serializeContext.append("class ")
        serializeContext.serializeName(`class`)
        if (`class`.typeParameters.isNotEmpty()) {
            serializeContext.appendLine("<")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(`class`.typeParameters)
            }
            serializeContext.append(">")
        }
        if (`class`.valueParameters.isNotEmpty()) {
            serializeContext.appendLine("(")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(`class`.valueParameters)
            }
            serializeContext.append(")")
        }
        if (`class`.superDescriptor.type.reference != Core.C_Any) {
            serializeContext.append(" : ${`class`.superDescriptor.type}")
            if (`class`.getConstructor() == null) {
                serializeSuperConstructorCall(`class`.superDescriptor, serializeContext)
            }
        }
        if (`class`.declarations.isNotEmpty()) {
            serializeContext.appendLine(" {")
            serializeContext.indent {
                `class`.declarations.forEach { serializeContext.serialize(it) }
            }
            serializeContext.appendLine("}")
        } else {
            serializeContext.appendLine()
        }
    }

    fun serializeEnum(enum: EEnum, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(enum, serializeContext)
        serializeContext.append("enum class ")
        serializeContext.serializeName(enum)
        if (enum.entries.isNotEmpty()) {
            serializeContext.appendLine(" {")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(enum.entries)
            }
            serializeContext.appendLine("}")
        } else {
            serializeContext.appendLine()
        }
    }

    fun serializeTypeAlias(typeAlias: ETypeAlias, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(typeAlias, serializeContext)
        serializeContext.append("typealias ")
        serializeContext.serializeName(typeAlias)
        serializeContext.appendLine(" = ${typeAlias.descriptor.type}")
    }

    fun serializeTypeParameter(typeParameter: ETypeParameter, serializeContext: SerializeContext) {
        serializeContext.serializeName(typeParameter)
        if (typeParameter.isCardinal) {
            serializeContext.append(" : `*`")
        }
    }

    fun serializeFunction(function: EKtFunction, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(function, serializeContext)
        function.annotationEntries.forEach {
            serializeContext.appendLine("@${it.name}")
        }
        when {
            function.isOverride -> serializeContext.append("override ")
            function.isOpen -> serializeContext.append("open ")
        }
        serializeContext.append("fun ")
        serializeContext.serializeName(function)
        if (function.valueParameters.isNotEmpty()) {
            serializeContext.appendLine("(")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(function.valueParameters)
            }
            serializeContext.append(")")
        } else {
            serializeContext.append("()")
        }
        serializeContext.appendLine(": ${function.descriptor.type} = imported()")
    }

    fun serializeConstructor(constructor: EKtConstructor, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(constructor, serializeContext)
        serializeContext.append("constructor")
        if (constructor.valueParameters.isNotEmpty()) {
            serializeContext.appendLine("(")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(constructor.valueParameters)
            }
            serializeContext.append(")")
        } else {
            serializeContext.append("()")
        }
        val `class` = constructor.parentNotNull().cast<EKtClass>()
        if (`class`.superDescriptor.type.reference != Core.C_Any) {
            serializeContext.append(" : super")
            serializeSuperConstructorCall(`class`.superDescriptor, serializeContext)
        }
        serializeContext.appendLine()
    }

    fun serializeProperty(property: EProperty, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(property, serializeContext)
        if (property.isMutable) {
            serializeContext.append("var ")
        } else {
            serializeContext.append("val ")
        }
        serializeContext.serializeName(property)
        serializeContext.appendLine(": ${property.descriptor.type} = imported()")
    }

    fun serializeValueParameter(valueParameter: EKtValueParameter, serializeContext: SerializeContext) {
        valueParameter.annotationEntries.forEach {
            serializeContext.append("@${it.name} ")
        }
        when (valueParameter.isMutable) {
            true -> serializeContext.append("var ")
            false -> serializeContext.append("val ")
        }
        serializeContext.serializeName(valueParameter)
        serializeContext.append(": ${valueParameter.descriptor.type}")
        if (valueParameter.hasDefault) {
            serializeContext.append(" = imported()")
        }
    }

    fun serializeEnumEntry(enumEntry: EEnumEntry, serializeContext: SerializeContext) {
        serializeContext.serializeName(enumEntry)
    }

    private fun serializeSuperConstructorCall(descriptor: EDescriptor, serializeContext: SerializeContext) {
        val declaration = descriptor.type.reference
        val constructor = if (declaration is EKtClass) declaration.getConstructor() else null
        if (constructor != null) {
            val parameterCount = constructor.valueParameters.size
            if (parameterCount != 0) {
                serializeContext.appendLine("(")
                serializeContext.indent {
                    serializeContext.append("imported()")
                    repeat(parameterCount - 1) {
                        serializeContext.append(",")
                        serializeContext.appendLine()
                        serializeContext.append("imported()")
                    }
                    serializeContext.appendLine()
                }
                serializeContext.append(")")
            } else {
                serializeContext.append("()")
            }
        } else {
            serializeContext.append("()")
        }
    }

    private fun serializeDocs(declaration: EDeclaration, serializeContext: SerializeContext) {
        serializeContext.appendLine("/**")
        val locationLine = declaration.location.line
        val locationStringShort = "${declaration.location.path.fileName}:$locationLine"
        val locationStringLong = "${declaration.location.path}:$locationLine"
        serializeContext.appendLine(" * $locationStringShort")
        serializeContext.appendLine(" * []($locationStringLong)")

        val signature = declaration.signature
        if (signature != null && signature.isNotBlank()) {
            serializeContext.appendLine(" *")
            serializeContext.appendLine(" *  ```")
            signature.lines().forEach {
                serializeContext.appendLine(" *  $it")
            }
            serializeContext.appendLine(" *  ```")
        }
        serializeContext.appendLine(" */")
    }
}
