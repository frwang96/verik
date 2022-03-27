/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.test

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.common.EProject
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.common.TreeVisitor

fun EProject.findDeclaration(name: String): EDeclaration {
    val declarationVisitor = object : TreeVisitor() {
        val declarations = ArrayList<EDeclaration>()
        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element is EDeclaration && element.name == name)
                declarations.add(element)
        }
    }
    accept(declarationVisitor)
    when (declarationVisitor.declarations.size) {
        0 -> throw IllegalArgumentException("Could not find declaration")
        1 -> {}
        else -> throw IllegalArgumentException("Could not find unique declaration")
    }
    return declarationVisitor.declarations[0]
}
