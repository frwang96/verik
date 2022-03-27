/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.declaration

import io.verik.compiler.target.common.PrimitiveTargetFunctionDeclaration
import io.verik.compiler.target.common.Target
import io.verik.compiler.target.common.TargetScope

object TargetQueue : TargetScope(Target.C_Queue) {

    val F_push_back = PrimitiveTargetFunctionDeclaration(parent, "push_back")
    val F_size = PrimitiveTargetFunctionDeclaration(parent, "size")
}
