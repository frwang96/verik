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

package io.verik.compiler.serialize.target

import io.verik.compiler.common.ProjectStage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.target.common.CompositeTargetClassDeclaration
import io.verik.compiler.target.common.CompositeTargetFunctionDeclaration
import io.verik.compiler.target.common.Target
import io.verik.compiler.target.common.TargetClassDeclaration
import io.verik.compiler.target.common.TargetDeclaration
import io.verik.compiler.target.common.TargetFunctionDeclaration
import io.verik.compiler.target.common.TargetPackage
import io.verik.compiler.target.common.TargetScope
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

object TargetSerializerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val targetIndexerVisitor = TargetIndexerVisitor()
        projectContext.project.accept(targetIndexerVisitor)
        val targetDeclarationSet = targetIndexerVisitor.targetDeclarationSet

        val targetPackageFilePath = projectContext.config.outputSourceDir.resolve(TargetPackage.path)
        val targetSourceBuilder = TargetSourceBuilder(projectContext, targetPackageFilePath)
        targetSourceBuilder.appendLine("package ${TargetPackage.name};")
        targetSourceBuilder.indent { serializeDeclarations(targetDeclarationSet, targetSourceBuilder) }
        targetSourceBuilder.appendLine()
        targetSourceBuilder.appendLine("endpackage : ${TargetPackage.name}")
        projectContext.outputContext.targetPackageTextFile = targetSourceBuilder.toTextFile()
    }

    private fun serializeDeclarations(
        targetDeclarationSet: Set<TargetDeclaration>,
        targetSourceBuilder: TargetSourceBuilder
    ) {
        Target::class.nestedClasses.forEach { nestedClass ->
            val targetScope = nestedClass.objectInstance
            if (targetScope is TargetScope) {
                val targetFunctionDeclarations = ArrayList<TargetFunctionDeclaration>()
                targetScope::class.declaredMemberProperties.forEach {
                    @Suppress("UNCHECKED_CAST")
                    val targetFunctionDeclaration = (it as KProperty1<Any, *>).get(targetScope)
                    if (targetFunctionDeclaration is TargetFunctionDeclaration)
                        targetFunctionDeclarations.add(targetFunctionDeclaration)
                }
                serializeClassDeclaration(
                    targetSourceBuilder,
                    targetDeclarationSet,
                    targetScope.declaration,
                    targetFunctionDeclarations
                )
            }
        }
    }

    private fun serializeClassDeclaration(
        targetSourceBuilder: TargetSourceBuilder,
        targetDeclarationSet: Set<TargetDeclaration>,
        targetClassDeclaration: TargetClassDeclaration,
        targetFunctionDeclarations: List<TargetFunctionDeclaration>
    ) {
        if (targetClassDeclaration is CompositeTargetClassDeclaration &&
            targetClassDeclaration in targetDeclarationSet
        ) {
            targetSourceBuilder.appendLine()
            targetSourceBuilder.appendLine(targetClassDeclaration.prolog)
            targetSourceBuilder.indent {
                if (targetClassDeclaration.body != null) {
                    targetSourceBuilder.appendLine()
                    targetSourceBuilder.appendLine(targetClassDeclaration.body)
                }
                targetFunctionDeclarations.forEach {
                    if (it is CompositeTargetFunctionDeclaration && it in targetDeclarationSet) {
                        targetSourceBuilder.appendLine()
                        targetSourceBuilder.appendLine(it.content)
                    }
                }
                targetSourceBuilder.appendLine()
            }
            targetSourceBuilder.appendLine(targetClassDeclaration.epilog)
        }
    }
}
