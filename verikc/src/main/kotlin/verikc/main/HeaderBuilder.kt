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
import verikc.kt.ast.KtProperty
import verikc.kt.ast.KtType
import verikc.lang.util.LangIdentifierUtil

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
        val typeConstructorIdentifier = LangIdentifierUtil.typeConstructorIdentifier(identifier)

        if (parentIdentifier != "Enum") {
            builder.append("\nfun $typeConstructorIdentifier(${getParameterString(declaration.parameterProperties)})")
            builder.appendLine(" = $identifier(${getInvocationString(declaration.parameterProperties)})")
        } else {
            builder.appendLine("\nfun $typeConstructorIdentifier() = $identifier.values()[0]")
        }

        when (parentIdentifier) {
            "Bus" -> {
                builder.appendLine("\ninfix fun $identifier.con(x: $identifier) {}")
                builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
            }
            "BusPort", "ClockPort" -> {
                builder.appendLine("\ninfix fun $identifier.con(x: $identifier) {}")
            }
            "Enum", "Struct" -> {
                builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
            }
            "Module" -> {}
            else -> {
                if (!declaration.isStatic) {
                    builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
                    buildInstanceConstructorFunctions(declaration, builder)
                }
            }
        }
    }

    private fun buildInstanceConstructorFunctions(declaration: KtType, builder: StringBuilder) {
        val identifier = declaration.identifier
        val instanceConstructorIdentifier = LangIdentifierUtil.instanceConstructorIdentifier(identifier)

        var hasExplicitConstructor = false
        for (function in declaration.functions) {
            if (function.identifier == "init") {
                val parameterProperties = declaration.parameterProperties + function.parameterProperties
                builder.append("\nfun $instanceConstructorIdentifier(${getParameterString(parameterProperties)})")
                builder.append(" = $identifier(${getInvocationString(declaration.parameterProperties)})")
                builder.appendLine(".also{ it.init(${getInvocationString(function.parameterProperties)}) }")
                hasExplicitConstructor = true
            }
        }

        if (!hasExplicitConstructor) {
            builder.append("\nfun $instanceConstructorIdentifier")
            builder.append("(${getParameterString(declaration.parameterProperties)})")
            builder.appendLine(" = $identifier(${getInvocationString(declaration.parameterProperties)})")
        }
    }

    private fun getParameterString(parameterProperties: List<KtProperty>): String {
        return parameterProperties.joinToString { "${it.identifier}: ${it.typeIdentifier}" }
    }

    private fun getInvocationString(parameterProperties: List<KtProperty>): String {
        return parameterProperties.joinToString { it.identifier }
    }
}
