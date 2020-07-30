/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.core

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
                set_property xsim.simulate.runtime 0 [get_filesets sim_1]
                launch_simulation
                run
            """.trimIndent())
            return builder.toString()
        }
    }
}