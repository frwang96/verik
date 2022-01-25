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

package io.verik.importer.common

import io.verik.importer.ast.sv.element.common.SvElement
import io.verik.importer.ast.sv.element.descriptor.SvBitDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvPackedDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvReferenceDescriptor
import io.verik.importer.ast.sv.element.descriptor.SvSimpleDescriptor
import io.verik.importer.ast.sv.element.expression.SvLiteralExpression
import io.verik.importer.ast.sv.element.expression.SvReferenceExpression
import io.verik.importer.message.Messages

object ElementCopier {

    fun <E : SvElement> deepCopy(element: E): E {
        return copy(element)
    }

    private fun <E : SvElement> copy(element: E): E {
        val copiedElement: SvElement = when (element) {
            is SvSimpleDescriptor -> copySimpleDescriptor(element)
            is SvBitDescriptor -> copyBitDescriptor(element)
            is SvPackedDescriptor -> copyPackedDescriptor(element)
            is SvReferenceDescriptor -> copyReferenceDescriptor(element)
            is SvLiteralExpression -> copyLiteralExpression(element)
            is SvReferenceExpression -> copyReferenceExpression(element)
            else -> Messages.INTERNAL_ERROR.on(element, "Unable to copy element: $element")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedElement as E
    }

    private fun copySimpleDescriptor(simpleDescriptor: SvSimpleDescriptor): SvSimpleDescriptor {
        return SvSimpleDescriptor(
            simpleDescriptor.location,
            simpleDescriptor.type
        )
    }

    private fun copyBitDescriptor(bitDescriptor: SvBitDescriptor): SvBitDescriptor {
        val left = copy(bitDescriptor.left)
        val right = copy(bitDescriptor.right)
        return SvBitDescriptor(
            bitDescriptor.location,
            bitDescriptor.type,
            left,
            right,
            bitDescriptor.isSigned
        )
    }

    private fun copyPackedDescriptor(packedDescriptor: SvPackedDescriptor): SvPackedDescriptor {
        val descriptor = copy(packedDescriptor.descriptor)
        val left = copy(packedDescriptor.left)
        val right = copy(packedDescriptor.right)
        return SvPackedDescriptor(
            packedDescriptor.location,
            packedDescriptor.type,
            descriptor,
            left,
            right
        )
    }

    private fun copyReferenceDescriptor(referenceDescriptor: SvReferenceDescriptor): SvReferenceDescriptor {
        return SvReferenceDescriptor(
            referenceDescriptor.location,
            referenceDescriptor.type,
            referenceDescriptor.name
        )
    }

    private fun copyLiteralExpression(literalExpression: SvLiteralExpression): SvLiteralExpression {
        return SvLiteralExpression(
            literalExpression.location,
            literalExpression.value
        )
    }

    private fun copyReferenceExpression(referenceExpression: SvReferenceExpression): SvElement {
        return SvReferenceExpression(
            referenceExpression.location,
            referenceExpression.name,
            referenceExpression.reference
        )
    }
}
