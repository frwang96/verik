/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.target.common.Target

object CoreVkCoverGroup : CoreScope(Core.Vk.C_CoverGroup) {

    val F_sample = BasicCoreFunctionDeclaration(parent, "sample", "fun sample()", Target.F_sample)

    val F_getCoverage = BasicCoreFunctionDeclaration(parent, "getCoverage", "fun getCoverage()", Target.F_get_coverage)
}
