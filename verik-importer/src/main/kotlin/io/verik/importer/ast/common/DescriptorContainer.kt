/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.common

import io.verik.importer.ast.element.descriptor.EDescriptor

/**
 * Interface for elements that contain [descriptors][EDescriptor].
 */
interface DescriptorContainer {

    fun replaceChild(oldDescriptor: EDescriptor, newDescriptor: EDescriptor): Boolean
}
