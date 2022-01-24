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
import io.verik.importer.ast.kt.element.KtConstructor
import io.verik.importer.ast.kt.element.KtElement
import io.verik.importer.ast.kt.element.KtFunction
import io.verik.importer.ast.kt.element.KtProperty
import io.verik.importer.ast.kt.element.KtValueParameter
import io.verik.importer.common.KtVisitor
import io.verik.importer.message.Messages

class SourceSerializerVisitor(
    private val serializeContext: SerializeContext
) : KtVisitor() {

    override fun visitElement(element: KtElement) {
        Messages.INTERNAL_ERROR.on(element, "Unable to serialize element: $element")
    }

    override fun visitClass(`class`: KtClass) {
        DeclarationSerializer.serializeClass(`class`, serializeContext)
    }

    override fun visitFunction(function: KtFunction) {
        DeclarationSerializer.serializeFunction(function, serializeContext)
    }

    override fun visitConstructor(constructor: KtConstructor) {
        DeclarationSerializer.serializeConstructor(constructor, serializeContext)
    }

    override fun visitProperty(property: KtProperty) {
        DeclarationSerializer.serializeProperty(property, serializeContext)
    }

    override fun visitValueParameter(valueParameter: KtValueParameter) {
        DeclarationSerializer.serializeValueParameter(valueParameter, serializeContext)
    }
}
