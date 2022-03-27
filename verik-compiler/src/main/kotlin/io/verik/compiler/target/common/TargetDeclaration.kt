/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.common

import io.verik.compiler.ast.common.Declaration

interface TargetDeclaration : Declaration {

    val parent: TargetDeclaration?
}
