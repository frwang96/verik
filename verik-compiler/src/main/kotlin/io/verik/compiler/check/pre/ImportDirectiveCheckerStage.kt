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

package io.verik.compiler.check.pre

import io.verik.compiler.common.ProjectStage
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

object ImportDirectiveCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val packageNames = HashSet<String>()
        packageNames.add(CorePackage.VK.name)
        projectContext.ktFiles.forEach {
            val packageName = it.packageFqName.asString()
            if (packageName == "")
                Messages.PACKAGE_NAME_ROOT.on(it)
            if (packageName in listOf(CorePackage.VK.name, CorePackage.SV.name, CorePackage.ROOT.name))
                Messages.PACKAGE_NAME_RESERVED.on(it, packageName)
            packageNames.add(it.packageFqName.toString())
        }

        val importDirectiveCheckerVisitor = ImportDirectiveCheckerVisitor(packageNames)
        projectContext.ktFiles.forEach { it.accept(importDirectiveCheckerVisitor) }
    }

    class ImportDirectiveCheckerVisitor(private val packageNames: Set<String>) : KtTreeVisitorVoid() {

        override fun visitImportDirective(importDirective: KtImportDirective) {
            super.visitImportDirective(importDirective)
            val packageName = if (importDirective.isAllUnder) {
                importDirective.importedFqName!!.toString()
            } else {
                importDirective.importedFqName!!.parent().toString()
            }
            if (packageName !in packageNames)
                Messages.PACkAGE_NOT_FOUND.on(importDirective, packageName)
        }
    }
}