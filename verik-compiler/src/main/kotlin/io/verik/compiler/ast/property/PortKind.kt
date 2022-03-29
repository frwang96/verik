/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

enum class PortKind {
    INPUT,
    OUTPUT,
    INOUT,
    MODULE_INTERFACE,
    MODULE_PORT,
    CLOCKING_BLOCK
}
