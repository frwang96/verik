/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.common.EElement

/**
 * A registry that stores the dependencies between the child declarations of an element.
 */
class DependencyRegistry {

    private val dependencyRegistry = HashMap<EElement, HashSet<Dependency>>()

    fun add(element: EElement, dependency: Dependency) {
        val dependencySet = dependencyRegistry[element]
        if (dependencySet != null) {
            dependencySet.add(dependency)
        } else {
            dependencyRegistry[element] = hashSetOf(dependency)
        }
    }

    fun getDependencies(element: EElement): List<Dependency> {
        val dependencySet = dependencyRegistry[element]
        return dependencySet?.toList() ?: listOf()
    }
}
