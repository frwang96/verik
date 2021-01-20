/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.main

import verikc.base.config.ProjectConfig
import verikc.kt.ast.KtCompilationUnit
import verikc.kt.ast.KtPkg
import verikc.kt.ast.KtType

object HeaderBuilder {

    fun build(projectConfig: ProjectConfig, compilationUnit: KtCompilationUnit) {
        StatusPrinter.info("writing headers", 1)
        for (pkg in compilationUnit.pkgs) {
            val fileString = build(pkg)
            if (fileString != null) {
                val fileHeader = projectConfig.header(pkg.config.dir, pkg.config.header)
                pkg.config.header.writeText(fileHeader + "\n" + fileString)
                StatusPrinter.info("+ ${pkg.config.header.relativeTo(projectConfig.pathConfig.projectDir)}", 2)
            } else {
                if (pkg.config.header.exists()) {
                    pkg.config.header.delete()
                    StatusPrinter.info("- ${pkg.config.header.relativeTo(projectConfig.pathConfig.projectDir)}", 2)
                }
            }
        }
    }

    fun build(pkg: KtPkg): String? {
        if (pkg.files.all { it.types.isEmpty() }) return null

        val builder = StringBuilder()
        builder.appendLine("@file:Suppress(\"FunctionName\", \"unused\", \"UNUSED_PARAMETER\", \"UnusedImport\")")
        if (pkg.config.identifierKt != "") {
            builder.appendLine("\npackage ${pkg.config.identifierKt}")
        }
        builder.appendLine()
        builder.appendLine("import verik.base.*")
        builder.appendLine("import verik.data.*")

        for (file in pkg.files) {
            file.types.forEach {
                buildDeclaration(it, builder)
            }
        }
        return builder.toString()
    }

    private fun buildDeclaration(declaration: KtType, builder: StringBuilder) {
        val parentIdentifier = declaration.typeParent.typeIdentifier
        val identifier = declaration.identifier

        when (parentIdentifier) {
            "Bus" -> {
                builder.appendLine("\ninfix fun $identifier.con(x: $identifier) {}")
                builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
                builder.appendLine("\nfun t_$identifier() = $identifier()")
            }
            "BusPort", "ClockPort" -> {
                builder.appendLine("\ninfix fun $identifier.con(x: $identifier) {}")
                builder.appendLine("\nfun t_$identifier() = $identifier()")
            }
            "_enum" -> {
                builder.appendLine("\nfun $identifier() = $identifier.values()[0]")
                builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
            }
            "Struct" -> {
                builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
            }
            "Module" -> {
                builder.appendLine("\nfun t_$identifier() = $identifier()")
            }
            else -> {
                if (!declaration.isStatic) {
                    builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
                    buildConstructorFunctions(declaration, builder)
                }
            }
        }
    }

    private fun buildConstructorFunctions(declaration: KtType, builder: StringBuilder) {
        val baseIdentifier = declaration.identifier.substring(1)
        var hasExplicitConstructor = false
        for (function in declaration.functions) {
            if (function.identifier == "init") {
                val parameterProperties = declaration.parameterProperties + function.parameterProperties
                val parameterString = parameterProperties.joinToString { "${it.identifier}: ${it.typeIdentifier}" }
                builder.append("\nfun $baseIdentifier($parameterString) = ")
                builder.append("_$baseIdentifier(${declaration.parameterProperties.joinToString { it.identifier }})")
                builder.append(".also{ it.init(${function.parameterProperties.joinToString { it.identifier }}) }\n")
                hasExplicitConstructor = true
            }
        }
        if (!hasExplicitConstructor) {
            val parameterString = declaration.parameterProperties.joinToString { "${it.identifier}: ${it.typeIdentifier}" }
            val invocationString = declaration.parameterProperties.joinToString { it.identifier }
            builder.appendLine("\nfun $baseIdentifier($parameterString) = _$baseIdentifier($invocationString)")
        }
    }
}
