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

package io.verik.importer.transform.pre

import io.verik.importer.ast.common.Type
import io.verik.importer.ast.common.TypeParameterized
import io.verik.importer.ast.element.declaration.ETypeParameter
import io.verik.importer.ast.element.descriptor.EArrayDimensionDescriptor
import io.verik.importer.ast.element.descriptor.EBitDescriptor
import io.verik.importer.ast.element.descriptor.EIndexDimensionDescriptor
import io.verik.importer.ast.element.descriptor.ELiteralDescriptor
import io.verik.importer.ast.element.descriptor.ERangeDimensionDescriptor
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.core.Cardinal
import io.verik.importer.core.Core
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object DescriptorResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(DescriptorResolverVisitor)
    }

    private object DescriptorResolverVisitor : TreeVisitor() {

        override fun visitLiteralDescriptor(literalDescriptor: ELiteralDescriptor) {
            val value = literalDescriptor.value.toIntOrNull()
            if (value != null) {
                literalDescriptor.type = Cardinal.of(value).toType()
            } else {
                literalDescriptor.type = Core.C_Nothing.toType()
            }
        }

        override fun visitBitDescriptor(bitDescriptor: EBitDescriptor) {
            super.visitBitDescriptor(bitDescriptor)
            val type = Core.T_ADD.toType(
                Core.T_SUB.toType(bitDescriptor.left.type.copy(), bitDescriptor.right.type.copy()),
                Cardinal.of(1).toType()
            )
            bitDescriptor.type = if (bitDescriptor.isSigned) {
                Core.C_Sbit.toType(type)
            } else {
                Core.C_Ubit.toType(type)
            }
        }

        override fun visitReferenceDescriptor(referenceDescriptor: EReferenceDescriptor) {
            super.visitReferenceDescriptor(referenceDescriptor)
            val reference = referenceDescriptor.reference
            if (reference is TypeParameterized) {
                val typeArguments = referenceDescriptor.typeArguments.map {
                    it.descriptor.type.copy()
                }
                val defaultTypeArguments = reference.typeParameters.drop(typeArguments.size).map {
                    getTypeFromTypeParameter(it)
                }
                referenceDescriptor.type = referenceDescriptor.reference.toType(typeArguments + defaultTypeArguments)
            } else {
                referenceDescriptor.type = reference.toType()
            }
        }

        override fun visitArrayDimensionDescriptor(arrayDimensionDescriptor: EArrayDimensionDescriptor) {
            super.visitArrayDimensionDescriptor(arrayDimensionDescriptor)
            val baseType = if (arrayDimensionDescriptor.isQueue) Core.C_Queue else Core.C_DynamicArray
            arrayDimensionDescriptor.type = baseType.toType(arrayDimensionDescriptor.descriptor.type.copy())
        }

        override fun visitIndexDimensionDescriptor(indexDimensionDescriptor: EIndexDimensionDescriptor) {
            super.visitIndexDimensionDescriptor(indexDimensionDescriptor)
            indexDimensionDescriptor.type = Core.C_AssociativeArray.toType(
                indexDimensionDescriptor.index.type.copy(),
                indexDimensionDescriptor.descriptor.type.copy()
            )
        }

        override fun visitRangeDimensionDescriptor(rangeDimensionDescriptor: ERangeDimensionDescriptor) {
            super.visitRangeDimensionDescriptor(rangeDimensionDescriptor)
            val baseType = if (rangeDimensionDescriptor.isPacked) Core.C_Packed else Core.C_Unpacked
            val widthType = Core.T_ADD.toType(
                Core.T_SUB.toType(
                    rangeDimensionDescriptor.left.type.copy(),
                    rangeDimensionDescriptor.right.type.copy()
                ),
                Cardinal.of(1).toType()
            )
            rangeDimensionDescriptor.type = baseType.toType(
                widthType,
                rangeDimensionDescriptor.descriptor.type.copy()
            )
        }

        private fun getTypeFromTypeParameter(typeParameter: ETypeParameter): Type {
            return if (typeParameter.descriptor != null) {
                val reference = typeParameter.descriptor.type.reference
                if (reference is ETypeParameter) getTypeFromTypeParameter(reference)
                else typeParameter.descriptor.type.copy()
            } else Core.C_Nothing.toType()
        }
    }
}
