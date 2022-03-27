/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ConstantPropertyEliminatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val constantPropertyIndexerVisitor = ConstantPropertyIndexerVisitor()
        projectContext.project.accept(constantPropertyIndexerVisitor)
        val constantPropertyEliminatorVisitor =
            ConstantPropertyEliminatorVisitor(constantPropertyIndexerVisitor.referencedProperties)
        projectContext.project.accept(constantPropertyEliminatorVisitor)
    }

    private class ConstantPropertyIndexerVisitor : TreeVisitor() {

        val referencedProperties = HashSet<EProperty>()

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            val reference = referenceExpression.reference
            if (reference is EProperty) {
                referencedProperties.add(reference)
            }
        }
    }

    private class ConstantPropertyEliminatorVisitor(
        private val referencedProperties: HashSet<EProperty>
    ) : TreeVisitor() {

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            file.declarations = filterDeclarations(file.declarations)
        }

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            cls.declarations = filterDeclarations(cls.declarations)
        }

        override fun visitBlockExpression(blockExpression: EBlockExpression) {
            super.visitBlockExpression(blockExpression)
            blockExpression.statements = filterStatements(blockExpression.statements)
        }

        private fun filterDeclarations(declarations: List<EDeclaration>): ArrayList<EDeclaration> {
            val filteredDeclarations = ArrayList<EDeclaration>()
            declarations.forEach {
                if (it !is EProperty || !isConstantProperty(it)) {
                    filteredDeclarations.add(it)
                }
            }
            return filteredDeclarations
        }

        private fun filterStatements(statements: List<EExpression>): ArrayList<EExpression> {
            val filteredStatements = ArrayList<EExpression>()
            statements.forEach {
                if (it !is EPropertyStatement || !isConstantProperty(it.property)) {
                    filteredStatements.add(it)
                }
            }
            return filteredStatements
        }

        private fun isConstantProperty(property: EProperty): Boolean {
            return !property.isMutable &&
                property.initializer is EConstantExpression &&
                property !in referencedProperties
        }
    }
}
