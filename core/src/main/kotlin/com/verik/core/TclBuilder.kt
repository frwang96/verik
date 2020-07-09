package com.verik.core

// Copyright (c) 2020 Francis Wang

class TclBuilder {

    companion object {

        fun build(conf: ProjConf, top: String): String {
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
                set_property top $top [get_filesets sim_1]
                launch_simulation
                run
            """.trimIndent())
            return builder.toString()
        }
    }
}