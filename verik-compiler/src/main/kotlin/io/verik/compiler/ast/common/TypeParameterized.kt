/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.common

import io.verik.compiler.ast.element.declaration.common.ETypeParameter

/**
 * Interface for declarations that are type parameterized.
 */
interface TypeParameterized {

    var typeParameters: ArrayList<ETypeParameter>
}
