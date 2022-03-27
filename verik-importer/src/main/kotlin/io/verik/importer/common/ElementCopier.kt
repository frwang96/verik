/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.common

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.descriptor.EArrayDimensionDescriptor
import io.verik.importer.ast.element.descriptor.EBitDescriptor
import io.verik.importer.ast.element.descriptor.EIndexDimensionDescriptor
import io.verik.importer.ast.element.descriptor.ELiteralDescriptor
import io.verik.importer.ast.element.descriptor.ENothingDescriptor
import io.verik.importer.ast.element.descriptor.ERangeDimensionDescriptor
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
            is ENothingDescriptor -> copyNothingDescriptor(element, location)
            is ESimpleDescriptor -> copySimpleDescriptor(element, location)
            is ELiteralDescriptor -> copyLiteralDescriptor(element, location)
            is EBitDescriptor -> copyBitDescriptor(element, location)
            is EReferenceDescriptor -> copyReferenceDescriptor(element, location)
            is ERangeDimensionDescriptor -> copyRangeDimensionDescriptor(element, location)
            is EArrayDimensionDescriptor -> copyArrayDimensionDescriptor(element, location)
            is EIndexDimensionDescriptor -> copyIndexDimensionDescriptor(element, location)
            is ETypeArgument -> copyTypeArgument(element, location)
            else -> Messages.INTERNAL_ERROR.on(element, "Unable to copy element: $element")
        }
        @Suppress("UNCHECKED_CAST")
        return copiedElement as E
    }

    private fun copyNothingDescriptor(
        nothingDescriptor: ENothingDescriptor,
        location: SourceLocation?
    ): ENothingDescriptor {
        return ENothingDescriptor(location ?: nothingDescriptor.location)
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

    private fun copyArrayDimensionDescriptor(
        arrayDimensionDescriptor: EArrayDimensionDescriptor,
        location: SourceLocation?
    ): EArrayDimensionDescriptor {
        val type = arrayDimensionDescriptor.type.copy()
        val descriptor = copy(arrayDimensionDescriptor.descriptor, location)
        return EArrayDimensionDescriptor(
            location ?: arrayDimensionDescriptor.location,
            type,
            descriptor,
            arrayDimensionDescriptor.isQueue
        )
    }

    private fun copyIndexDimensionDescriptor(
        mapDescriptor: EIndexDimensionDescriptor,
        location: SourceLocation?
    ): EIndexDimensionDescriptor {
        val type = mapDescriptor.type.copy()
        val descriptor = copy(mapDescriptor.descriptor, location)
        val index = copy(mapDescriptor.index, location)
        return EIndexDimensionDescriptor(
            location ?: mapDescriptor.location,
            type,
            descriptor,
            index
        )
    }

    private fun copyRangeDimensionDescriptor(
        packedDescriptor: ERangeDimensionDescriptor,
        location: SourceLocation?
    ): ERangeDimensionDescriptor {
        val type = packedDescriptor.type.copy()
        val descriptor = copy(packedDescriptor.descriptor, location)
        val left = copy(packedDescriptor.left, location)
        val right = copy(packedDescriptor.right, location)
        return ERangeDimensionDescriptor(
            location ?: packedDescriptor.location,
            type,
            descriptor,
            left,
            right,
            packedDescriptor.isPacked
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
