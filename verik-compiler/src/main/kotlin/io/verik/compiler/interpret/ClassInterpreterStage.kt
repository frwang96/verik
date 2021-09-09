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

import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.EPort
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.property.PortType
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Annotations
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object ClassInterpreterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val classInterpreterVisitor = ClassInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(classInterpreterVisitor)
        referenceUpdater.flush()
    }

    class ClassInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            if (interpretModule(basicClass, referenceUpdater))
                return
            if (interpretStruct(basicClass, referenceUpdater))
                return
            interpretBasicClass(basicClass, referenceUpdater)
        }
    }

    private fun interpretModule(basicClass: EKtBasicClass, referenceUpdater: ReferenceUpdater): Boolean {
        if (!basicClass.toType().isSubtype(Core.Vk.C_MODULE.toType()))
            return false
        val ports = basicClass.primaryConstructor
            ?.valueParameters
            ?.mapNotNull { interpretPort(it) }
            ?: listOf()
        val module = EModule(
            basicClass.location,
            basicClass.name,
            basicClass.supertype,
            basicClass.typeParameters,
            basicClass.members,
            ports
        )
        referenceUpdater.replace(basicClass, module)
        basicClass.primaryConstructor?.let { referenceUpdater.update(it, module) }
        return true
    }

    private fun interpretPort(valueParameter: EKtValueParameter): EPort? {
        return when {
            valueParameter.hasAnnotation(Annotations.IN) ->
                EPort(valueParameter.location, valueParameter.name, valueParameter.type, PortType.INPUT)
            valueParameter.hasAnnotation(Annotations.OUT) ->
                EPort(valueParameter.location, valueParameter.name, valueParameter.type, PortType.OUTPUT)
            else -> {
                Messages.PORT_NO_DIRECTIONALITY.on(valueParameter, valueParameter.name)
                null
            }
        }
    }

    private fun interpretStruct(basicClass: EKtBasicClass, referenceUpdater: ReferenceUpdater): Boolean {
        if (!basicClass.toType().isSubtype(Core.Vk.C_STRUCT.toType()))
            return false
        val properties = basicClass.primaryConstructor!!
            .valueParameters
            .map { interpretStructProperty(it, referenceUpdater) }
        val struct = EStruct(basicClass.location, basicClass.name, properties)
        referenceUpdater.replace(basicClass, struct)
        return true
    }

    private fun interpretStructProperty(
        valueParameter: EKtValueParameter,
        referenceUpdater: ReferenceUpdater
    ): ESvProperty {
        val property = ESvProperty(
            valueParameter.location,
            valueParameter.name,
            valueParameter.type,
            null
        )
        referenceUpdater.update(valueParameter, property)
        return property
    }

    private fun interpretBasicClass(basicClass: EKtBasicClass, referenceUpdater: ReferenceUpdater) {
        referenceUpdater.replace(
            basicClass,
            ESvBasicClass(
                basicClass.location,
                basicClass.name,
                basicClass.supertype,
                basicClass.typeParameters,
                basicClass.members
            )
        )
    }
}
