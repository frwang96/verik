/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.common

import io.verik.compiler.ast.common.Declaration

/**
 * The base class of all target declarations. Target declarations are declarations defined by the SystemVerilog language
 * or are generated in the Verik SystemVerilog package. All references to core declarations must be transformed to
 * target declarations by the end of the compilation.
 */
interface TargetDeclaration : Declaration {

    val parent: TargetDeclaration?
}
