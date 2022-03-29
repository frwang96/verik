/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.cast.CastContext
import io.verik.compiler.specialize.SpecializeContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Context that stores all of the internal state of the compiler.
 */
class ProjectContext(
    val config: VerikConfig
) {

    val report = VerikReport()
    var sourceSetContexts: List<SourceSetContext> = listOf()
    val processedProjectStages = HashSet<ProjectStage>()
    lateinit var kotlinCoreEnvironment: KotlinCoreEnvironment
    lateinit var bindingContext: BindingContext
    lateinit var castContext: CastContext
    var specializeContext: SpecializeContext? = null
    lateinit var project: EProject
    val outputContext = OutputContext()

    fun getKtFiles(): List<KtFile> {
        return sourceSetContexts.flatMap { it.ktFiles }
    }
}
