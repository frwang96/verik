package com.verik.core

import java.lang.StringBuilder

// Copyright (c) 2020 Francis Wang

class TclBuilder {

    companion object {

        fun build(conf: ProjConf): String {
            val builder = StringBuilder()
            builder.appendln("""
                create_project vivado vivado -part ${conf.vivado.part}
                import_files ${conf.dstFile.relativeTo(conf.vivado.tclFile.parentFile)}
            """.trimIndent())
            if (conf.vivado.constraints != null) {
                builder.appendln("""
                    import_files -fileset constrs_1 ${conf.vivado.constraints.relativeTo(conf.vivado.tclFile.parentFile)}
                """.trimIndent())
            }
            builder.appendln("""
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