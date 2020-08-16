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

package verik.core.main

import verik.core.al.AlRuleParser
import verik.core.main.config.PkgConfig
import verik.core.main.config.ProjectConfig
import verik.core.main.symbol.Symbol

object HeaderGenerator {

    fun generate(config: ProjectConfig, pkg: Symbol) {
        val declarations = config.symbolContext.files(pkg).flatMap {
            val fileConfig = config.symbolContext.fileConfig(it)
            try {
                val txtFile = fileConfig.file.readText()
                val alFile = AlRuleParser.parseKotlinFile(txtFile)
                HeaderParser.parse(alFile)
            } catch (exception: LineException) {
                exception.file = fileConfig.file
                throw exception
            }
        }

        val pkgConfig = config.symbolContext.pkgConfig(pkg)
        if (declarations.isEmpty()) {
            if (pkgConfig.header.exists()) {
                StatusPrinter.info("- ${pkgConfig.header.relativeTo(config.projectDir)}", 1)
                pkgConfig.header.delete()
            }
        } else {
            if (pkgConfig.header.exists()) {
                val originalFileString = FileHeaderBuilder.strip(pkgConfig.header.readText())
                val fileString = build(pkgConfig.pkgKt, declarations)
                if (fileString != originalFileString) {
                    write(config, pkgConfig, fileString)
                }
            } else {
                write(config, pkgConfig, build(pkgConfig.pkgKt, declarations))
            }
        }
    }

    private fun write(config: ProjectConfig, pkgConfig: PkgConfig, fileString: String) {
        StatusPrinter.info("+ ${pkgConfig.header.relativeTo(config.projectDir)}", 1)
        val fileHeader = FileHeaderBuilder.build(config, pkgConfig.dir, pkgConfig.header)
        pkgConfig.header.writeText(fileHeader + "\n" + fileString)
    }

    private fun build(pkgName: String, declarations: List<HeaderDeclaration>): String {
        val builder = StringBuilder()
        builder.appendln("@file:Suppress(\"FunctionName\", \"unused\", \"UNUSED_PARAMETER\")")
        builder.appendln("\npackage $pkgName")
        for (declaration in declarations) {
            if (declaration.type != HeaderDeclarationType.CLASS_COMPANION) {
                val name = declaration.identifier.substring(1)
                when (declaration.type) {
                    HeaderDeclarationType.INTERF -> {
                        builder.appendln("\ninfix fun _$name.put(x: _$name) {}")
                        builder.appendln("\ninfix fun _$name.con(x: _$name) {}")
                    }
                    HeaderDeclarationType.MODPORT -> {
                        builder.appendln("\ninfix fun _$name.con(x: _$name) {}")
                    }
                    HeaderDeclarationType.CLASS, HeaderDeclarationType.CLASS_CHILD -> {
                        if (declarations.none { it.type == HeaderDeclarationType.CLASS_COMPANION && it.identifier == name }) {
                            builder.appendln("\nclass $name: _$name()")
                        }
                        builder.appendln("\ninfix fun _$name.put(x: _$name) {}")
                    }
                    HeaderDeclarationType.ENUM, HeaderDeclarationType.STRUCT -> {
                        if (declaration.type == HeaderDeclarationType.ENUM) {
                            builder.appendln("\nfun _$name() = _$name.values()[0]")
                        }
                        builder.appendln("\ninfix fun _$name.put(x: _$name) {}")
                        builder.appendln("\ninfix fun _$name.reg(x: _$name) {}")
                        builder.appendln("\ninfix fun _$name.con(x: _$name) {}")
                        builder.appendln("\ninfix fun _$name.eq(x: _$name) = false")
                        builder.appendln("\ninfix fun _$name.neq(x: _$name) = false")
                    }
                    else -> {}
                }
            }
        }
        return builder.toString()
    }
}