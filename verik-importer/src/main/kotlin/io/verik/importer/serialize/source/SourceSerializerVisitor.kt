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

import io.verik.importer.ast.sv.element.SvElement
import io.verik.importer.ast.sv.element.SvModule
import io.verik.importer.ast.sv.element.SvPort
import io.verik.importer.ast.sv.element.SvProperty
import io.verik.importer.common.SvVisitor
import io.verik.importer.message.Messages

class SourceSerializerVisitor(
    private val serializeContext: SerializeContext
) : SvVisitor() {

    override fun visitElement(element: SvElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to serialize element: $element")
    }

    override fun visitModule(module: SvModule) {
        DeclarationSerializer.serializeModule(module, serializeContext)
    }

    override fun visitProperty(property: SvProperty) {
        DeclarationSerializer.serializeProperty(property, serializeContext)
    }

    override fun visitPort(port: SvPort) {
        DeclarationSerializer.serializePort(port, serializeContext)
    }
}
