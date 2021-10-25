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

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReturnStatement
import io.verik.compiler.ast.element.common.ESuperExpression
import io.verik.compiler.ast.element.common.ETemporaryProperty
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.kt.EKtConstructor
import io.verik.compiler.ast.element.kt.EKtReferenceExpression
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvBlockExpression
import io.verik.compiler.ast.element.sv.ESvCallExpression
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvReferenceExpression
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.ast.property.FunctionQualifierType
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object BasicClassInterpreterStage : ProjectStage() {

    override val checkNormalization = true

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
                val valueParameter = ESvValueParameter(it.location, it.name, it.type)
                valueParameters.add(valueParameter)
                referenceUpdater.update(it, valueParameter)
            }
            val initializer = ESvFunction(
                constructor.location,
                "_${'$'}init",
                Core.Kt.C_Unit.toType(),
                constructor.body,
                false,
                FunctionQualifierType.REGULAR,
                ArrayList(valueParameters)
            )
            initializerMap[constructor] = initializer
        }
    }

    private class ClassInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater,
        private val initializerMap: HashMap<EKtConstructor, ESvFunction>
    ) : TreeVisitor() {

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            val declarations = ArrayList<EDeclaration>()
            basicClass.declarations.forEach {
                if (it is EKtConstructor) {
                    val interpretedConstructor = interpretConstructor(basicClass, it)
                    if (interpretedConstructor != null) {
                        val instantiator = interpretedConstructor.instantiator
                        if (instantiator != null) {
                            instantiator.parent = basicClass
                            declarations.add(instantiator)
                        }
                        interpretedConstructor.initializer.parent = basicClass
                        declarations.add(interpretedConstructor.initializer)
                    }
                } else {
                    declarations.add(it)
                }
            }
            referenceUpdater.replace(
                basicClass,
                ESvBasicClass(
                    basicClass.location,
                    basicClass.name,
                    basicClass.superType,
                    declarations,
                    basicClass.isAbstract
                )
            )
        }

        private fun interpretConstructor(
            basicClass: EKtBasicClass,
            constructor: EKtConstructor
        ): InterpretedConstructor? {
            val initializer = initializerMap[constructor]
            if (initializer == null) {
                Messages.INTERNAL_ERROR.on(constructor, "Initializer not found")
                return null
            }
            val superTypeCallEntry = constructor.superTypeCallEntry
            if (superTypeCallEntry != null) {
                val reference = superTypeCallEntry.reference
                if (reference is EKtConstructor) {
                    val delegatedInitializer = initializerMap[reference]
                    if (delegatedInitializer == null) {
                        Messages.INTERNAL_ERROR.on(reference, "Initializer not found")
                        return null
                    }
                    val superExpression = ESuperExpression(
                        constructor.location,
                        reference.type.copy()
                    )
                    val callExpression = EKtCallExpression(
                        constructor.location,
                        Core.Kt.C_Unit.toType(),
                        delegatedInitializer,
                        superExpression,
                        superTypeCallEntry.valueArguments,
                        ArrayList()
                    )
                    val body = initializer.getBodyNotNull().cast<EKtBlockExpression>()
                    if (body != null) {
                        callExpression.parent = body
                        body.statements.add(0, callExpression)
                    }
                }
            }
            val instantiator = interpretInstantiator(basicClass, constructor, initializer)
            return InterpretedConstructor(instantiator, initializer)
        }

        private fun interpretInstantiator(
            basicClass: EKtBasicClass,
            constructor: EKtConstructor,
            initializer: ESvFunction
        ): ESvFunction? {
            if (basicClass.isAbstract)
                return null
            val temporaryProperty = ETemporaryProperty(
                constructor.location,
                constructor.type.copy(),
                ESvCallExpression(
                    constructor.location,
                    constructor.type.copy(),
                    Core.Sv.F_new,
                    null,
                    arrayListOf(),
                    false
                )
            )
            val valueParameters = constructor.valueParameters.map {
                ESvValueParameter(it.location, it.name, it.type.copy())
            }

            val propertyStatement = EPropertyStatement(constructor.location, temporaryProperty)
            val valueArguments = valueParameters.map {
                EKtReferenceExpression(it.location, it.type.copy(), it, null)
            }
            val callExpression = EKtCallExpression(
                constructor.location,
                Core.Kt.C_Unit.toType(),
                initializer,
                EKtReferenceExpression(constructor.location, constructor.type.copy(), temporaryProperty, null),
                ArrayList(valueArguments),
                arrayListOf()
            )
            val returnStatement = EReturnStatement(
                constructor.location,
                Core.Kt.C_Nothing.toType(),
                ESvReferenceExpression(
                    constructor.location,
                    constructor.type.copy(),
                    temporaryProperty,
                    null,
                    false
                )
            )
            val statements = arrayListOf(propertyStatement, callExpression, returnStatement)

            val instantiator = ESvFunction(
                constructor.location,
                "_${'$'}new",
                constructor.type,
                ESvBlockExpression(constructor.location, statements, false, null),
                true,
                FunctionQualifierType.REGULAR,
                ArrayList(valueParameters)
            )
            referenceUpdater.replace(constructor, instantiator)
            return instantiator
        }
    }

    data class InterpretedConstructor(
        val instantiator: ESvFunction?,
        val initializer: ESvFunction
    )
}
