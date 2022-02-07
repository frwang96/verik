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
import io.verik.importer.ast.element.descriptor.EBitDescriptor
import io.verik.importer.ast.element.descriptor.EDescriptorTypeArgument
import io.verik.importer.ast.element.descriptor.EPackedDescriptor
import io.verik.importer.ast.element.descriptor.EQueueDescriptor
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

        override fun visitBitDescriptor(bitDescriptor: EBitDescriptor) {
            super.visitBitDescriptor(bitDescriptor)
            val type = Core.T_ADD.toType(
                Core.T_SUB.toType(bitDescriptor.left.type, bitDescriptor.right.type),
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
            val typeArguments = referenceDescriptor.typeArguments.map {
                if (it is EDescriptorTypeArgument) {
                    it.descriptor.type.copy()
                } else Type.unresolved()
            }
            referenceDescriptor.type = referenceDescriptor.reference.toType(typeArguments)
        }

        override fun visitPackedDescriptor(packedDescriptor: EPackedDescriptor) {
            super.visitPackedDescriptor(packedDescriptor)
            val type = Core.T_ADD.toType(
                Core.T_SUB.toType(packedDescriptor.left.type, packedDescriptor.right.type),
                Cardinal.of(1).toType()
            )
            packedDescriptor.type = Core.C_Packed.toType(
                type,
                packedDescriptor.descriptor.type.copy()
            )
        }

        override fun visitQueueDescriptor(queueDescriptor: EQueueDescriptor) {
            super.visitQueueDescriptor(queueDescriptor)
            queueDescriptor.type = Core.C_ArrayList.toType(queueDescriptor.descriptor.type.copy())
        }
    }
}
