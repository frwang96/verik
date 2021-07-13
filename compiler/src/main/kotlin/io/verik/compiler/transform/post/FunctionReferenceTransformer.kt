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

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.core.common.CoreSvFunctionDeclaration
import io.verik.compiler.main.ProjectContext

object FunctionReferenceTransformer : ProjectPass {

    private val functionReferenceMap = HashMap<CoreKtFunctionDeclaration, CoreSvFunctionDeclaration>()

    init {
        functionReferenceMap[Core.Kt.Io.PRINT] = Core.Sv.WRITE
        functionReferenceMap[Core.Kt.Io.PRINT_ANY] = Core.Sv.WRITE
        functionReferenceMap[Core.Kt.Io.PRINT_INT] = Core.Sv.WRITE
        functionReferenceMap[Core.Kt.Io.PRINTLN] = Core.Sv.DISPLAY
        functionReferenceMap[Core.Kt.Io.PRINTLN_ANY] = Core.Sv.DISPLAY
        functionReferenceMap[Core.Kt.Io.PRINTLN_INT] = Core.Sv.DISPLAY
        functionReferenceMap[Core.Vk.RANDOM] = Core.Sv.RANDOM
        functionReferenceMap[Core.Vk.FINISH] = Core.Sv.FINISH
    }

    override fun pass(projectContext: ProjectContext) {
        projectContext.files.forEach {
            it.accept(FunctionReferenceVisitor)
        }
    }

    object FunctionReferenceVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            val reference = functionReferenceMap[callExpression.reference]
            if (reference != null) {
                callExpression.reference = reference
            }
        }
    }
}