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

import io.verik.importer.ast.kt.element.KtClass
import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.kt.element.KtFunction
import io.verik.importer.ast.kt.element.KtProperty
import io.verik.importer.ast.kt.element.KtValueParameter
import io.verik.importer.core.Core

object DeclarationSerializer {

    fun serializeClass(`class`: KtClass, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(`class`, serializeContext)
        serializeContext.append("class ${`class`.name}")
        if (`class`.valueParameters.isNotEmpty()) {
            serializeContext.appendLine("(")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(`class`.valueParameters) {
                    serializeContext.serialize(it)
                }
            }
            serializeContext.append(")")
        }
        if (`class`.superType.reference != Core.C_Any) {
            serializeContext.append(" : ${`class`.superType}()")
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

    fun serializeFunction(function: KtFunction, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(function, serializeContext)
        function.annotationEntries.forEach {
            serializeContext.appendLine("@${it.name}")
        }
        serializeContext.append("fun ${function.name}()")
        if (function.type.reference != Core.C_Unit) {
            serializeContext.appendLine(": ${function.type} {")
            serializeContext.indent {
                serializeContext.appendLine("return imported()")
            }
            serializeContext.appendLine("}")
        } else {
            serializeContext.appendLine(" {}")
        }
    }

    fun serializeProperty(property: KtProperty, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeDocs(property, serializeContext)
        serializeContext.appendLine("val ${property.name}: ${property.type} = imported()")
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
