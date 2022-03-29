/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.common.Type

/**
 * Abstract element that has a type.
 */
abstract class ETypedElement : EElement() {

    abstract var type: Type
}
