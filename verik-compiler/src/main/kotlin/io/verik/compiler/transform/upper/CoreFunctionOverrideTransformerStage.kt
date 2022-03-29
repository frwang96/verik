/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvFunction
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that transforms the names of functions that override core functions if required.
 */
object CoreFunctionOverrideTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(CoreFunctionOverrideTransformerVisitor)
    }

    private object CoreFunctionOverrideTransformerVisitor : TreeVisitor() {

        override fun visitSvFunction(function: ESvFunction) {
            if (function.parent is ESvClass && function.valueParameters.isEmpty()) {
                when (function.name) {
                    "preRandomize" -> {
                        function.name = "pre_randomize"
                        function.isVirtual = false
                    }
                    "postRandomize" -> {
                        function.name = "post_randomize"
                        function.isVirtual = false
                    }
                }
            }
        }
    }
}
