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

import io.verik.importer.ast.element.EDeclaration
import io.verik.importer.ast.element.EModule
import io.verik.importer.ast.element.EPort
import io.verik.importer.ast.element.EProperty
import io.verik.importer.ast.interfaces.cast
import io.verik.importer.ast.property.PortType
import io.verik.importer.main.Platform

object DeclarationSerializer {

    fun serializeModule(module: EModule, serializerContext: SerializerContext) {
        serializerContext.appendLine()
        serializeLocation(module, serializerContext)
        serializerContext.append("class ${module.name}")
        if (module.portReferences.isNotEmpty()) {
            serializerContext.appendLine("(")
            serializerContext.indent {
                serializerContext.serializeJoinAppendLine(module.portReferences) {
                    val port = it.reference.cast<EPort>(module)
                    serializerContext.serialize(port)
                }
            }
            serializerContext.append(")")
        }
        serializerContext.appendLine(" : Module()")
    }

    fun serializeProperty(property: EProperty, serializerContext: SerializerContext) {
        serializerContext.appendLine()
        serializeLocation(property, serializerContext)
        serializerContext.append("val ${property.name}: ")
        serializerContext.appendLine("${property.type} = imported()")
    }

    fun serializePort(port: EPort, serializerContext: SerializerContext) {
        serializeLocation(port, serializerContext)
        when (port.portType) {
            PortType.INPUT -> serializerContext.append("@In ")
            PortType.OUTPUT -> serializerContext.append("@Out ")
        }
        serializerContext.append("var ${port.name}: ${port.type}")
    }

    private fun serializeLocation(declaration: EDeclaration, serializerContext: SerializerContext) {
        if (serializerContext.labelSourceLocations) {
            val pathString = Platform.getStringFromPath(declaration.location.path)
            val locationString = "$pathString:${declaration.location.line}:${declaration.location.column}"
            serializerContext.appendLine("@Imported(\"$locationString\")")
        }
    }
}
