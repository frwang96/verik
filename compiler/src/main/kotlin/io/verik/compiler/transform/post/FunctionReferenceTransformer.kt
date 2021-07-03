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

import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.VkCallExpression
import io.verik.compiler.core.CoreFunction
import io.verik.compiler.core.CoreKtFunctionDeclaration
import io.verik.compiler.core.CoreSvFunctionDeclaration
import io.verik.compiler.main.ProjectContext

object FunctionReferenceTransformer {

    private val functionReferenceMap = HashMap<CoreKtFunctionDeclaration, CoreSvFunctionDeclaration>()

    init {
        functionReferenceMap[CoreFunction.Core.RANDOM] = CoreFunction.Sv.RANDOM
        functionReferenceMap[CoreFunction.KotlinIo.PRINT] = CoreFunction.Sv.WRITE
        functionReferenceMap[CoreFunction.KotlinIo.PRINT_ANY] = CoreFunction.Sv.WRITE
        functionReferenceMap[CoreFunction.KotlinIo.PRINT_INT] = CoreFunction.Sv.WRITE
        functionReferenceMap[CoreFunction.KotlinIo.PRINTLN] = CoreFunction.Sv.DISPLAY
        functionReferenceMap[CoreFunction.KotlinIo.PRINTLN_ANY] = CoreFunction.Sv.DISPLAY
        functionReferenceMap[CoreFunction.KotlinIo.PRINTLN_INT] = CoreFunction.Sv.DISPLAY
    }

    fun transform(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(FunctionReferenceVisitor)
        }
    }

    object FunctionReferenceVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: VkCallExpression) {
            super.visitCallExpression(callExpression)
            val reference = functionReferenceMap[callExpression.reference]
            if (reference != null) {
                callExpression.reference = reference
            }
        }
    }
}