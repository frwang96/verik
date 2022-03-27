/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object TypeParameterCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(TypeParameterCheckerVisitor)
    }

    private object TypeParameterCheckerVisitor : TreeVisitor() {

        override fun visitKtFunction(function: EKtFunction) {
            if (function.typeParameters.isNotEmpty()) {
                val parent = function.parent
                if (parent !is EFile && parent !is ECompanionObject) {
                    Messages.PARAMETERIZED_FUNCTION_NOT_TOP.on(function, function.name)
                }
            }
        }
    }
}
