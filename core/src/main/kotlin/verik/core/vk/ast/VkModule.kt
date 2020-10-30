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

package verik.core.vk.ast

import verik.core.base.ast.LineException
import verik.core.base.ast.Symbol
import verik.core.kt.ast.KtAnnotationType
import verik.core.kt.ast.KtDeclaration
import verik.core.kt.ast.KtPrimaryType
import verik.core.lang.LangSymbol.TYPE_MODULE

data class VkModule(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val ports: List<VkPort>,
        val isTop: Boolean,
        val primaryProperties: List<VkPrimaryProperty>,
        val componentInstances: List<VkComponentInstance>,
        val actionBlocks: List<VkActionBlock>
): VkComponent {

    companion object {

        fun isModule(declaration: KtDeclaration): Boolean {
            return declaration is KtPrimaryType
                    && declaration.constructorInvocation.type == TYPE_MODULE
        }

        operator fun invoke(declaration: KtDeclaration): VkModule {
            val primaryType = declaration.let {
                if (it is KtPrimaryType) it
                else throw LineException("type declaration expected", it)
            }

            if (primaryType.constructorInvocation.type != TYPE_MODULE) {
                throw LineException("expected type to inherit from module", primaryType)
            }

            val isTop = KtAnnotationType.TOP in primaryType.annotations

            val ports = ArrayList<VkPort>()
            val primaryProperties = ArrayList<VkPrimaryProperty>()
            val componentInstances = ArrayList<VkComponentInstance>()
            val actionBlocks = ArrayList<VkActionBlock>()
            for (memberDeclaration in primaryType.declarations) {
                when {
                    VkPort.isPort(memberDeclaration) -> {
                        ports.add(VkPort(memberDeclaration))
                    }
                    VkPrimaryProperty.isPrimaryProperty(memberDeclaration) -> {
                        primaryProperties.add(VkPrimaryProperty(memberDeclaration))
                    }
                    VkComponentInstance.isComponentInstance(memberDeclaration) -> {
                        componentInstances.add(VkComponentInstance(memberDeclaration))
                    }
                    VkActionBlock.isActionBlock(memberDeclaration) -> {
                        actionBlocks.add(VkActionBlock(memberDeclaration))
                    }
                    else -> throw LineException("unable to identify declaration ${memberDeclaration.identifier}", memberDeclaration)
                }
            }

            return VkModule(
                    primaryType.line,
                    primaryType.identifier,
                    primaryType.symbol,
                    ports,
                    isTop,
                    primaryProperties,
                    componentInstances,
                    actionBlocks
            )
        }
    }
}