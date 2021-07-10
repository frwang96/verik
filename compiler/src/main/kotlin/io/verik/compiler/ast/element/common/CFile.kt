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

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.element.kt.KImportDirective
import io.verik.compiler.ast.interfaces.DeclarationContainer
import io.verik.compiler.ast.property.SourceSetType
import io.verik.compiler.ast.property.SourceType
import io.verik.compiler.common.PackageDeclaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.main.SourceLocation
import io.verik.compiler.main.m
import java.nio.file.Path

class CFile(
    override val location: SourceLocation,
    val inputPath: Path,
    private val outputPath: Path?,
    val relativePath: Path,
    val sourceSetType: SourceSetType,
    val sourceType: SourceType?,
    var packageDeclaration: PackageDeclaration,
    var declarations: ArrayList<CDeclaration>,
    private val importDirectives: List<KImportDirective>
) : CElement(), DeclarationContainer {

    init {
        importDirectives.forEach { it.parent = this }
        declarations.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitCFile(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        importDirectives.forEach { it.accept(visitor) }
        declarations.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldDeclaration: CDeclaration, newDeclaration: CDeclaration) {
        newDeclaration.parent = this
        if (!declarations.replaceIfContains(oldDeclaration, newDeclaration))
            m.error("Could not find declaration $oldDeclaration in file", this)
    }

    fun getOutputPathNotNull(): Path {
        return outputPath
            ?: m.fatal("File output path not specified", location)
    }
}