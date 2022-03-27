/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

abstract class ProjectStage {

    abstract fun process(projectContext: ProjectContext)

    override fun toString(): String {
        return this::class.simpleName.toString()
    }
}
