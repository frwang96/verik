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

import io.verik.core.Line
import io.verik.core.LineException
import io.verik.core.al.AlRuleType
import io.verik.core.sv.SvBlock
import io.verik.core.sv.SvContinuousAssignment
import io.verik.core.sv.SvInstanceDeclaration
import io.verik.core.sv.SvModule

data class VkModule(
        override val line: Int,
        val isTop: Boolean,
        val identifier: String,
        val instanceDeclarations: List<VkInstanceDeclaration>,
        val moduleDeclarations: List<VkModuleDeclaration>,
        val blocks: List<VkBlock>
): Line {

    fun extract(): SvModule {
        val svIdentifier = identifier.drop(1)

        val svPortDeclarations: ArrayList<SvInstanceDeclaration> = ArrayList()
        val svInstanceDeclarations: ArrayList<SvInstanceDeclaration> = ArrayList()
        for (instance in instanceDeclarations.map { it.extract() }) {
            if (instance.portType.isPort()) {
                svPortDeclarations.add(instance)
            } else {
                svInstanceDeclarations.add(instance)
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

        return SvModule(
                line,
                svIdentifier,
                svPortDeclarations,
                svInstanceDeclarations,
                svModuleDeclarations,
                svContinuousAssignments,
                svBlocks
        )
    }

    companion object {

        fun isModule(classDeclaration: VkClassDeclaration): Boolean {
            return classDeclaration.delegationSpecifierName in listOf("_module", "_circuit")
        }

        operator fun invoke(classDeclaration: VkClassDeclaration): VkModule {
            if (VkClassAnnotation.ABSTRACT in classDeclaration.annotations) {
                throw LineException("modules cannot be abstract", classDeclaration)
            }
            if (VkClassAnnotation.EXPORT in classDeclaration.annotations) {
                throw LineException("modules cannot be exported", classDeclaration)
            }

            val isTop = (VkClassAnnotation.TOP) in classDeclaration.annotations

            if (classDeclaration.modifiers.isNotEmpty()) {
                throw LineException("class modifiers are not permitted here", classDeclaration)
            }

            if (classDeclaration.delegationSpecifierName != "_module") {
                throw LineException("illegal delegation specifier", classDeclaration)
            }

            val declarations = if (classDeclaration.body != null) {
                if (classDeclaration.body.type == AlRuleType.ENUM_CLASS_BODY) {
                    throw LineException("enum class body is not permitted here", classDeclaration)
                } else {
                    classDeclaration.body.childAs(AlRuleType.CLASS_MEMBER_DECLARATIONS)
                            .childrenAs(AlRuleType.CLASS_MEMBER_DECLARATION)
                            .map { it.firstAsRule() }
                            .map { VkDeclaration(it) }
                }
            } else listOf()

            val instanceDeclarations: ArrayList<VkInstanceDeclaration> = ArrayList()
            val moduleDeclarations: ArrayList<VkModuleDeclaration> = ArrayList()
            val blocks: ArrayList<VkBlock> = ArrayList()
            for (declaration in declarations) {
                when (declaration) {
                    is VkClassDeclaration -> throw LineException("nested classes are not permitted", declaration)
                    is VkFunctionDeclaration -> {
                        if (VkBlock.isBlock(declaration)) blocks.add(VkBlock(declaration))
                        else throw LineException("unsupported function declaration", declaration)
                    }
                    is VkPropertyDeclaration -> {
                        if (VkModuleDeclaration.isModuleDeclaration(declaration)) {
                            moduleDeclarations.add(VkModuleDeclaration(declaration))
                        } else {
                            instanceDeclarations.add(VkInstanceDeclaration(declaration))
                        }
                    }
                }
            }

            return VkModule(
                    classDeclaration.line,
                    isTop,
                    classDeclaration.identifier,
                    instanceDeclarations,
                    moduleDeclarations,
                    blocks
            )
        }
    }
}