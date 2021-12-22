/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.common.EDeclaration

object DependencyReorderer {

    fun reorder(declarations: List<EDeclaration>, dependencies: List<Dependency>): DependencyReordererResult {
        val declarationEntries = declarations.map { declaration ->
            DeclarationEntry(
                declaration,
                dependencies.filter { it.fromDeclaration == declaration },
                false
            )
        }

        val reorderedDeclarationsSet = HashSet<EDeclaration>()
        val reorderedDeclarationsList = ArrayList<EDeclaration>()
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

    private data class DeclarationEntry(
        val declaration: EDeclaration,
        val dependencies: List<Dependency>,
        var isReordered: Boolean
    )
}
