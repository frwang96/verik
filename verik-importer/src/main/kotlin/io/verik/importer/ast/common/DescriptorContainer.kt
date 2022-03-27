/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.common

import io.verik.importer.ast.element.descriptor.EDescriptor

interface DescriptorContainer {

    fun replaceChild(oldDescriptor: EDescriptor, newDescriptor: EDescriptor): Boolean
}
