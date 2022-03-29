/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.ast.common.ResizableDeclarationContainer
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.serialize.source.SerializerUtil
import java.nio.file.Path

/**
 * Element that represents a file. The file contains [declarations].
 */
class EFile(
    override val location: SourceLocation,
    val inputPath: Path,
    val outputPath: Path,
    var declarations: ArrayList<EDeclaration>
) : EDeclaration(), ResizableDeclarationContainer {

    override var name = inputPath.fileName.toString()

    override var type = Core.Kt.C_Unit.toType()
    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null

    init {
        declarations.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitFile(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        declarations.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean {
        newDeclaration.parent = this
        return declarations.replaceIfContains(oldDeclaration, newDeclaration)
    }

    override fun insertChild(declaration: EDeclaration) {
        declaration.parent = this
        declarations.add(declaration)
    }

    override fun insertChildBefore(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        newDeclaration.parent = this
        val index = declarations.indexOf(oldDeclaration)
        if (index != -1) {
            declarations.add(index, newDeclaration)
        } else {
            Messages.INTERNAL_ERROR.on(oldDeclaration, "Could not find declaration: ${oldDeclaration.name}")
        }
    }

    fun isEmptySerialization(): Boolean {
        return declarations.all { SerializerUtil.declarationIsHidden(it) }
    }
}
