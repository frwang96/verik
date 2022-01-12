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

package io.verik.compiler.transform.lower

import io.verik.compiler.ast.element.common.EAbstractContainerClass
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.sv.EAbstractContainerComponent
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.transform.upper.ConstantPropagator

object ConstantPropagatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val constantPropagatorIndexerVisitor = ConstantPropagatorIndexerVisitor()
        projectContext.project.accept(constantPropagatorIndexerVisitor)
        val constantPropagatorTransformerVisitor = ConstantPropagatorTransformerVisitor(
            constantPropagatorIndexerVisitor.constantMap
        )
        projectContext.project.accept(constantPropagatorTransformerVisitor)
    }

    private class ConstantPropagatorIndexerVisitor : TreeVisitor() {

        val constantMap = HashMap<EProperty, EExpression>()

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            file.declarations = filterConstantProperties(file.declarations)
        }

        override fun visitAbstractContainerClass(abstractContainerClass: EAbstractContainerClass) {
            super.visitAbstractContainerClass(abstractContainerClass)
            abstractContainerClass.declarations = filterConstantProperties(abstractContainerClass.declarations)
        }

        override fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
            super.visitAbstractContainerComponent(abstractContainerComponent)
            abstractContainerComponent.declarations = filterConstantProperties(abstractContainerComponent.declarations)
        }

        private fun filterConstantProperties(declarations: ArrayList<EDeclaration>): ArrayList<EDeclaration> {
            val filteredDeclarations = ArrayList<EDeclaration>()
            declarations.forEach {
                if (it !is EProperty || !indexConstantProperty(it)) {
                    filteredDeclarations.add(it)
                }
            }
            return filteredDeclarations
        }

        private fun indexConstantProperty(property: EProperty): Boolean {
            val initializer = property.initializer
            if (!property.isMutable && initializer != null) {
                val expression = ConstantPropagator.expandExpression(initializer)
                if (ConstantPropagator.isConstant(expression)) {
                    constantMap[property] = expression
                    return true
                }
            }
            return false
        }
    }

    private class ConstantPropagatorTransformerVisitor(
        private val constantMap: HashMap<EProperty, EExpression>
    ) : TreeVisitor() {

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            super.visitReferenceExpression(referenceExpression)
            val expression = constantMap[referenceExpression.reference]
            if (expression != null) {
                referenceExpression.replace(ExpressionCopier.deepCopy(expression, referenceExpression.location))
            }
        }
    }
}
