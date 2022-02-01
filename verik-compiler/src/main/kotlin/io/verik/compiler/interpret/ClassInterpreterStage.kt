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
import io.verik.compiler.ast.element.declaration.kt.EKtConstructor
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

        val initializerMap = HashMap<EKtConstructor, ESvFunction>()

        override fun visitKtConstructor(constructor: EKtConstructor) {
            super.visitKtConstructor(constructor)
            val valueParameters = ArrayList<ESvValueParameter>()
            constructor.valueParameters.forEach {
                val valueParameter = ESvValueParameter(
                    it.location,
                    it.name,
                    it.type,
                    it.annotationEntries,
                    true
                )
                valueParameters.add(valueParameter)
                referenceUpdater.update(it, valueParameter)
            }
            val initializer = ESvFunction(
                location = constructor.location,
                name = "${constructor.name}_init",
                type = Core.Kt.C_Unit.toType(),
                annotationEntries = listOf(),
                documentationLines = null,
                body = constructor.body,
                valueParameters = ArrayList(valueParameters),
                qualifierType = FunctionQualifierType.REGULAR,
                isConstructor = false
            )
            initializerMap[constructor] = initializer
        }
    }

    private class ClassInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater,
        private val initializerMap: HashMap<EKtConstructor, ESvFunction>
    ) : TreeVisitor() {

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            val declarations = ArrayList<EDeclaration>()
            `class`.declarations.forEach {
                if (it is EKtConstructor) {
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
            constructor: EKtConstructor
        ): Pair<ESvFunction?, ESvFunction> {
            val initializer = initializerMap[constructor]
                ?: Messages.INTERNAL_ERROR.on(constructor, "Initializer not found")
            val superTypeCallExpression = constructor.superTypeCallExpression
            if (superTypeCallExpression != null) {
                val reference = superTypeCallExpression.reference
                if (reference is EKtConstructor) {
                    val delegatedInitializer = initializerMap[reference]
                        ?: Messages.INTERNAL_ERROR.on(reference, "Initializer not found")
                    val superExpression = ESuperExpression(
                        constructor.location,
                        reference.type.copy()
                    )
                    val callExpression = ECallExpression(
                        constructor.location,
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
            val interpretedConstructor = interpretConstructor(`class`, constructor, initializer)
            return Pair(interpretedConstructor, initializer)
        }

        private fun interpretConstructor(
            `class`: EKtClass,
            constructor: EKtConstructor,
            initializer: ESvFunction
        ): ESvFunction? {
            if (`class`.isAbstract)
                return null

            val property = EProperty.temporary(
                constructor.location,
                constructor.type.copy(),
                ECallExpression(
                    location = constructor.location,
                    type = constructor.type.copy(),
                    reference = Target.F_new,
                    receiver = null,
                    valueArguments = ArrayList(),
                    typeArguments = ArrayList()
                ),
                false
            )
            val valueParameters = constructor.valueParameters.map {
                ESvValueParameter(
                    it.location,
                    it.name,
                    it.type.copy(),
                    it.annotationEntries,
                    true
                )
            }
            val propertyStatement = EPropertyStatement(constructor.location, property)
            val valueArguments = valueParameters.map {
                EReferenceExpression(it.location, it.type.copy(), it, null)
            }
            val callExpression = ECallExpression(
                location = constructor.location,
                type = Core.Kt.C_Unit.toType(),
                reference = initializer,
                receiver = EReferenceExpression(constructor.location, constructor.type.copy(), property, null),
                valueArguments = ArrayList(valueArguments),
                typeArguments = ArrayList()
            )
            val returnStatement = EReturnStatement(
                constructor.location,
                Core.Kt.C_Nothing.toType(),
                EReferenceExpression(
                    constructor.location,
                    constructor.type.copy(),
                    property,
                    null
                )
            )
            val statements = arrayListOf(propertyStatement, callExpression, returnStatement)

            val interpretedConstructor = ESvFunction(
                location = constructor.location,
                name = "${constructor.name}_new",
                type = constructor.type,
                annotationEntries = constructor.annotationEntries,
                documentationLines = constructor.documentationLines,
                body = EBlockExpression(
                    constructor.location,
                    constructor.location,
                    Core.Kt.C_Unit.toType(),
                    statements
                ),
                valueParameters = ArrayList(valueParameters),
                qualifierType = FunctionQualifierType.REGULAR,
                isConstructor = true
            )
            referenceUpdater.replace(constructor, interpretedConstructor)
            return interpretedConstructor
        }
    }
}
