/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.property.PackageKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that transforms the Kotlin package names to SystemVerilog package names.
 */
object PackageNameTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(PackageNameTransformerVisitor)
    }

    private object PackageNameTransformerVisitor : TreeVisitor() {

        override fun visitPackage(pkg: EPackage) {
            when (pkg.kind) {
                PackageKind.REGULAR_NON_ROOT -> {
                    if (!pkg.name.matches(Regex("[a-zA-Z][a-zA-Z\\d]*(\\.[a-zA-Z][a-zA-Z\\d]*)*")))
                        Messages.INTERNAL_ERROR.on(pkg, "Unable to transform package name: ${pkg.name}")
                    val names = pkg.name.split(".")
                    val name = names.joinToString(separator = "_", postfix = "_pkg")
                    pkg.name = name
                }
                PackageKind.IMPORTED_NON_ROOT -> {
                    pkg.name = pkg.name.substringAfterLast(".")
                }
                else -> {}
            }
        }
    }
}
