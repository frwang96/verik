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

import io.verik.importer.ast.element.EElement
import io.verik.importer.ast.element.EModule
import io.verik.importer.ast.element.EPort
import io.verik.importer.ast.element.EProperty
import io.verik.importer.common.Visitor
import io.verik.importer.message.Messages

class SourceSerializerVisitor(
    private val serializerContext: SerializerContext
) : Visitor() {

    override fun visitElement(element: EElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to serialize element: $element")
    }

    override fun visitModule(module: EModule) {
        DeclarationSerializer.serializeModule(module, serializerContext)
    }

    override fun visitProperty(property: EProperty) {
        DeclarationSerializer.serializeProperty(property, serializerContext)
    }

    override fun visitPort(port: EPort) {
        DeclarationSerializer.serializePort(port, serializerContext)
    }
}
