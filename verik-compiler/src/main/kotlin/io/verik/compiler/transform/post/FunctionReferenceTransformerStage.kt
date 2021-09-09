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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.core.common.CoreSvFunctionDeclaration
import io.verik.compiler.main.ProjectContext

object FunctionReferenceTransformerStage : ProjectStage() {

    override val checkNormalization = true

    private val functionReferenceMap = HashMap<CoreKtFunctionDeclaration, CoreSvFunctionDeclaration>()

    init {
        functionReferenceMap[Core.Kt.Io.F_PRINT] = Core.Sv.F_WRITE
        functionReferenceMap[Core.Kt.Io.F_PRINT_ANY] = Core.Sv.F_WRITE
        functionReferenceMap[Core.Kt.Io.F_PRINT_INT] = Core.Sv.F_WRITE
        functionReferenceMap[Core.Kt.Io.F_PRINTLN] = Core.Sv.F_DISPLAY
        functionReferenceMap[Core.Kt.Io.F_PRINTLN_ANY] = Core.Sv.F_DISPLAY
        functionReferenceMap[Core.Kt.Io.F_PRINTLN_INT] = Core.Sv.F_DISPLAY
        functionReferenceMap[Core.Vk.F_RANDOM] = Core.Sv.F_RANDOM
        functionReferenceMap[Core.Vk.F_TIME] = Core.Sv.F_TIME
        functionReferenceMap[Core.Vk.F_FINISH] = Core.Sv.F_FINISH
    }

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(FunctionReferenceTransformerVisitor)
    }

    object FunctionReferenceTransformerVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            val reference = functionReferenceMap[callExpression.reference]
            if (reference != null) {
                callExpression.reference = reference
            }
        }
    }
}
