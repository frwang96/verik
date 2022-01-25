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

import io.verik.importer.common.KtVisitor
import io.verik.importer.message.SourceLocation

class KtProject(
    val packages: List<KtPackage>
) : KtElement() {

    override val location: SourceLocation = SourceLocation.NULL

    override fun accept(visitor: KtVisitor) {
        visitor.visitProject(this)
    }

    override fun acceptChildren(visitor: KtVisitor) {
        packages.forEach { it.accept(visitor) }
    }
}
