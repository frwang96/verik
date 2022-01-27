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

package io.verik.importer.resolve

import io.verik.importer.ast.element.descriptor.EBitDescriptor
import io.verik.importer.ast.element.descriptor.EPackedDescriptor
import io.verik.importer.common.ExpressionEvaluator
import io.verik.importer.common.TreeVisitor
import io.verik.importer.core.Cardinal
import io.verik.importer.core.Core
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import kotlin.math.abs

object DescriptorResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(DescriptorResolverVisitor)
    }

    private object DescriptorResolverVisitor : TreeVisitor() {

        override fun visitBitDescriptor(bitDescriptor: EBitDescriptor) {
            super.visitBitDescriptor(bitDescriptor)
            val leftValue = ExpressionEvaluator.evaluate(bitDescriptor.left)
            val rightValue = ExpressionEvaluator.evaluate(bitDescriptor.right)
            if (leftValue != null && rightValue != null) {
                val width = abs(leftValue - rightValue) + 1
                bitDescriptor.type = if (bitDescriptor.isSigned) {
                    Core.C_Sbit.toType(Cardinal.of(width).toType())
                } else {
                    Core.C_Ubit.toType(Cardinal.of(width).toType())
                }
            }
        }

        override fun visitPackedDescriptor(packedDescriptor: EPackedDescriptor) {
            super.visitPackedDescriptor(packedDescriptor)
            val leftValue = ExpressionEvaluator.evaluate(packedDescriptor.left)
            val rightValue = ExpressionEvaluator.evaluate(packedDescriptor.right)
            if (leftValue != null && rightValue != null) {
                val width = abs(leftValue - rightValue) + 1
                packedDescriptor.type = Core.C_Packed.toType(
                    Cardinal.of(width).toType(),
                    packedDescriptor.descriptor.type.copy()
                )
            }
        }
    }
}
