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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.ESuperExpression
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ClassInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val constructorIndexerVisitor = ConstructorIndexerVisitor(referenceUpdater)
        projectContext.project.accept(constructorIndexerVisitor)
        val classInterpreterVisitor = ClassInterpreterVisitor(
            referenceUpdater,
            constructorIndexerVisitor.constructorMap
        )
        projectContext.project.accept(classInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ConstructorIndexerVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        val constructorMap = HashMap<ESecondaryConstructor, ESvConstructor>()

        override fun visitSecondaryConstructor(secondaryConstructor: ESecondaryConstructor) {
            super.visitSecondaryConstructor(secondaryConstructor)
            val valueParameters = ArrayList<ESvValueParameter>()
            secondaryConstructor.valueParameters.forEach {
                val valueParameter = ESvValueParameter(
                    location = it.location,
                    name = it.name,
                    type = it.type,
                    annotationEntries = it.annotationEntries,
                    expression = it.expression,
                    isInput = true
                )
                valueParameters.add(valueParameter)
                referenceUpdater.update(it, valueParameter)
            }
            val constructor = ESvConstructor(
                location = secondaryConstructor.location,
                type = secondaryConstructor.type,
                annotationEntries = secondaryConstructor.annotationEntries,
                documentationLines = secondaryConstructor.documentationLines,
                body = secondaryConstructor.body,
                valueParameters = ArrayList(valueParameters)
            )
            constructorMap[secondaryConstructor] = constructor
        }
    }

    private class ClassInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater,
        private val constructorMap: HashMap<ESecondaryConstructor, ESvConstructor>
    ) : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            val declarations = ArrayList<EDeclaration>()
            cls.declarations.forEach {
                if (it is ESecondaryConstructor) {
                    val constructor = interpretConstructor(it)
                    constructor.parent = cls
                    declarations.add(constructor)
                } else {
                    declarations.add(it)
                }
            }
            val interpretedClass = ESvClass(
                location = cls.location,
                bodyStartLocation = cls.bodyStartLocation,
                bodyEndLocation = cls.bodyEndLocation,
                name = cls.name,
                type = cls.type,
                annotationEntries = cls.annotationEntries,
                documentationLines = cls.documentationLines,
                superType = cls.superType,
                typeParameters = cls.typeParameters,
                declarations = declarations,
                isVirtual = cls.isAbstract,
                isObject = cls.isObject
            )
            referenceUpdater.replace(cls, interpretedClass)
        }

        private fun interpretConstructor(secondaryConstructor: ESecondaryConstructor): ESvConstructor {
            val constructor = constructorMap[secondaryConstructor]
                ?: Messages.INTERNAL_ERROR.on(secondaryConstructor, "Constructor not found")
            val superTypeCallExpression = secondaryConstructor.superTypeCallExpression
            if (superTypeCallExpression != null) {
                val reference = superTypeCallExpression.reference
                if (reference is ESecondaryConstructor) {
                    val delegatedConstructor = constructorMap[reference]
                        ?: Messages.INTERNAL_ERROR.on(reference, "Constructor not found")
                    val superExpression = ESuperExpression(
                        secondaryConstructor.location,
                        reference.type.copy()
                    )
                    val callExpression = ECallExpression(
                        secondaryConstructor.location,
                        Core.Kt.C_Unit.toType(),
                        delegatedConstructor,
                        superExpression,
                        superTypeCallExpression.valueArguments,
                        ArrayList()
                    )
                    callExpression.parent = constructor.body
                    constructor.body.statements.add(0, callExpression)
                }
            }
            referenceUpdater.replace(secondaryConstructor, constructor)
            return constructor
        }
    }
}
