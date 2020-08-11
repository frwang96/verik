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

package io.verik.core.main

import io.verik.core.al.AlRuleParser
import io.verik.core.config.ProjectConfig

object HeaderGenerator {

    fun generate(config: ProjectConfig, pkg: FileTablePkg) {
        val declarations = pkg.files.flatMap {
            try {
                val txtFile = it.config.file.readText()
                val alFile = AlRuleParser.parseKotlinFile(txtFile)
                HeaderParser.parse(alFile)
            } catch (exception: LineException) {
                exception.file = it.config.file
                throw exception
            }
        }

        if (declarations.isEmpty()) {
            if (pkg.config.header.exists()) {
                StatusPrinter.info("- ${pkg.config.header.relativeTo(config.projectDir)}", 1)
                pkg.config.header.delete()
            }
        } else {
            if (pkg.config.header.exists()) {
                val originalFileString = FileHeaderBuilder.strip(pkg.config.header.readText())
                val fileString = build(pkg, declarations)
                if (fileString != originalFileString) {
                    write(config, pkg, fileString)
                }
            } else {
                write(config, pkg, build(pkg, declarations))
            }
        }
    }

    private fun write(config: ProjectConfig, pkg: FileTablePkg, fileString: String) {
        StatusPrinter.info("+ ${pkg.config.header.relativeTo(config.projectDir)}", 1)
        val fileHeader = FileHeaderBuilder.build(config, pkg.config.dir, pkg.config.header)
        pkg.config.header.writeText(fileHeader + "\n" + fileString)
    }

    private fun build(pkg: FileTablePkg, declarations: List<HeaderDeclaration>): String {
        val builder = StringBuilder()
        builder.appendln("@file:Suppress(\"FunctionName\", \"unused\", \"UNUSED_PARAMETER\")")
        if (pkg.config.pkgKt != "") {
            builder.appendln("\npackage ${pkg.config.pkgKt}")
        }
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
                    }
                    else -> {}
                }
            }
        }
        return builder.toString()
    }
}