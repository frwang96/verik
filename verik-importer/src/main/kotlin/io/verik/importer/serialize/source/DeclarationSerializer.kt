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
import io.verik.importer.ast.element.declaration.EProperty
import io.verik.importer.ast.element.declaration.ETypeAlias

object DeclarationSerializer {

    fun serializeEnum(enum: EEnum, serializeContext: SerializeContext) {
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

    fun serializeTypeAlias(typeAlias: ETypeAlias, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(typeAlias, serializeContext)
        serializeContext.appendLine("typealias ${typeAlias.name} = ${typeAlias.descriptor.type}")
    }

    fun serializeProperty(property: EProperty, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(property, serializeContext)
        if (property.isMutable) {
            serializeContext.append("var ")
        } else {
            serializeContext.append("val ")
        }
        serializeContext.appendLine("${property.name}: ${property.descriptor.type} = imported()")
    }

    fun serializeEnumEntry(enumEntry: EEnumEntry, serializeContext: SerializeContext) {
        serializeContext.append(enumEntry.name)
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
