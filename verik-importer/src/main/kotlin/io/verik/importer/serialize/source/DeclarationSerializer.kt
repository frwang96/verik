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

import io.verik.importer.ast.common.Type
import io.verik.importer.ast.kt.element.KtClass
import io.verik.importer.ast.kt.element.KtConstructor
import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.kt.element.KtEnum
import io.verik.importer.ast.kt.element.KtEnumEntry
import io.verik.importer.ast.kt.element.KtFunction
import io.verik.importer.ast.kt.element.KtProperty
import io.verik.importer.ast.kt.element.KtTypeAlias
import io.verik.importer.ast.kt.element.KtValueParameter
import io.verik.importer.core.Core

object DeclarationSerializer {

    fun serializeClass(`class`: KtClass, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(`class`, serializeContext)
        if (`class`.isOpen) {
            serializeContext.append("open ")
        }
        serializeContext.append("class ${`class`.name}")
        if (`class`.valueParameters.isNotEmpty()) {
            serializeContext.appendLine("(")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(`class`.valueParameters)
            }
            serializeContext.append(")")
        }
        if (`class`.superType.reference != Core.C_Any) {
            serializeContext.append(" : ${`class`.superType}")
            if (`class`.getConstructor() == null) {
                serializeSuperConstructorCall(`class`.superType, serializeContext)
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

    fun serializeEnum(enum: KtEnum, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(enum, serializeContext)
        serializeContext.append("enum class ${enum.name}")
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

    fun serializeTypeAlias(typeAlias: KtTypeAlias, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(typeAlias, serializeContext)
        serializeContext.appendLine("typealias ${typeAlias.name} = ${typeAlias.type}")
    }

    fun serializeFunction(function: KtFunction, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(function, serializeContext)
        function.annotationEntries.forEach {
            serializeContext.appendLine("@${it.name}")
        }
        if (function.isOpen) {
            serializeContext.append("open ")
        }
        serializeContext.append("fun ${function.name}")
        if (function.valueParameters.isNotEmpty()) {
            serializeContext.appendLine("(")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(function.valueParameters)
            }
            serializeContext.append(")")
        } else {
            serializeContext.append("()")
        }
        serializeContext.appendLine(": ${function.type} = imported()")
    }

    fun serializeConstructor(constructor: KtConstructor, serializeContext: SerializeContext) {
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
        val `class` = constructor.parentNotNull().cast<KtClass>()
        if (`class`.superType.reference != Core.C_Any) {
            serializeContext.append(" : super")
            serializeSuperConstructorCall(`class`.superType, serializeContext)
        }
        serializeContext.appendLine()
    }

    fun serializeProperty(property: KtProperty, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(property, serializeContext)
        if (property.isMutable) {
            serializeContext.append("var ")
        } else {
            serializeContext.append("val ")
        }
        serializeContext.appendLine("${property.name}: ${property.type} = imported()")
    }

    fun serializeValueParameter(valueParameter: KtValueParameter, serializeContext: SerializeContext) {
        valueParameter.annotationEntries.forEach {
            serializeContext.append("@${it.name} ")
        }
        when (valueParameter.isMutable) {
            true -> serializeContext.append("var ")
            false -> serializeContext.append("val ")
        }
        serializeContext.append("${valueParameter.name}: ${valueParameter.type}")
    }

    fun serializeEnumEntry(enumEntry: KtEnumEntry, serializeContext: SerializeContext) {
        serializeContext.append(enumEntry.name)
    }

    private fun serializeSuperConstructorCall(type: Type, serializeContext: SerializeContext) {
        val declaration = type.reference
        val constructor = if (declaration is KtClass) declaration.getConstructor() else null
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

    private fun serializeDocs(declaration: KtDeclaration, serializeContext: SerializeContext) {
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
