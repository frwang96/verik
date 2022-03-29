/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.common.Declaration
import io.verik.importer.ast.element.common.EElement

/**
 * Base class of all elements that are declarations. [signature] is the original declaration signature.
 */
abstract class EDeclaration : EElement(), Declaration {

    abstract var signature: String?
}
