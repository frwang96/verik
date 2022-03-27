/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.common.Declaration
import io.verik.importer.ast.element.common.EElement

abstract class EDeclaration : EElement(), Declaration {

    abstract var signature: String?
}
