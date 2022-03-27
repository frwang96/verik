/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.sv.EInjectedProperty
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object PackageInjectedPropertyReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.regularPackages().forEach { processPackage(it) }
    }

    private fun processPackage(pkg: EPackage) {
        pkg.files.find { it.name == "Pkg.kt" }?.let { processFile(it, pkg) }
        pkg.files.find { it.name == "pkg.kt" }?.let { processFile(it, pkg) }
    }

    private fun processFile(file: EFile, pkg: EPackage) {
        pkg.files.remove(file)
        val injectedProperties = getInjectedProperties(file)
        injectedProperties.forEach { it.parent = pkg }
        pkg.injectedProperties = injectedProperties
    }

    private fun getInjectedProperties(file: EFile): ArrayList<EInjectedProperty> {
        val injectedProperties = ArrayList<EInjectedProperty>()
        file.declarations.forEach {
            if (it is EInjectedProperty) {
                checkInjectedProperty(it)
                injectedProperties.add(it)
            } else {
                Messages.NOT_INJECTED_PROPERTY.on(it)
            }
        }
        return injectedProperties
    }

    private fun checkInjectedProperty(injectedProperty: EInjectedProperty) {
        injectedProperty.injectedExpression.entries.forEach {
            if (it is ExpressionStringEntry) {
                Messages.INJECTED_PROPERTY_NOT_LITERAL.on(it.expression)
            }
        }
    }
}
