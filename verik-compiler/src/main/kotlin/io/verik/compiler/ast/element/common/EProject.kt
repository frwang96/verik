/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents the entire project. It contains regular packages and imported packages. Root packages are
 * handled separately from non-root packages as SystemVerilog handles the root namespace differently from package
 * namespaces.
 */
class EProject(
    override val location: SourceLocation,
    var regularNonRootPackages: ArrayList<EPackage>,
    val regularRootPackage: EPackage,
    val importedNonRootPackages: ArrayList<EPackage>,
    val importedRootPackage: EPackage
) : EElement() {

    init {
        regularNonRootPackages.forEach { it.parent = this }
        regularRootPackage.parent = this
        importedNonRootPackages.forEach { it.parent = this }
        importedRootPackage.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitProject(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        regularNonRootPackages.forEach { it.accept(visitor) }
        regularRootPackage.accept(visitor)
        importedNonRootPackages.forEach { it.accept(visitor) }
        importedRootPackage.accept(visitor)
    }

    fun packages(): List<EPackage> {
        return regularNonRootPackages + regularRootPackage + importedNonRootPackages + importedRootPackage
    }

    fun regularPackages(): List<EPackage> {
        return regularNonRootPackages + regularRootPackage
    }

    fun files(): List<EFile> {
        return regularNonRootPackages.flatMap { it.files } +
            regularRootPackage.files +
            importedNonRootPackages.flatMap { it.files } +
            importedRootPackage.files
    }

    fun regularFiles(): List<EFile> {
        return regularNonRootPackages.flatMap { it.files } + regularRootPackage.files
    }
}
