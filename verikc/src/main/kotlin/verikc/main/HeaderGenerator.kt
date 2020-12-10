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

package verikc.main

import verikc.kt.ast.*
import verikc.main.config.PkgConfig
import verikc.main.config.ProjectConfig

object HeaderGenerator {

    fun generate(projectConfig: ProjectConfig, compilationUnit: KtCompilationUnit) {
        StatusPrinter.info("writing headers", 1)
        for (pkg in projectConfig.symbolContext.pkgs()) {
            val pkgConfig = projectConfig.symbolContext.pkgConfig(pkg)
            val fileString = build(pkgConfig, compilationUnit.pkg(pkg))
            if (fileString != null) {
                write(projectConfig, pkgConfig, fileString)
            } else {
                if (pkgConfig.header.exists()) {
                    StatusPrinter.info("- ${pkgConfig.header.relativeTo(projectConfig.projectDir)}", 2)
                    pkgConfig.header.delete()
                }
            }
        }
    }

    private fun build(pkgConfig: PkgConfig, pkg: KtPkg): String? {
        val builder = StringBuilder()
        builder.appendLine("@file:Suppress(\"FunctionName\", \"unused\", \"UNUSED_PARAMETER\", \"UnusedImport\")")
        builder.appendLine("\npackage ${pkgConfig.pkgKt}")
        builder.appendLine()
        builder.appendLine("import verik.data.*")

        var isEmpty = true
        for (file in pkg.files) {
            file.declarations.forEach {
                if (it is KtPrimaryType) {
                    if(buildDeclaration(it, builder)) {
                        isEmpty = false
                    }
                }
            }
        }

        return if (!isEmpty) builder.toString()
        else null
    }

    private fun buildDeclaration(declaration: KtPrimaryType, builder: StringBuilder): Boolean {
        val constructorIdentifier = declaration.constructorInvocation.typeIdentifier
        val identifier = declaration.identifier

        return when (constructorIdentifier) {
            "_bus" -> {
                builder.appendLine("\ninfix fun $identifier.con(x: $identifier) {}")
                builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
                true
            }
            "_busport" -> {
                builder.appendLine("\ninfix fun $identifier.con(x: $identifier) {}")
                true
            }
            "_enum" -> {
                builder.appendLine("\nfun $identifier() = $identifier.values()[0]")
                builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
                true
            }
            "_struct" -> {
                builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
                true
            }
            "_module" -> {
                false
            }
            else -> {
                builder.appendLine("\ninfix fun $identifier.set(x: $identifier) {}")
                if (KtAnnotationType.STATIC !in declaration.annotations) {
                    buildConstructorFunctions(declaration, builder)
                }
                true
            }
        }
    }

    private fun buildConstructorFunctions(declaration: KtPrimaryType, builder: StringBuilder) {
        val baseIdentifier = declaration.identifier.substring(1)
        var hasExplicitConstructor = false
        for (primaryFunction in declaration.declarations) {
            if (primaryFunction is KtPrimaryFunction && primaryFunction.identifier == "init") {
                val parameters = declaration.parameters + primaryFunction.parameters
                val parameterString = parameters.joinToString { "${it.identifier}: ${it.typeIdentifier}" }
                builder.append("\nfun $baseIdentifier($parameterString) = ")
                builder.append("_$baseIdentifier(${declaration.parameters.joinToString { it.identifier }})")
                builder.append(".also{ it.init(${primaryFunction.parameters.joinToString { it.identifier }}) }\n")
                hasExplicitConstructor = true
            }
        }
        if (!hasExplicitConstructor) {
            val parameterString = declaration.parameters.joinToString { "${it.identifier}: ${it.typeIdentifier}" }
            val invocationString = declaration.parameters.joinToString { it.identifier }
            builder.appendLine("\nfun $baseIdentifier($parameterString) = _$baseIdentifier($invocationString)")
        }
    }

    private fun write(projectConfig: ProjectConfig, pkgConfig: PkgConfig, fileString: String) {
        StatusPrinter.info("+ ${pkgConfig.header.relativeTo(projectConfig.projectDir)}", 2)
        val fileHeader = FileHeaderBuilder.build(projectConfig, pkgConfig.dir, pkgConfig.header)
        pkgConfig.header.writeText(fileHeader + "\n" + fileString)
    }
}
