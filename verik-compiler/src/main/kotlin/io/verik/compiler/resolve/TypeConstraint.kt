/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

/**
 * Type constraint that imposes a relationship between the types referenced in the [typeAdapters].
 */
class TypeConstraint(
    val kind: TypeConstraintKind,
    val typeAdapters: List<TypeAdapter>
) {

    constructor(
        kind: TypeConstraintKind,
        vararg typeAdapters: TypeAdapter
    ) : this(kind, typeAdapters.toList())
}
