/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.pre

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.message.Messages

/**
 * A map from elements to namespaces.
 */
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
