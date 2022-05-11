/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.target

import io.verik.compiler.common.TextFile
import io.verik.plugin.config.XrunTargetConfig

/**
 * Builder for Cadence xrun targets.
 */
object XrunTargetBuilder {

    fun build(targetConfig: XrunTargetConfig): TextFile {
        val path = targetConfig.buildDir.resolve("Makefile")
        val header = TargetFileHeaderBuilder.build(targetConfig, path)

        val builder = StringBuilder()
        builder.appendLine(header)
        builder.appendLine("XRUN = xrun")
        builder.appendLine()

        builder.appendLine(".PHONY: clean all")
        builder.appendLine()

        builder.appendLine("all:")
        builder.appendLine("\t\$(XRUN) -top ${targetConfig.top} -incdir ../compile ../compile/out.sv")
        builder.appendLine()

        builder.appendLine("clean:")
        builder.appendLine("\trm -rf xcelium.d xrun.history xrun.log")

        return TextFile(path, builder.toString())
    }
}
