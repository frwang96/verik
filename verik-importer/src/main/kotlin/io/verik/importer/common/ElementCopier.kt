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
import io.verik.importer.ast.element.descriptor.ELiteralDescriptor
import io.verik.importer.ast.element.descriptor.EPackedDescriptor
import io.verik.importer.ast.element.descriptor.EQueueDescriptor
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.ast.element.descriptor.ESimpleDescriptor
import io.verik.importer.ast.element.descriptor.ETypeArgument
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation

object ElementCopier {

    fun <E : EElement> deepCopy(element: E, location: SourceLocation? = null): E {
        return copy(element, location)
    }

    private fun <E : EElement> copy(element: E, location: SourceLocation?): E {
        val copiedElement: EElement = when (element) {
            is ESimpleDescriptor -> copySimpleDescriptor(element, location)
            is ELiteralDescriptor -> copyLiteralDescriptor(element, location)
            is EBitDescriptor -> copyBitDescriptor(element, location)
            is EReferenceDescriptor -> copyReferenceDescriptor(element, location)
            is EPackedDescriptor -> copyPackedDescriptor(element, location)
            is EQueueDescriptor -> copyQueueDescriptor(element, location)
            is ETypeArgument -> copyTypeArgument(element, location)
            else -> Messages.INTERNAL_ERROR.on(element, "Unable to copy element: $element")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedElement as E
    }

    private fun copySimpleDescriptor(
        simpleDescriptor: ESimpleDescriptor,
        location: SourceLocation?
    ): ESimpleDescriptor {
        val type = simpleDescriptor.type.copy()
        return ESimpleDescriptor(
            location ?: simpleDescriptor.location,
            type
        )
    }

    private fun copyLiteralDescriptor(
        literalDescriptor: ELiteralDescriptor,
        location: SourceLocation?
    ): ELiteralDescriptor {
        val type = literalDescriptor.type.copy()
        return ELiteralDescriptor(
            location ?: literalDescriptor.location,
            type,
            literalDescriptor.value
        )
    }

    private fun copyBitDescriptor(
        bitDescriptor: EBitDescriptor,
        location: SourceLocation?,
    ): EBitDescriptor {
        val type = bitDescriptor.type.copy()
        val left = copy(bitDescriptor.left, location)
        val right = copy(bitDescriptor.right, location)
        return EBitDescriptor(
            location ?: bitDescriptor.location,
            type,
            left,
            right,
            bitDescriptor.isSigned
        )
    }

    private fun copyReferenceDescriptor(
        referenceDescriptor: EReferenceDescriptor,
        location: SourceLocation?,
    ): EReferenceDescriptor {
        val type = referenceDescriptor.type.copy()
        val typeArguments = referenceDescriptor.typeArguments.map { copy(it, location) }
        return EReferenceDescriptor(
            location ?: referenceDescriptor.location,
            type,
            referenceDescriptor.name,
            referenceDescriptor.reference,
            typeArguments
        )
    }

    private fun copyPackedDescriptor(
        packedDescriptor: EPackedDescriptor,
        location: SourceLocation?
    ): EPackedDescriptor {
        val type = packedDescriptor.type.copy()
        val descriptor = copy(packedDescriptor.descriptor, location)
        val left = copy(packedDescriptor.left, location)
        val right = copy(packedDescriptor.right, location)
        return EPackedDescriptor(
            location ?: packedDescriptor.location,
            type,
            descriptor,
            left,
            right
        )
    }

    private fun copyQueueDescriptor(
        queueDescriptor: EQueueDescriptor,
        location: SourceLocation?
    ): EQueueDescriptor {
        val type = queueDescriptor.type.copy()
        val descriptor = copy(queueDescriptor.descriptor, location)
        return EQueueDescriptor(
            location ?: queueDescriptor.location,
            type,
            descriptor
        )
    }

    private fun copyTypeArgument(
        typeArgument: ETypeArgument,
        location: SourceLocation?
    ): ETypeArgument {
        val descriptor = copy(typeArgument.descriptor, location)
        return ETypeArgument(
            location ?: typeArgument.location,
            typeArgument.name,
            descriptor
        )
    }
}
