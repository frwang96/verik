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

import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object FunctionOverloadingTransformerStage : ProjectStage() {

    override val checkNormalization = true

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

        override fun visitBasicPackage(basicPackage: EBasicPackage) {
            super.visitBasicPackage(basicPackage)
            val functions = basicPackage.files
                .flatMap { it.declarations }
                .filterIsInstance<EKtFunction>()
            transformFunctions(functions)
        }

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            val functions = basicClass.declarations.filterIsInstance<EKtFunction>()
            transformFunctions(functions)
        }
    }
}
