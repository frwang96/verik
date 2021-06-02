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

package io.verik.compiler.canonicalize

import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.descriptor.CardinalFunctionDescriptor
import io.verik.compiler.ast.descriptor.ClassDescriptor
import io.verik.compiler.ast.descriptor.TypeParameterDescriptor
import io.verik.compiler.ast.element.VkDeclaration
import io.verik.compiler.ast.element.VkElement
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector

object CanonicalTypeChecker {

    fun check(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(CanonicalTypeVisitor)
        }
    }

    private fun checkType(type: Type, element: VkElement) {
        type.arguments.forEach { checkType(it, element) }
        when (val classifierDescriptor = type.classifierDescriptor) {
            is ClassDescriptor ->
                if (classifierDescriptor == CoreClass.CARDINAL)
                    messageCollector.error("Could not deduce value of cardinal: $type", element)
            is CardinalFunctionDescriptor ->
                messageCollector.error("Could not deduce value of cardinal: $type", element)
            is TypeParameterDescriptor -> {
                if (classifierDescriptor.upperBound == CoreClass.CARDINAL)
                    messageCollector.error("Could not deduce value of cardinal: $type", element)
                else
                    messageCollector.error("Could not deduce value of type parameter: $type", element)
            }
        }
    }

    object CanonicalTypeVisitor: TreeVisitor() {

        override fun visitDeclaration(declaration: VkDeclaration) {
            checkType(declaration.type, declaration)
            super.visitDeclaration(declaration)
        }
    }
}