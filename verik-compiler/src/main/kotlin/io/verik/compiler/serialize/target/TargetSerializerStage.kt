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
import io.verik.compiler.target.common.Target
import io.verik.compiler.target.common.TargetPackage
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

object TargetSerializerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val targetPackageFilePath = projectContext.config.outputSourceDir.resolve(TargetPackage.path)
        val targetSourceBuilder = TargetSourceBuilder(projectContext, targetPackageFilePath)
        targetSourceBuilder.appendLine("package ${TargetPackage.name};")
        targetSourceBuilder.indent {
            Target::class.memberProperties.forEach {
                @Suppress("UNCHECKED_CAST")
                val property = (it as KProperty1<Any, *>).get(Target)
                if (property is CompositeTargetClassDeclaration) {
                    serializeDeclaration(targetSourceBuilder, property)
                }
            }
        }
        targetSourceBuilder.appendLine()
        targetSourceBuilder.appendLine("endpackage : ${TargetPackage.name}")
        projectContext.outputContext.targetPackageTextFile = targetSourceBuilder.toTextFile()
    }

    private fun serializeDeclaration(
        targetSourceBuilder: TargetSourceBuilder,
        declaration: CompositeTargetClassDeclaration
    ) {
        targetSourceBuilder.appendLine()
        targetSourceBuilder.appendLine(declaration.content)
    }
}
