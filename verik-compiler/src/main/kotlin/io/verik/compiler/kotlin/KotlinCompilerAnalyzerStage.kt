/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.kotlin

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory

/**
 * Stage that runs the Kotlin compiler analyzer.
 */
object KotlinCompilerAnalyzerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val environment = projectContext.kotlinCoreEnvironment
        val analyzer = AnalyzerWithCompilerReport(environment.configuration)
        val ktFiles = projectContext.getKtFiles()
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
        projectContext.bindingContext = analyzer.analysisResult.bindingContext
    }
}
