/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.normalize

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Interface for normalization checkers that check for the validity of the AST. Normalization checks are only run in
 * debug mode.
 */
interface NormalizationChecker {

    fun check(projectContext: ProjectContext, projectStage: ProjectStage)

    companion object {

        fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
            ElementParentChecker.check(projectContext, projectStage)
            ElementAliasChecker.check(projectContext, projectStage)
            TypeAliasChecker.check(projectContext, projectStage)
            DanglingReferenceChecker.check(projectContext, projectStage)
        }
    }
}
