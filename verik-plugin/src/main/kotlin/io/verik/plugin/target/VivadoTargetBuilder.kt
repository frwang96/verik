/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.target

import io.verik.compiler.common.TextFile
import io.verik.compiler.main.Platform
import io.verik.plugin.config.VivadoTargetConfig
import kotlin.io.path.name

/**
 * Builder for Vivado targets
 */
object VivadoTargetBuilder {

    private val reports = listOf(
        "timing",
        "clocks",
        "clock_interaction",
        "cdc",
        "timing_summary",
        "datasheet",
        "design_analysis",
        "power",
        "pulse_width",
        "utilization",
        "clock_util",
        "io",
        "control_sets",
        "drc",
        "methodology",
        "ssn",
        "param",
        "config_timing",
        "ip_status",
        "incremental_reuse"
    )

    fun build(targetConfig: VivadoTargetConfig): List<TextFile> {
        val textFiles = ArrayList<TextFile>()
        textFiles.add(buildMakeFile(targetConfig))
        if (targetConfig.simTop.isNotBlank()) {
            textFiles.add(buildSimTclFile(targetConfig))
        }
        if (targetConfig.synthTop.isNotBlank()) {
            textFiles.add(buildSynthTclFile(targetConfig))
            val constraintsFile = Platform.readTextFile(targetConfig.constraintsFile!!)
            val constraintsFileCopy = TextFile(
                targetConfig.buildDir.resolve("constraints.xdc"),
                constraintsFile.content
            )
            textFiles.add(constraintsFileCopy)
        }
        targetConfig.ipConfigFiles.forEach {
            val ipConfigFile = Platform.readTextFile(it)
            val ipConfigFileCopy = TextFile(
                targetConfig.buildDir.resolve("ip/${it.name}"),
                ipConfigFile.content
            )
            textFiles.add(ipConfigFileCopy)
        }
        return textFiles
    }

    private fun buildMakeFile(targetConfig: VivadoTargetConfig): TextFile {
        val path = targetConfig.buildDir.resolve("Makefile")
        val header = TargetFileHeaderBuilder.build(targetConfig, path)

        val builder = StringBuilder()
        builder.appendLine(header)
        builder.appendLine("VIVADO = vivado")
        builder.appendLine()

        builder.append(".PHONY: clean all")
        if (targetConfig.simTop.isNotBlank()) builder.append(" sim")
        if (targetConfig.synthTop.isNotBlank()) builder.append(" synth")
        builder.appendLine()
        builder.appendLine()

        builder.append("all:")
        if (targetConfig.simTop.isNotBlank()) builder.append(" sim")
        if (targetConfig.synthTop.isNotBlank()) builder.append(" synth")
        builder.appendLine()
        builder.appendLine()

        if (targetConfig.simTop.isNotBlank()) {
            builder.appendLine("sim:")
            builder.appendLine("\trm -rf sim")
            builder.appendLine("\t$(VIVADO) -mode batch -source sim.tcl")
            builder.appendLine("\tmv *.log sim")
            builder.appendLine("\tmv *.jou sim")
            builder.appendLine()
        }

        if (targetConfig.synthTop.isNotBlank()) {
            builder.appendLine("synth:")
            builder.appendLine("\trm -rf synth")
            builder.appendLine("\t$(VIVADO) -mode batch -source synth.tcl")
            builder.appendLine("\tmv *.log synth")
            builder.appendLine("\tmv *.jou synth")
            builder.appendLine()
        }

        builder.appendLine("clean:")
        builder.append("\trm -rf .Xil *.log *.jou")
        if (targetConfig.simTop.isNotBlank()) builder.append(" sim")
        if (targetConfig.synthTop.isNotBlank()) builder.append(" synth")
        builder.appendLine()

        return TextFile(path, builder.toString())
    }

    private fun buildSimTclFile(targetConfig: VivadoTargetConfig): TextFile {
        val path = targetConfig.buildDir.resolve("sim.tcl")
        val header = TargetFileHeaderBuilder.build(targetConfig, path)

        val builder = StringBuilder()
        builder.appendLine(header)
        builder.appendLine("create_project sim sim -part ${targetConfig.part}")
        builder.appendLine("set_property source_mgmt_mode None [current_project]")
        builder.appendLine("add_files -fileset sim_1 -scan_for_includes ../compile/out.sv")
        targetConfig.ipConfigFiles.forEach {
            builder.appendLine("import_ip -srcset [get_fileset sim_1] ip/${it.name}")
        }
        builder.appendLine("set_property top ${targetConfig.simTop} [get_fileset sim_1] ")
        builder.appendLine("launch_simulation -simset [get_fileset sim_1]")
        builder.appendLine("run all")
        builder.appendLine("close_project")

        return TextFile(path, builder.toString())
    }

    private fun buildSynthTclFile(targetConfig: VivadoTargetConfig): TextFile {
        val path = targetConfig.buildDir.resolve("synth.tcl")
        val header = TargetFileHeaderBuilder.build(targetConfig, path)

        val builder = StringBuilder()
        builder.appendLine(header)
        builder.appendLine("create_project synth synth -part ${targetConfig.part}")
        builder.appendLine("set_property source_mgmt_mode None [current_project]")
        builder.appendLine("add_files -fileset constrs_1 constraints.xdc")
        builder.appendLine("add_files -fileset sources_1 -scan_for_includes ../compile/out.sv")
        targetConfig.ipConfigFiles.forEach {
            builder.appendLine("import_ip -srcset [get_fileset sources_1] ip/${it.name}")
        }
        builder.appendLine("set_property top ${targetConfig.synthTop} [get_fileset sources_1] ")
        builder.appendLine("launch_runs synth_1")
        builder.appendLine("wait_on_run synth_1")
        builder.appendLine("launch_runs impl_1")
        builder.appendLine("wait_on_run impl_1")
        builder.appendLine("open_run impl_1")
        builder.appendLine("file mkdir synth/reports")
        reports.forEach {
            builder.appendLine("report_$it > synth/reports/$it.rpt")
        }
        builder.appendLine("write_bitstream synth/out.bit")
        builder.appendLine("close_project")

        return TextFile(path, builder.toString())
    }
}
