/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.pre

import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.message.Messages

class Namespace {

    private val namespace = HashMap<String, EDeclaration>()

    operator fun set(name: String, declaration: EDeclaration) {
        if (name !in namespace) {
            namespace[name] = declaration
        } else {
            Messages.NAME_ALREADY_DEFINED.on(declaration, declaration.name)
        }
    }

    operator fun get(name: String): EDeclaration? {
        return namespace[name]
    }
}
