/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.main

/**
 * Base class for all importer stages.
 */
abstract class ProjectStage {

    abstract fun process(projectContext: ProjectContext)

    override fun toString(): String {
        return this::class.simpleName.toString()
    }
}
