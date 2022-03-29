/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.PackageKind

/**
 * Base class for all elements that are declarations.
 */
abstract class EDeclaration : ETypedElement(), Declaration {

    abstract var annotationEntries: List<AnnotationEntry>
    abstract var documentationLines: List<String>?

    fun hasAnnotationEntry(annotationEntry: AnnotationEntry): Boolean {
        return annotationEntries.any { it == annotationEntry }
    }

    fun isSpecializable(): Boolean {
        return when (val parent = this.parent) {
            is EFile -> true
            is EKtClass -> this in parent.declarations || this == parent.primaryConstructor
            else -> false
        }
    }

    fun getQualifiedName(): String? {
        if (this is EFile) return null
        return when (val parent = parent) {
            is EFile -> {
                val pkg = parent.getParentPackage()
                if (pkg.kind == PackageKind.REGULAR_ROOT) name
                else "${pkg.name}.$name"
            }
            is EDeclaration -> "${parent.getQualifiedName()}.$name"
            else -> null
        }
    }
}
