/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.core.declaration.vk

import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.target.common.Target

/**
 * Core declarations from CoverGroup.
 */
object CoreVkCoverGroup : CoreScope(Core.Vk.C_CoverGroup) {

    val F_sample = BasicCoreFunctionDeclaration(parent, "sample", "fun sample()", Target.F_sample)

    val F_coverage = BasicCoreFunctionDeclaration(parent, "coverage", "fun coverage()", Target.F_get_coverage)
}
