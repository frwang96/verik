/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that performs name manging on overloaded functions.
 */
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

        override fun visitPackage(pkg: EPackage) {
            super.visitPackage(pkg)
            if (!pkg.kind.isImported()) {
                val functions = pkg.files
                    .flatMap { it.declarations }
                    .filterIsInstance<EKtFunction>()
                transformFunctions(functions)
            }
        }

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            val functions = cls.declarations.filterIsInstance<EKtFunction>()
            transformFunctions(functions)
        }
    }
}
