/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.declaration.common.EDeclaration

object DependencyReorderer {

    fun <D : EDeclaration> reorder(
        declarations: List<D>,
        dependencies: List<Dependency>
    ): DependencyReordererResult<D> {
        val filteredDependencies = dependencies.filter {
            it.toDeclaration in declarations && it.fromDeclaration in declarations
        }

        val declarationEntries = declarations.map { declaration ->
            DeclarationEntry(
                declaration,
                filteredDependencies.filter { it.fromDeclaration == declaration },
                false
            )
        }

        val reorderedDeclarationsSet = HashSet<D>()
        val reorderedDeclarationsList = ArrayList<D>()
        var index = 0
        while (index < declarationEntries.size) {
            val declarationEntry = declarationEntries[index]
            if (!declarationEntry.isReordered) {
                if (declarationEntry.dependencies.all { it.toDeclaration in reorderedDeclarationsSet }) {
                    declarationEntry.isReordered = true
                    reorderedDeclarationsSet.add(declarationEntry.declaration)
                    reorderedDeclarationsList.add(declarationEntry.declaration)
                    index = 0
                } else {
                    index++
                }
            } else {
                index++
            }
        }

        val unsatisfiedDependencies = ArrayList<Dependency>()
        declarationEntries
            .filter { !it.isReordered }
            .forEach { declarationEntry ->
                reorderedDeclarationsList.add(declarationEntry.declaration)
                unsatisfiedDependencies.addAll(
                    declarationEntry.dependencies.filter { it.toDeclaration !in reorderedDeclarationsSet }
                )
            }
        return DependencyReordererResult(reorderedDeclarationsList, unsatisfiedDependencies)
    }

    private data class DeclarationEntry<D : EDeclaration>(
        val declaration: D,
        val dependencies: List<Dependency>,
        var isReordered: Boolean
    )
}
