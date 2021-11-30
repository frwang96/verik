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

import io.verik.importer.ast.common.Visitor
import io.verik.importer.ast.element.EDeclaration
import io.verik.importer.ast.element.EModule
import io.verik.importer.ast.element.EProperty
import io.verik.importer.main.Platform
import io.verik.importer.message.SourceLocation

class SourceSerializerVisitor(
    private val serializerContext: SerializerContext
) : Visitor() {

    fun serializeAsDeclaration(declaration: EDeclaration) {
        serializerContext.appendLine()
        if (serializerContext.labelSourceLocations) {
            serializerContext.appendLine("/**")
            serializerContext.appendLine(" * Imported: ${getLocationString(declaration.location)}")
            serializerContext.appendLine(" */")
        }
        declaration.accept(this)
    }

    override fun visitModule(module: EModule) {
        serializerContext.appendLine("class ${module.name} : Module()")
    }

    override fun visitProperty(property: EProperty) {
        serializerContext.appendLine("val ${property.name}: Boolean = imported()")
    }

    private fun getLocationString(location: SourceLocation): String {
        val pathString = Platform.getStringFromPath(location.path)
        return "$pathString:${location.line}:${location.column}"
    }
}
