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

package io.verik.compiler.main

import io.verik.compiler.common.ProjectPass
import io.verik.compiler.message.SourceLocation
import io.verik.core.*
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory
import java.io.File
import java.nio.file.Paths

class KotlinCompiler : ProjectPass {

    private val MODULE_NAME = "verik"

    override fun pass(projectContext: ProjectContext) {
        m.info("Compile: Parse input files")
        val environment = createKotlinCoreEnvironment(projectContext)
        val psiFileFactory = KtPsiFactory(environment.project, false)

        val ktFiles = projectContext.inputTextFiles.map {
            psiFileFactory.createPhysicalFile(it.path.toString(), it.content)
        }
        m.flush()

        m.info("Compile: Analyze input files")
        val analyzer = AnalyzerWithCompilerReport(environment.configuration)
        analyzer.analyzeAndReport(ktFiles) {
            TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
                environment.project,
                ktFiles,
                NoScopeRecordCliBindingTrace(),
                environment.configuration,
                environment::createPackagePartProvider,
                ::FileBasedDeclarationProviderFactory
            )
        }

        projectContext.ktFiles = ktFiles
        projectContext.bindingContext = analyzer.analysisResult.bindingContext
        m.flush()
    }

    private fun createKotlinCoreEnvironment(projectContext: ProjectContext): KotlinCoreEnvironment {
        setIdeaIoUseFallback()
        val configuration = createCompilerConfiguration(projectContext)
        val disposable = Disposer.newDisposable()
        return KotlinCoreEnvironment.createForProduction(
            disposable,
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES
        )
    }

    private fun createCompilerConfiguration(projectContext: ProjectContext): CompilerConfiguration {
        val configuration = CompilerConfiguration()
        configuration.put(JVMConfigurationKeys.JVM_TARGET, JvmTarget.JVM_1_8)
        configuration.put(
            CommonConfigurationKeys.LANGUAGE_VERSION_SETTINGS,
            LanguageVersionSettingsImpl(LanguageVersion.KOTLIN_1_4, ApiVersion.KOTLIN_1_4)
        )
        configuration.put(CommonConfigurationKeys.MODULE_NAME, MODULE_NAME)
        configuration.put(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            KotlinCompilerMessageCollector(projectContext.config.suppressCompileWarnings)
        )
        getJvmClasspathRoots().forEach {
            configuration.addJvmClasspathRoot(it)
        }
        return configuration
    }

    private fun getJvmClasspathRoots(): List<File> {
        return listOf(
            File(AnnotationTarget::class.java.protectionDomain.codeSource.location.path), // kotlin-stdlib
            File(Module::class.java.protectionDomain.codeSource.location.path) // verik-core
        )
    }

    private class KotlinCompilerMessageCollector(private val suppressCompileWarnings: Boolean) : MessageCollector {

        override fun clear() {}

        override fun hasErrors() = false

        override fun report(
            severity: CompilerMessageSeverity,
            message: String,
            location: CompilerMessageSourceLocation?
        ) {
            val sourceLocation = location?.let { SourceLocation(it.column, it.line, Paths.get(it.path), null) }
            when (severity) {
                CompilerMessageSeverity.EXCEPTION, CompilerMessageSeverity.ERROR ->
                    m.error(message, sourceLocation)
                CompilerMessageSeverity.STRONG_WARNING, CompilerMessageSeverity.WARNING ->
                    if (!suppressCompileWarnings)
                        m.warning(message, sourceLocation)
                else -> {}
            }
        }
    }
}