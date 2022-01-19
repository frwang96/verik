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

import io.verik.importer.ast.sv.element.SvDeclaration
import io.verik.importer.ast.sv.element.SvModule
import io.verik.importer.ast.sv.element.SvPort
import io.verik.importer.ast.sv.element.SvProperty
import io.verik.importer.ast.sv.property.PortType
import io.verik.importer.main.Platform

object DeclarationSerializer {

    fun serializeModule(module: SvModule, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeLocation(module, serializeContext)
        serializeContext.append("class ${module.name}")
        if (module.ports.isNotEmpty()) {
            serializeContext.appendLine("(")
            serializeContext.indent {
                serializeContext.serializeJoinAppendLine(module.ports) {
                    serializeContext.serialize(it)
                }
            }
            serializeContext.append(")")
        }
        serializeContext.appendLine(" : Module()")
    }

    fun serializeProperty(property: SvProperty, serializeContext: SerializeContext) {
        serializeContext.appendLine()
        serializeLocation(property, serializeContext)
        serializeContext.append("val ${property.name}: ")
        serializeContext.appendLine("${property.type} = imported()")
    }

    fun serializePort(port: SvPort, serializeContext: SerializeContext) {
        serializeLocation(port, serializeContext)
        when (port.portType) {
            PortType.INPUT -> serializeContext.append("@In ")
            PortType.OUTPUT -> serializeContext.append("@Out ")
        }
        serializeContext.append("var ${port.name}: ${port.type}")
    }

    private fun serializeLocation(declaration: SvDeclaration, serializeContext: SerializeContext) {
        if (serializeContext.annotateDeclarations) {
            val pathString = Platform.getStringFromPath(declaration.location.path)
            val locationString = "$pathString:${declaration.location.line}"
            serializeContext.appendLine("@Imported(\"$locationString\")")
        }
    }
}
