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

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object FunctionOverloadingTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(FunctionOverloadingTransformerVisitor)
    }

    private object FunctionOverloadingTransformerVisitor : TreeVisitor() {

        private fun transformFunctions(functions: List<EKtFunction>) {
            val nameSet = HashSet<String>()
            val duplicatedNameSet = HashSet<String>()
            for (function in functions) {
                if (function.name !in nameSet)
                    nameSet.add(function.name)
                else
                    duplicatedNameSet.add(function.name)
            }
            functions.forEach {
                if (it.name in duplicatedNameSet)
                    transformFunction(it)
            }
        }

        private fun transformFunction(function: EKtFunction) {
            val suffix = function.valueParameters.joinToString(separator = "_") { it.type.reference.name }
            if (suffix != "")
                function.name = "${function.name}_$suffix"
        }

        override fun visitPackage(`package`: EPackage) {
            super.visitPackage(`package`)
            if (!`package`.packageType.isImported()) {
                val functions = `package`.files
                    .flatMap { it.declarations }
                    .filterIsInstance<EKtFunction>()
                transformFunctions(functions)
            }
        }

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            val functions = `class`.declarations.filterIsInstance<EKtFunction>()
            transformFunctions(functions)
        }
    }
}
