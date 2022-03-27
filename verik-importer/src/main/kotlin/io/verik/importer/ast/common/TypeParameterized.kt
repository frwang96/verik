/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.common

import io.verik.importer.ast.element.declaration.ETypeParameter

interface TypeParameterized {

    val typeParameters: List<ETypeParameter>
}
