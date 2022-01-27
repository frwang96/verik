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

package io.verik.importer.ast.kt.element

import io.verik.importer.ast.common.Type
import io.verik.importer.ast.property.AnnotationEntry
import io.verik.importer.common.KtVisitor
import io.verik.importer.message.SourceLocation

class KtFunction(
    override val location: SourceLocation,
    override val name: String,
    override val signature: String?,
    override val type: Type,
    val annotationEntries: List<AnnotationEntry>,
    val valueParameters: List<KtValueParameter>,
    val isOpen: Boolean
) : KtDeclaration() {

    init {
        valueParameters.forEach { it.parent = this }
    }

    override fun accept(visitor: KtVisitor) {
        visitor.visitFunction(this)
    }

    override fun acceptChildren(visitor: KtVisitor) {
        valueParameters.forEach { it.accept(visitor) }
    }
}
