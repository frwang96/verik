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

package verik.core.vk

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.KtAnnotationType
import verik.core.kt.KtDeclaration
import verik.core.kt.KtDeclarationType
import verik.core.lang.LangSymbol.TYPE_MODULE

data class VkModule(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val ports: List<VkPort>,
        val isTop: Boolean,
        val baseProperties: List<VkBaseProperty>,
        val componentInstances: List<VkComponentInstance>,
        val actionBlocks: List<VkActionBlock>
): VkComponent {

    companion object {

        fun isModule(declaration: KtDeclaration): Boolean {
            return declaration is KtDeclarationType
                    && declaration.constructorInvocation.type == TYPE_MODULE
        }

        operator fun invoke(declaration: KtDeclaration): VkModule {
            val declarationType = declaration.let {
                if (it is KtDeclarationType) it
                else throw LineException("type declaration expected", it)
            }

            if (declarationType.constructorInvocation.type != TYPE_MODULE) {
                throw LineException("expected type to inherit from module", declarationType)
            }
            if (!declarationType.identifier.matches(Regex("_[a-zA-Z].*"))) {
                throw LineException("module identifier should begin with a single underscore", declarationType)
            }

            val isTop = KtAnnotationType.TOP in declarationType.annotations

            val ports = ArrayList<VkPort>()
            val baseProperties = ArrayList<VkBaseProperty>()
            val componentInstances = ArrayList<VkComponentInstance>()
            val actionBlocks = ArrayList<VkActionBlock>()
            for (memberDeclaration in declarationType.declarations) {
                when {
                    VkPort.isPort(memberDeclaration) -> {
                        ports.add(VkPort(memberDeclaration))
                    }
                    VkBaseProperty.isBaseProperty(memberDeclaration) -> {
                        baseProperties.add(VkBaseProperty(memberDeclaration))
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
                    declarationType.line,
                    declarationType.identifier,
                    declarationType.symbol,
                    ports,
                    isTop,
                    baseProperties,
                    componentInstances,
                    actionBlocks
            )
        }
    }
}