/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.declaration

import io.verik.compiler.target.common.PrimitiveTargetFunctionDeclaration
import io.verik.compiler.target.common.TargetPackage
import io.verik.compiler.target.common.TargetPropertyDeclaration
import io.verik.compiler.target.common.TargetScope

object TargetSystem : TargetScope(TargetPackage) {

    val F_cast = PrimitiveTargetFunctionDeclaration(parent, "\$cast")
    val F_display = PrimitiveTargetFunctionDeclaration(parent, "\$display")
    val F_write = PrimitiveTargetFunctionDeclaration(parent, "\$write")
    val F_sformatf = PrimitiveTargetFunctionDeclaration(parent, "\$sformatf")
    val F_clog2 = PrimitiveTargetFunctionDeclaration(parent, "\$clog2")
    val F_random = PrimitiveTargetFunctionDeclaration(parent, "\$random")
    val F_urandom = PrimitiveTargetFunctionDeclaration(parent, "\$urandom")
    val F_urandom_range = PrimitiveTargetFunctionDeclaration(parent, "\$urandom_range")
    val F_unsigned = PrimitiveTargetFunctionDeclaration(parent, "\$unsigned")
    val F_signed = PrimitiveTargetFunctionDeclaration(parent, "\$signed")
    val F_time = PrimitiveTargetFunctionDeclaration(parent, "\$time")
    val F_strobe = PrimitiveTargetFunctionDeclaration(parent, "\$strobe")
    val F_monitor = PrimitiveTargetFunctionDeclaration(parent, "\$monitor")
    val F_finish = PrimitiveTargetFunctionDeclaration(parent, "\$finish")
    val F_fatal = PrimitiveTargetFunctionDeclaration(parent, "\$fatal")
    val F_error = PrimitiveTargetFunctionDeclaration(parent, "\$error")
    val F_warning = PrimitiveTargetFunctionDeclaration(parent, "\$warning")
    val F_info = PrimitiveTargetFunctionDeclaration(parent, "\$info")
    val F_wait = PrimitiveTargetFunctionDeclaration(parent, "wait")
    val F_name = PrimitiveTargetFunctionDeclaration(parent, "name")
    val F_randomize = PrimitiveTargetFunctionDeclaration(parent, "randomize")
    val F_pre_randomize = PrimitiveTargetFunctionDeclaration(parent, "pre_randomize")
    val F_post_randomize = PrimitiveTargetFunctionDeclaration(parent, "post_randomize")
    val F_sample = PrimitiveTargetFunctionDeclaration(parent, "sample")
    val F_get_coverage = PrimitiveTargetFunctionDeclaration(parent, "get_coverage")

    val P_root = TargetPropertyDeclaration(parent, "\$root")
}
