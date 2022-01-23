/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtClass
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

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            `class`.declarations = filterDeclarations(`class`.declarations)
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
