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

package io.verik.core.vk

import io.verik.core.LinePos
import io.verik.core.LinePosException
import io.verik.core.kt.KtRuleType
import io.verik.core.sv.SvBlock
import io.verik.core.sv.SvContinuousAssignment
import io.verik.core.sv.SvInstance
import io.verik.core.sv.SvModule

data class VkModule(
        val isTop: Boolean,
        val identifier: String,
        val instances: List<VkInstance>,
        val moduleDeclarations: List<VkModuleDeclaration>,
        val blocks: List<VkBlock>,
        val linePos: LinePos
) {

    fun extract(): SvModule {
        val svIdentifier = identifier.drop(1)

        val instances = instances.map { it.extract() }
        val svPorts: ArrayList<SvInstance> = ArrayList()
        val svInstances: ArrayList<SvInstance> = ArrayList()
        for (instance in instances) {
            if (instance.usageType.isPort()) {
                svPorts.add(instance)
            } else {
                svInstances.add(instance)
            }
        }

        val svModuleDeclarations = moduleDeclarations.map { it.extract() }
        val svContinuousAssignments: ArrayList<SvContinuousAssignment> = ArrayList()
        val svBlocks: ArrayList<SvBlock> = ArrayList()
        for (block in blocks) {
            val continuousAssignment = block.extractContinuousAssignment()
            if (continuousAssignment != null) {
                svContinuousAssignments.add(continuousAssignment)
            } else {
                svBlocks.add(block.extractBlock())
            }
        }

        return SvModule(svIdentifier, svPorts, svInstances, svModuleDeclarations, svContinuousAssignments, svBlocks, linePos)
    }

    companion object {

        fun isModule(classDeclaration: VkClassDeclaration): Boolean {
            return classDeclaration.delegationSpecifierName in listOf("_module", "_circuit")
        }

        operator fun invoke(classDeclaration: VkClassDeclaration): VkModule {
            val isTop = when (classDeclaration.annotations.size) {
                0 -> false
                1 -> when (classDeclaration.annotations[0]) {
                    VkClassAnnotation.TOP -> true
                }
                else -> throw LinePosException("illegal module annotations", classDeclaration.linePos)
            }

            if (classDeclaration.modifiers.isNotEmpty()) {
                throw LinePosException("class modifiers are not permitted here", classDeclaration.linePos)
            }

            if (classDeclaration.delegationSpecifierName != "_module") {
                throw LinePosException("illegal delegation specifier", classDeclaration.linePos)
            }

            val declarations = if (classDeclaration.body != null) {
                if (classDeclaration.body.type == KtRuleType.ENUM_CLASS_BODY) {
                    throw LinePosException("enum class body is not permitted here", classDeclaration.linePos)
                } else {
                    classDeclaration.body.childAs(KtRuleType.CLASS_MEMBER_DECLARATIONS)
                            .childrenAs(KtRuleType.CLASS_MEMBER_DECLARATION)
                            .map { it.firstAsRule() }
                            .map { VkDeclaration(it) }
                }
            } else listOf()

            val instances: ArrayList<VkInstance> = ArrayList()
            val moduleDeclarations: ArrayList<VkModuleDeclaration> = ArrayList()
            val blocks: ArrayList<VkBlock> = ArrayList()
            for (declaration in declarations) {
                when (declaration) {
                    is VkClassDeclaration -> throw LinePosException("nested classes are not permitted", declaration.linePos)
                    is VkFunctionDeclaration -> {
                        if (VkBlock.isBlock(declaration)) blocks.add(VkBlock(declaration))
                        else throw LinePosException("unsupported function declaration", declaration.linePos)
                    }
                    is VkPropertyDeclaration -> {
                        if (VkModuleDeclaration.isModuleDeclaration(declaration)) {
                            moduleDeclarations.add(VkModuleDeclaration(declaration))
                        } else {
                            instances.add(VkInstance(declaration))
                        }
                    }
                }
            }

            return VkModule(isTop, classDeclaration.identifier, instances, moduleDeclarations, blocks, classDeclaration.linePos)
        }
    }
}