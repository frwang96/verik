/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.common

import io.verik.importer.ast.element.common.EElement

abstract class TreeVisitor : Visitor() {

    override fun visitElement(element: EElement) {
        element.acceptChildren(this)
    }
}
