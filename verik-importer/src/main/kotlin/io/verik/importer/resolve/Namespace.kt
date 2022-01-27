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

package io.verik.importer.resolve

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