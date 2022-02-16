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

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object OverrideCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(OverrideCheckerVisitor)
    }

    private object OverrideCheckerVisitor : TreeVisitor() {

        override fun visitKtFunction(function: EKtFunction) {
            super.visitKtFunction(function)
            val overriddenFunction = function.overriddenFunction
            if (overriddenFunction != null) {
                val isTask = function.hasAnnotationEntry(AnnotationEntries.TASK)
                val overriddenFunctionIsTask = overriddenFunctionIsTask(overriddenFunction)
                when {
                    overriddenFunctionIsTask && !isTask -> Messages.FUNCTION_IS_TASK.on(function, function.name)
                    !overriddenFunctionIsTask && isTask -> Messages.FUNCTION_NOT_TASK.on(function, function.name)
                }
            }
        }

        private fun overriddenFunctionIsTask(function: Declaration): Boolean {
            return if (function is EKtFunction) {
                val overriddenFunction = function.overriddenFunction
                if (overriddenFunction != null) {
                    overriddenFunctionIsTask(overriddenFunction)
                } else {
                    function.hasAnnotationEntry(AnnotationEntries.TASK)
                }
            } else false
        }
    }
}
