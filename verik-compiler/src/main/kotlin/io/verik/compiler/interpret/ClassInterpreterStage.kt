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
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvFunction
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.common.EReturnStatement
import io.verik.compiler.ast.element.expression.common.ESuperExpression
import io.verik.compiler.ast.property.FunctionQualifierType
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import io.verik.compiler.target.common.Target

object ClassInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val initializerIndexerVisitor = InitializerIndexerVisitor(referenceUpdater)
        projectContext.project.accept(initializerIndexerVisitor)
        val classInterpreterVisitor = ClassInterpreterVisitor(
            referenceUpdater,
            initializerIndexerVisitor.initializerMap
        )
        projectContext.project.accept(classInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class InitializerIndexerVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        val initializerMap = HashMap<ESecondaryConstructor, ESvFunction>()

        override fun visitSecondaryConstructor(secondaryConstructor: ESecondaryConstructor) {
            super.visitSecondaryConstructor(secondaryConstructor)
            val valueParameters = ArrayList<ESvValueParameter>()
            secondaryConstructor.valueParameters.forEach {
                val valueParameter = interpretValueParameter(it)
                valueParameters.add(valueParameter)
                referenceUpdater.update(it, valueParameter)
            }
            val initializer = ESvFunction(
                location = secondaryConstructor.location,
                name = "__init",
                type = Core.Kt.C_Unit.toType(),
                annotationEntries = listOf(),
                documentationLines = null,
                body = secondaryConstructor.body,
                valueParameters = ArrayList(valueParameters),
                qualifierType = FunctionQualifierType.REGULAR,
                isConstructor = false
            )
            initializerMap[secondaryConstructor] = initializer
        }
    }

    private class ClassInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater,
        private val initializerMap: HashMap<ESecondaryConstructor, ESvFunction>
    ) : TreeVisitor() {

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            val declarations = ArrayList<EDeclaration>()
            `class`.declarations.forEach {
                if (it is ESecondaryConstructor) {
                    val (constructor, initializer) = interpretConstructorAndInitializer(`class`, it)
                    if (constructor != null) {
                        constructor.parent = `class`
                        declarations.add(constructor)
                    }
                    initializer.parent = `class`
                    declarations.add(initializer)
                } else {
                    declarations.add(it)
                }
            }
            val interpretedClass = ESvClass(
                `class`.location,
                `class`.bodyStartLocation,
                `class`.bodyEndLocation,
                `class`.name,
                `class`.type,
                `class`.annotationEntries,
                `class`.documentationLines,
                `class`.superType,
                declarations,
                `class`.isAbstract,
                `class`.isObject
            )
            referenceUpdater.replace(`class`, interpretedClass)
        }

        private fun interpretConstructorAndInitializer(
            `class`: EKtClass,
            secondaryConstructor: ESecondaryConstructor
        ): Pair<ESvFunction?, ESvFunction> {
            val initializer = initializerMap[secondaryConstructor]
                ?: Messages.INTERNAL_ERROR.on(secondaryConstructor, "Initializer not found")
            val superTypeCallExpression = secondaryConstructor.superTypeCallExpression
            if (superTypeCallExpression != null) {
                val reference = superTypeCallExpression.reference
                if (reference is ESecondaryConstructor) {
                    val delegatedInitializer = initializerMap[reference]
                        ?: Messages.INTERNAL_ERROR.on(reference, "Initializer not found")
                    val superExpression = ESuperExpression(
                        secondaryConstructor.location,
                        reference.type.copy()
                    )
                    val callExpression = ECallExpression(
                        secondaryConstructor.location,
                        Core.Kt.C_Unit.toType(),
                        delegatedInitializer,
                        superExpression,
                        superTypeCallExpression.valueArguments,
                        ArrayList()
                    )
                    val body = initializer.body.cast<EBlockExpression>()
                    callExpression.parent = body
                    body.statements.add(0, callExpression)
                }
            }
            val constructor = interpretConstructor(`class`, secondaryConstructor, initializer)
            return Pair(constructor, initializer)
        }

        private fun interpretConstructor(
            `class`: EKtClass,
            secondaryConstructor: ESecondaryConstructor,
            initializer: ESvFunction
        ): ESvFunction? {
            if (`class`.isAbstract)
                return null

            // TODO better handling of imported constructors
            val name = if (`class`.isImported()) "new" else "__new"
            val property = EProperty.temporary(
                secondaryConstructor.location,
                secondaryConstructor.type.copy(),
                ECallExpression(
                    location = secondaryConstructor.location,
                    type = secondaryConstructor.type.copy(),
                    reference = Target.F_new,
                    receiver = null,
                    valueArguments = ArrayList(),
                    typeArguments = ArrayList()
                ),
                false
            )
            val valueParameters = secondaryConstructor.valueParameters.map { interpretValueParameter(it) }
            val propertyStatement = EPropertyStatement(secondaryConstructor.location, property)
            val referenceExpression = EReferenceExpression(
                secondaryConstructor.location,
                secondaryConstructor.type.copy(),
                property,
                null
            )
            val valueArguments = valueParameters.map {
                EReferenceExpression(it.location, it.type.copy(), it, null)
            }
            val callExpression = ECallExpression(
                location = secondaryConstructor.location,
                type = Core.Kt.C_Unit.toType(),
                reference = initializer,
                receiver = referenceExpression,
                valueArguments = ArrayList(valueArguments),
                typeArguments = ArrayList()
            )
            val returnStatement = EReturnStatement(
                secondaryConstructor.location,
                Core.Kt.C_Nothing.toType(),
                EReferenceExpression(
                    secondaryConstructor.location,
                    secondaryConstructor.type.copy(),
                    property,
                    null
                )
            )
            val statements = arrayListOf(propertyStatement, callExpression, returnStatement)

            val constructor = ESvFunction(
                location = secondaryConstructor.location,
                name = name,
                type = secondaryConstructor.type,
                annotationEntries = secondaryConstructor.annotationEntries,
                documentationLines = secondaryConstructor.documentationLines,
                body = EBlockExpression(
                    secondaryConstructor.location,
                    secondaryConstructor.location,
                    Core.Kt.C_Unit.toType(),
                    statements
                ),
                valueParameters = ArrayList(valueParameters),
                qualifierType = FunctionQualifierType.REGULAR,
                isConstructor = true
            )
            referenceUpdater.replace(secondaryConstructor, constructor)
            return constructor
        }
    }

    private fun interpretValueParameter(valueParameter: EKtValueParameter): ESvValueParameter {
        return ESvValueParameter(
            location = valueParameter.location,
            name = valueParameter.name,
            type = valueParameter.type.copy(),
            annotationEntries = valueParameter.annotationEntries,
            expression = valueParameter.expression,
            isInput = true
        )
    }
}
