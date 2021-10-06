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

package io.verik.compiler.copy

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.message.Messages

class ReferenceForwardingMap {

    val referenceForwardingMap = HashMap<EDeclaration, EDeclaration>()

    operator fun set(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        referenceForwardingMap[oldDeclaration] = newDeclaration
    }

    operator fun get(declaration: Declaration): Declaration? {
        return referenceForwardingMap[declaration]
    }

    inline fun <reified D: EDeclaration> getAsDeclaration(declaration: D): D? {
        return when (val forwardedDeclaration = referenceForwardingMap[declaration]) {
            is D -> forwardedDeclaration
            null -> {
                Messages.INTERNAL_ERROR.on(declaration, "Forwarded declaration not found: ${declaration.name}")
                null
            }
            else -> {
                Messages.INTERNAL_ERROR.on(
                    declaration,
                    "Unexpected forwarded declaration: Expected ${D::class.simpleName} actual $forwardedDeclaration"
                )
                null
            }
        }
    }
}
