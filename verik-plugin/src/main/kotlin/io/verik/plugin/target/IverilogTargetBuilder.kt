/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.target

import io.verik.compiler.common.TextFile
import io.verik.plugin.config.IverilogTargetConfig

/**
 * Builder for Icarus Verilog targets
 */
object IverilogTargetBuilder {

    fun build(targetConfig: IverilogTargetConfig): TextFile {
        val path = targetConfig.buildDir.resolve("Makefile")
        val header = TargetFileHeaderBuilder.build(targetConfig, path)

        val builder = StringBuilder()
        builder.appendLine(header)
        builder.appendLine("IVERILOG = iverilog")
        builder.appendLine("VVP = vvp")
        builder.appendLine()

        builder.appendLine(".PHONY: clean all")
        builder.appendLine()

        builder.appendLine("all:")
        builder.append("\t\$(IVERILOG) -o out -g2012 -s ${targetConfig.top}")
        builder.appendLine(" -I ../compile ../compile/out.sv")
        builder.appendLine("\t\$(VVP) -l log out")
        builder.appendLine()

        builder.appendLine("clean:")
        builder.appendLine("\trm -rf out log")
        builder.appendLine()

        return TextFile(path, builder.toString())
    }
}
