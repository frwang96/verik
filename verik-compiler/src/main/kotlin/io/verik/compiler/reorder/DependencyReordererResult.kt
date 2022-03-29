/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.declaration.common.EDeclaration

/**
 * Data class that stores the result of reordering declarations by their dependencies.
 */
data class DependencyReordererResult<D : EDeclaration>(
    val reorderedDeclarations: List<D>,
    val unsatisfiedDependencies: List<Dependency>
)
