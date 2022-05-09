/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.target

import io.verik.compiler.common.TextFile
import io.verik.compiler.main.Platform
import io.verik.plugin.config.DsimTargetConfig

/**
 * Builder for Metrics dsim targets.
 */
object DsimTargetBuilder {

    fun build(targetConfig: DsimTargetConfig): TextFile {
        val path = targetConfig.buildDir.resolve("Makefile")
        val header = TargetFileHeaderBuilder.build(targetConfig, path)

        val builder = StringBuilder()
        builder.appendLine(header)
        builder.appendLine("DSIM = dsim")
        builder.appendLine()

        builder.append(".PHONY: clean all compile")
        targetConfig.simConfigs.forEach {
            builder.append(" ${it.name}")
        }
        builder.appendLine()
        builder.appendLine()

        builder.append("all: compile")
        targetConfig.simConfigs.forEach {
            builder.append(" ${it.name}")
        }
        builder.appendLine()
        builder.appendLine()

        builder.appendLine("compile:")
        builder.appendLine("\tmkdir -p log")
        builder.append("\t\$(DSIM) -genimage image -l log/compile.log")
        targetConfig.compileTops.forEach {
            builder.append(" -compile-top $it")
        }
        targetConfig.extraIncludeDirs.forEach {
            builder.append(" -incdir ${Platform.getStringFromPath(it)}")
        }
        builder.append(" -incdir ../compile")
        targetConfig.extraFiles.forEach {
            builder.append(" ${Platform.getStringFromPath(it)}")
        }
        builder.append(" ../compile/out.sv")
        builder.appendLine()
        builder.appendLine()

        targetConfig.simConfigs.forEach { simConfig ->
            builder.appendLine("${simConfig.name}:")
            builder.append("\t\$(DSIM) -image image -l log/${simConfig.name}.log -run-top ${simConfig.simTop}")
            targetConfig.dpiLibs.forEach {
                builder.append(" -sv_lib ${Platform.getStringFromPath(it)}")
            }
            simConfig.plusArgs.forEach { (key, value) ->
                builder.append(" +$key=$value")
            }
            builder.appendLine()
            builder.appendLine()
        }

        builder.appendLine("clean:")
        builder.appendLine("\trm -rf log dsim_work dsim.env metrics.db metrics_history.db")
        builder.appendLine()

        return TextFile(path, builder.toString())
    }
}
