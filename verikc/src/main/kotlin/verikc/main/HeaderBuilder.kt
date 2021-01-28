/*
 * Copyright (c) 2021 Francis Wang
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

import verikc.base.ast.AnnotationProperty
import verikc.base.ast.LineException
import verikc.base.config.ProjectConfig
import verikc.kt.ast.*
import verikc.lang.util.LangIdentifierUtil

object HeaderBuilder {

    fun build(projectConfig: ProjectConfig, compilationUnit: KtCompilationUnit) {
        for (pkg in compilationUnit.pkgs) {
            val fileString = build(pkg, projectConfig.compileConfig.topIdentifier)
            if (fileString != null) {
                if (pkg.config.header.exists()) {
                    if (pkg.config.header.readText().endsWith(fileString)) {
                        continue
                    }
                }
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

    fun build(pkg: KtPkg, topIdentifier: String): String? {
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
                buildDeclaration(it, topIdentifier, builder)
            }
        }
        return builder.toString()
    }

    private fun buildDeclaration(declaration: KtType, topIdentifier: String, builder: StringBuilder) {
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
                builder.appendLine("\ninfix fun $identifier.init(x: $identifier) {}")
                buildWithFunction(declaration, builder)
            }
            "BusPort" -> {
                buildWithFunction(declaration, builder)
            }
            "ClockPort" -> {
                buildWithFunction(declaration, builder)
            }
            "Enum" -> {
                builder.appendLine("\ninfix fun $identifier.init(x: $identifier) {}")
            }
            "Struct" -> {
                builder.appendLine("\ninfix fun $identifier.init(x: $identifier) {}")
                buildStructInstanceConstructor(declaration, builder)
            }
            "Module" -> {
                if (identifier == topIdentifier) {
                    if (declaration.parameterProperties.isNotEmpty())
                        throw LineException("parameters not permitted for top module", declaration.line)
                    builder.appendLine("\nval top = $typeConstructorIdentifier()")
                }
                buildWithFunction(declaration, builder)
            }
            else -> {
                if (!declaration.isStatic) {
                    if (parentIdentifier == "Class") {
                        builder.appendLine("\ninfix fun $identifier.init(x: $identifier) {}")
                    }
                    buildClassInstanceConstructors(declaration, builder)
                }
            }
        }
    }

    private fun buildWithFunction(declaration: KtType, builder: StringBuilder) {
        val identifier = declaration.identifier
        val parentIdentifier = declaration.typeParent.typeIdentifier
        val parameterStrings = ArrayList<String>()
        if (parentIdentifier == "ClockPort") {
            parameterStrings.add("event: Event")
        }
        for (property in declaration.properties) {
            if (property.annotations.any { it.isPortAnnotation() }) {
                val isNullable = parentIdentifier in listOf("Module", "Bus")
                        && property.annotations.any { it == AnnotationProperty.OUTPUT }
                parameterStrings.add(getPropertyParameterString(property) + if (isNullable) "?" else "")
            }
        }

        if (parameterStrings.isEmpty()) {
            builder.appendLine("\nfun $identifier.with(): $identifier {")
        } else {
            builder.appendLine("\nfun $identifier.with(")
            parameterStrings.dropLast(1).forEach { builder.appendLine("    $it,") }
            builder.appendLine("    ${parameterStrings.last()}")
            builder.appendLine("): $identifier {")
        }
        builder.appendLine("    throw Exception()")
        builder.appendLine("}")
    }

    private fun buildStructInstanceConstructor(declaration: KtType, builder: StringBuilder) {
        val identifier = declaration.identifier
        val instanceConstructorIdentifier = LangIdentifierUtil.instanceConstructorIdentifier(identifier)
        val parameterStrings = declaration.properties.map { getPropertyParameterString(it) }

        if (parameterStrings.isEmpty()) {
            builder.appendLine("\nfun $instanceConstructorIdentifier(): $identifier {")
        } else {
            builder.appendLine("\nfun $instanceConstructorIdentifier(")
            parameterStrings.dropLast(1).forEach { builder.appendLine("    $it,") }
            builder.appendLine("    ${parameterStrings.last()}")
            builder.appendLine("): $identifier {")
        }
        builder.appendLine("    throw Exception()")
        builder.appendLine("}")
    }

    private fun buildClassInstanceConstructors(declaration: KtType, builder: StringBuilder) {
        val identifier = declaration.identifier
        val instanceConstructor = declaration.instanceConstructor ?: return

        val parameterString = getParameterString(
            declaration.parameterProperties + instanceConstructor.parameterProperties
        )
        builder.append("\nfun ${instanceConstructor.identifier}($parameterString)")
        builder.append(" = $identifier(${getInvocationString(declaration.parameterProperties)})")
        if (instanceConstructor.block != null) {
            builder.appendLine(".also { it.init(${getInvocationString(instanceConstructor.parameterProperties)}) }")
        } else {
            builder.appendLine()
        }
    }

    private fun getPropertyParameterString(property: KtProperty): String {
        if (property.expression == null) throw LineException("property expression expected", property.line)
        val typeIdentifier = if (property.expression is KtExpressionFunction) {
            val typeConstructorIdentifier = property.expression.identifier
            if (!typeConstructorIdentifier.startsWith("t_"))
                throw LineException("type constructor expression expected", property.line)
            typeConstructorIdentifier.substring(2)
        } else throw LineException("type constructor expression expected", property.line)
        return "${property.identifier}: $typeIdentifier"
    }

    private fun getParameterString(parameterProperties: List<KtProperty>): String {
        return parameterProperties.joinToString { "${it.identifier}: ${it.typeIdentifier}" }
    }

    private fun getInvocationString(parameterProperties: List<KtProperty>): String {
        return parameterProperties.joinToString { it.identifier }
    }
}
