package com.verik.core

import java.lang.StringBuilder

// Copyright (c) 2020 Francis Wang

class TclBuilder {

    companion object {

        fun build(config: Config): String {
            val builder = StringBuilder()
            builder.appendln("""
                create_project vivado vivado -part ${config.vivado.part}
                import_files ${config.dstFile.relativeTo(config.tclDir)}
                import_files -fileset constrs_1 ${config.vivado.constraints.relativeTo(config.tclDir)}
                update_compile_order -fileset sources_1
                launch_runs synth_1
                wait_on_run synth_1
                launch_runs impl_1
                wait_on_run impl_1
            """.trimIndent())
            return builder.toString()
        }
    }
}