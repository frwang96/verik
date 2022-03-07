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

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.ast.element.declaration.sv.EInjectedProperty
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.PackageKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class EPackage(
    override val location: SourceLocation,
    override var name: String,
    var files: ArrayList<EFile>,
    var injectedProperties: ArrayList<EInjectedProperty>,
    val kind: PackageKind
) : EDeclaration() {

    override var type = Core.Kt.C_Unit.toType()
    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null

    init {
        files.forEach { it.parent = this }
        injectedProperties.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitPackage(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        files.forEach { it.accept(visitor) }
        injectedProperties.forEach { it.accept(visitor) }
    }
}
