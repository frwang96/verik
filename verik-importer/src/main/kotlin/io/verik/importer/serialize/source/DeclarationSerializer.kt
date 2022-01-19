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
import io.verik.importer.ast.kt.element.KtElement
import io.verik.importer.ast.kt.element.KtProperty
import io.verik.importer.ast.kt.element.KtValueParameter
import io.verik.importer.core.Core

object DeclarationSerializer {

    fun serializeClass(`class`: KtClass, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeLocation(`class`, serializeContext)
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
            serializeContext.appendLine(" : ${`class`.superType}()")
        } else {
            serializeContext.appendLine()
        }
    }

    fun serializeProperty(property: KtProperty, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeLocation(property, serializeContext)
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

    private fun serializeLocation(element: KtElement, serializeContext: SerializeContext) {
        val locationString = "${element.location.path}:${element.location.line}"
        serializeContext.appendLine("/** Imported from: $locationString */")
    }
}
