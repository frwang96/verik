/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.resolve

class TypeConstraint(
    val kind: TypeConstraintKind,
    val typeAdapters: List<TypeAdapter>
) {

    constructor(
        kind: TypeConstraintKind,
        vararg typeAdapters: TypeAdapter
    ) : this(kind, typeAdapters.toList())
}
