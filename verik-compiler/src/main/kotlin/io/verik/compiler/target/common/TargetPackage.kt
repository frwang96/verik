/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.target.common

import java.nio.file.Path
import java.nio.file.Paths

/**
 * The target declaration that represents the Verik SystemVerilog package.
 */
object TargetPackage : TargetDeclaration {

    override val parent: TargetDeclaration? = null

    override var name = "verik_pkg"

    val path: Path = Paths.get("verik", "Pkg.sv")
}
