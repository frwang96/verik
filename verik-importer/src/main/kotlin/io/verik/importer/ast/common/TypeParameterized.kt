/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.common

import io.verik.importer.ast.element.declaration.ETypeParameter

/**
 * Interface for declarations that are type parameterized.
 */
interface TypeParameterized {

    val typeParameters: List<ETypeParameter>
}
