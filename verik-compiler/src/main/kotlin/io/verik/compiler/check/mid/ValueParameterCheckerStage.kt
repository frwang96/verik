/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.declaration.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ValueParameterCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ValueParameterCheckerVisitor)
    }

    private object ValueParameterCheckerVisitor : TreeVisitor() {

        override fun visitKtAbstractFunction(abstractFunction: EKtAbstractFunction) {
            super.visitKtAbstractFunction(abstractFunction)
            abstractFunction.valueParameters.forEach { checkValueParameter(it) }
        }

        private fun checkValueParameter(valueParameter: EKtValueParameter) {
            if (valueParameter.type.isSubtype(Core.Vk.C_Module)) {
                Messages.ILLEGAL_VALUE_PARAMETER_TYPE.on(valueParameter, valueParameter.type)
            }
        }
    }
}
