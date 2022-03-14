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
