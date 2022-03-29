/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.common

import io.verik.compiler.ast.element.common.EElement

/**
 * Visitor that recursively visits the children of each element.
 */
abstract class TreeVisitor : Visitor() {

    override fun visitElement(element: EElement) {
        element.acceptChildren(this)
    }
}
