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

import io.verik.compiler.ast.element.common.ETypedElement
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.target.common.CompositeTarget
import io.verik.compiler.target.common.TargetPackage
import io.verik.compiler.target.serialize.ClassTargetSerializationEntry
import io.verik.compiler.target.serialize.FunctionTargetSerializationEntry
import io.verik.compiler.target.serialize.TargetSerializationSequenceChecker
import io.verik.compiler.target.serialize.TargetSerializationSequencer

object CompositeTargetSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val compositeTargetIndexerVisitor = CompositeTargetIndexerVisitor()
        projectContext.project.accept(compositeTargetIndexerVisitor)
        val compositeTargetSet = compositeTargetIndexerVisitor.compositeTargetSet
        if (compositeTargetSet.isEmpty())
            return

        val targetPackageFilePath = projectContext.config.outputSourceDir.resolve(TargetPackage.path)
        val targetSourceBuilder = TargetSourceBuilder(projectContext, targetPackageFilePath)
        targetSourceBuilder.appendLine("package ${TargetPackage.name};")
        targetSourceBuilder.indent {
            val sequence = TargetSerializationSequencer.getSequence()
            if (projectContext.config.debug)
                TargetSerializationSequenceChecker.checkSequence(sequence)
            sequence.entries.forEach {
                when (it) {
                    is FunctionTargetSerializationEntry ->
                        serializeFunction(it, compositeTargetSet, targetSourceBuilder)
                    is ClassTargetSerializationEntry ->
                        serializeClass(it, compositeTargetSet, targetSourceBuilder)
                }
            }
        }
        targetSourceBuilder.appendLine()
        targetSourceBuilder.appendLine("endpackage : ${TargetPackage.name}")
        projectContext.outputContext.targetPackageTextFile = targetSourceBuilder.toTextFile()
    }

    private fun serializeFunction(
        entry: FunctionTargetSerializationEntry,
        compositeTargetSet: Set<CompositeTarget>,
        targetSourceBuilder: TargetSourceBuilder
    ) {
        if (entry.functionDeclaration in compositeTargetSet) {
            targetSourceBuilder.appendLine()
            targetSourceBuilder.appendLine(entry.functionDeclaration.contentBody)
        }
    }

    private fun serializeClass(
        entry: ClassTargetSerializationEntry,
        compositeTargetSet: Set<CompositeTarget>,
        targetSourceBuilder: TargetSourceBuilder
    ) {
        if (entry.classDeclaration in compositeTargetSet) {
            targetSourceBuilder.appendLine()
            targetSourceBuilder.appendLine(entry.classDeclaration.contentProlog)
            targetSourceBuilder.indent {
                targetSourceBuilder.appendLine()
                targetSourceBuilder.appendLine(entry.classDeclaration.contentBody)
                entry.functionDeclarations.forEach {
                    if (it in compositeTargetSet) {
                        targetSourceBuilder.appendLine()
                        targetSourceBuilder.appendLine(it.contentBody)
                    }
                }
                targetSourceBuilder.appendLine()
            }
            targetSourceBuilder.appendLine(entry.classDeclaration.contentEpilog)
        }
    }

    private class CompositeTargetIndexerVisitor : TreeVisitor() {

        val compositeTargetSet = HashSet<CompositeTarget>()

        override fun visitTypedElement(typedElement: ETypedElement) {
            super.visitTypedElement(typedElement)
            addTargets(typedElement.type)
            if (typedElement is Reference) {
                val reference = typedElement.reference
                if (reference is CompositeTarget)
                    compositeTargetSet.add(reference)
            }
        }

        private fun addTargets(type: Type) {
            type.arguments.forEach { addTargets(it) }
            val reference = type.reference
            if (reference is CompositeTarget)
                compositeTargetSet.add(reference)
        }
    }
}
