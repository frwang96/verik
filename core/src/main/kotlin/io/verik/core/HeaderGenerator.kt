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

import io.verik.core.al.AlRuleParser
import io.verik.core.config.PkgConfig
import io.verik.core.config.ProjectConfig

class HeaderGenerator {

    companion object {

        fun generate(config: ProjectConfig, pkg: PkgConfig) {
            val declarations = pkg.sources.flatMap {
                val txtFile = it.source.readText()
                val alFile = AlRuleParser.parseKotlinFile(it.source.name, txtFile)
                HeaderParser.parse(alFile)
            }

            if (declarations.isEmpty()) {
                if (pkg.header.exists()) {
                    StatusPrinter.info("- ${pkg.header.relativeTo(config.projectDir)}", 1)
                    pkg.header.delete()
                }
            } else {
                if (pkg.header.exists()) {
                    val originalFileString = FileHeaderBuilder.strip(pkg.header.readText())
                    val fileString = build(pkg, declarations)
                    if (fileString != originalFileString) {
                        write(config, pkg, fileString)
                    }
                } else {
                    write(config, pkg, build(pkg, declarations))
                }
            }
        }

        private fun write(config: ProjectConfig, pkg: PkgConfig, fileString: String) {
            StatusPrinter.info("+ ${pkg.header.relativeTo(config.projectDir)}", 1)
            val fileHeader = FileHeaderBuilder.build(config, pkg.dir, pkg.header)
            pkg.header.writeText(fileHeader + "\n" + fileString)
        }

        private fun build(pkg: PkgConfig, declarations: List<HeaderDeclaration>): String {
            val builder = StringBuilder()
            builder.appendln("@file:Suppress(\"FunctionName\", \"unused\", \"UNUSED_PARAMETER\")")
            if (pkg.pkgNameKt != "") {
                builder.appendln("\npackage ${pkg.pkgNameKt}")
            }
            for (declaration in declarations) {
                val name = declaration.name
                when (declaration.type) {
                    HeaderDeclarationType.INTERF -> {
                        builder.appendln("\ninfix fun _$name.put(x: _$name?) {}")
                        builder.appendln("\ninfix fun _$name.con(x: _$name?) {}")
                    }
                    HeaderDeclarationType.MODPORT -> {
                        builder.appendln("\ninfix fun _$name.con(x: _$name?) {}")
                    }
                    HeaderDeclarationType.CLASS -> {
                        builder.appendln("\nfun $name() = _$name()")
                        builder.appendln("\ninfix fun _$name.put(x: _$name?) {}")
                    }
                    HeaderDeclarationType.SUBCLASS -> {
                        builder.appendln("\nfun $name() = _$name()")
                    }
                    HeaderDeclarationType.ENUM, HeaderDeclarationType.STRUCT -> {
                        if (declaration.type == HeaderDeclarationType.ENUM) {
                            builder.appendln("\nfun _$name() = _$name.values()[0]")
                        }
                        builder.appendln("\ninfix fun _$name.put(x: _$name?) {}")
                        builder.appendln("\ninfix fun _$name.reg(x: _$name?) {}")
                        builder.appendln("\ninfix fun _$name.drive(x: _$name?) {}")
                        builder.appendln("\ninfix fun _$name.con(x: _$name?) {}")
                    }
                }
            }
            return builder.toString()
        }
    }
}