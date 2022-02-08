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

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class ETask(
    override val location: SourceLocation,
    override val name: String,
    override var signature: String?,
    override val valueParameters: List<ESvValueParameter>,
    override var isStatic: Boolean
) : ESvAbstractFunction() {

    init {
        valueParameters.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitTask(this)
    }
}
