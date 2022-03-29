/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope

/**
 * Core cardinal functions from the Verik core package.
 */
object CoreVkCardinal : CoreScope(CorePackage.VK) {

    val T_TRUE = CoreCardinalFunctionDeclaration(parent, "TRUE")
    val T_FALSE = CoreCardinalFunctionDeclaration(parent, "FALSE")
    val T_NOT = CoreCardinalFunctionDeclaration(parent, "NOT")
    val T_AND = CoreCardinalFunctionDeclaration(parent, "AND")
    val T_OR = CoreCardinalFunctionDeclaration(parent, "OR")
    val T_IF = CoreCardinalFunctionDeclaration(parent, "IF")
    val T_ADD = CoreCardinalFunctionDeclaration(parent, "ADD")
    val T_SUB = CoreCardinalFunctionDeclaration(parent, "SUB")
    val T_MUL = CoreCardinalFunctionDeclaration(parent, "MUL")
    val T_DIV = CoreCardinalFunctionDeclaration(parent, "DIV")
    val T_MAX = CoreCardinalFunctionDeclaration(parent, "MAX")
    val T_MIN = CoreCardinalFunctionDeclaration(parent, "MIN")
    val T_OF = CoreCardinalFunctionDeclaration(parent, "OF")
    val T_INC = CoreCardinalFunctionDeclaration(parent, "INC")
    val T_DEC = CoreCardinalFunctionDeclaration(parent, "DEC")
    val T_LOG = CoreCardinalFunctionDeclaration(parent, "LOG")
    val T_WIDTH = CoreCardinalFunctionDeclaration(parent, "WIDTH")
    val T_EXP = CoreCardinalFunctionDeclaration(parent, "EXP")
}
