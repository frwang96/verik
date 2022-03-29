/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.ast.element.declaration.sv.EInjectedProperty
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.PackageKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a package. The package contains [injectedProperties] and [files]. [injectedProperties] are
 * used to inject SystemVerilog in the package before the files are included.
 */
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
