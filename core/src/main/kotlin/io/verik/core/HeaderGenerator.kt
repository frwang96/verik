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

import io.verik.core.config.PkgConfig
import io.verik.core.config.ProjectConfig
import io.verik.core.kt.KtRuleParser

class HeaderGenerator {

    companion object {

        fun generate(config: ProjectConfig, pkg: PkgConfig) {
            val declarations = pkg.sources.flatMap {
                val txtFile = it.source.readText()
                val ktFile = KtRuleParser.parseKotlinFile(txtFile)
                HeaderParser.parse(ktFile)
            }

            if (declarations.isEmpty()) {
                if (pkg.header.exists()) {
                    StatusPrinter.info("removing header file: ${pkg.header.relativeTo(config.projectDir)}")
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
            StatusPrinter.info("writing header file: ${pkg.header.relativeTo(config.projectDir)}")
            val fileHeader = FileHeaderBuilder.build(config, pkg.dir, pkg.header)
            pkg.header.writeText(fileHeader + "\n" + fileString)
        }

        private fun build(pkg: PkgConfig, declarations: List<HeaderDeclaration>): String {
            val builder = StringBuilder()
            builder.appendln("@file:Suppress(\"FunctionName\")")
            if (pkg.pkgNameKt != "") {
                builder.appendln()
                builder.appendln("package ${pkg.pkgNameKt}")
            }
            for (declaration in declarations) {
                builder.appendln()
                val name = declaration.name
                when (declaration.type) {
                    HeaderDeclarationType.ENUM -> {
                        builder.appendln("fun _$name() = _$name.values()[0]")
                    }
                    HeaderDeclarationType.CLASS -> {
                        builder.appendln("fun $name() = _$name()")
                    }
                }
            }
            return builder.toString()
        }
    }
}