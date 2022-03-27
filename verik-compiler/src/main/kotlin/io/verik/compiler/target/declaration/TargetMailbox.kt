/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.declaration

import io.verik.compiler.target.common.PrimitiveTargetFunctionDeclaration
import io.verik.compiler.target.common.Target
import io.verik.compiler.target.common.TargetScope

object TargetMailbox : TargetScope(Target.C_Mailbox) {

    val F_new = PrimitiveTargetFunctionDeclaration(parent, "new")
    val F_put = PrimitiveTargetFunctionDeclaration(parent, "put")
    val F_get = PrimitiveTargetFunctionDeclaration(parent, "get")
}
