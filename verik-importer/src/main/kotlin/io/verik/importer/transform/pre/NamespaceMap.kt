/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.transform.pre

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.message.Messages

class NamespaceMap {

    private val namespaceMap = HashMap<EElement, Namespace>()

    operator fun set(element: EElement, namespace: Namespace) {
        if (element !in namespaceMap) {
            namespaceMap[element] = namespace
        } else {
            Messages.INTERNAL_ERROR.on(element, "Namespace has already been registered")
        }
    }

    operator fun get(element: EElement, name: String): EDeclaration? {
        var parent: EElement? = element
        while (parent != null) {
            val namespace = namespaceMap[parent]
            if (namespace != null) {
                namespace[name]?.let { return it }
            }
            parent = parent.parent
        }
        return null
    }
}
