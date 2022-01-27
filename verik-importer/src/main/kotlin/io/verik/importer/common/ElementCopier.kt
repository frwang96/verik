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

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.descriptor.EBitDescriptor
import io.verik.importer.ast.element.descriptor.EPackedDescriptor
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.ast.element.expression.ELiteralExpression
import io.verik.importer.ast.element.expression.EReferenceExpression
import io.verik.importer.message.Messages

object ElementCopier {

    fun <E : EElement> deepCopy(element: E): E {
        return copy(element)
    }

    private fun <E : EElement> copy(element: E): E {
        val copiedElement: EElement = when (element) {
            is ESimpleDescriptor -> copySimpleDescriptor(element)
            is EBitDescriptor -> copyBitDescriptor(element)
            is EPackedDescriptor -> copyPackedDescriptor(element)
            is EReferenceDescriptor -> copyReferenceDescriptor(element)
            is ELiteralExpression -> copyLiteralExpression(element)
            is EReferenceExpression -> copyReferenceExpression(element)
            else -> Messages.INTERNAL_ERROR.on(element, "Unable to copy element: $element")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedElement as E
    }

    private fun copySimpleDescriptor(simpleDescriptor: ESimpleDescriptor): ESimpleDescriptor {
        val type = simpleDescriptor.type.copy()
        return ESimpleDescriptor(
            simpleDescriptor.location,
            type
        )
    }

    private fun copyBitDescriptor(bitDescriptor: EBitDescriptor): EBitDescriptor {
        val type = bitDescriptor.type.copy()
        val left = copy(bitDescriptor.left)
        val right = copy(bitDescriptor.right)
        return EBitDescriptor(
            bitDescriptor.location,
            type,
            left,
            right,
            bitDescriptor.isSigned
        )
    }

    private fun copyPackedDescriptor(packedDescriptor: EPackedDescriptor): EPackedDescriptor {
        val type = packedDescriptor.type.copy()
        val descriptor = copy(packedDescriptor.descriptor)
        val left = copy(packedDescriptor.left)
        val right = copy(packedDescriptor.right)
        return EPackedDescriptor(
            packedDescriptor.location,
            type,
            descriptor,
            left,
            right
        )
    }

    private fun copyReferenceDescriptor(referenceDescriptor: EReferenceDescriptor): EReferenceDescriptor {
        val type = referenceDescriptor.type.copy()
        return EReferenceDescriptor(
            referenceDescriptor.location,
            type,
            referenceDescriptor.name
        )
    }

    private fun copyLiteralExpression(literalExpression: ELiteralExpression): ELiteralExpression {
        return ELiteralExpression(
            literalExpression.location,
            literalExpression.value
        )
    }

    private fun copyReferenceExpression(referenceExpression: EReferenceExpression): EElement {
        return EReferenceExpression(
            referenceExpression.location,
            referenceExpression.name,
            referenceExpression.reference
        )
    }
}
