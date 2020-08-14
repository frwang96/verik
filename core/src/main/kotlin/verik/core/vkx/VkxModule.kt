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

package verik.core.vkx

import verik.core.kt.KtAnnotationType
import verik.core.kt.KtDeclaration
import verik.core.kt.KtDeclarationType
import verik.core.main.LineException
import verik.core.symbol.Symbol

data class VkxModule(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val ports: List<VkxPort>,
        val isTop: Boolean,
        val properties: List<VkxProperty>,
        val componentInstances: List<VkxComponentInstance>,
        val actionBlocks: List<VkxActionBlock>
): VkxComponent {

    companion object {

        fun isModule(declaration: KtDeclaration): Boolean {
            return declaration is KtDeclarationType
                    && declaration.constructorInvocation.typeIdentifier == "_module"
        }

        operator fun invoke(declaration: KtDeclaration): VkxModule {
            val declarationType = declaration.let {
                if (it is KtDeclarationType) it
                else throw LineException("type declaration expected", it)
            }

            if (declarationType.constructorInvocation.typeIdentifier != "_module") {
                throw LineException("expected type to inherit from module", declarationType)
            }
            if (!declarationType.identifier.matches(Regex("_[a-zA-Z].*"))) {
                throw LineException("module identifier should begin with a single underscore", declarationType)
            }

            val isTop = KtAnnotationType.TOP in declarationType.annotations
            if (KtAnnotationType.EXPORT in declarationType.annotations) {
                throw LineException("module cannot be exported", declarationType)
            }
            if (KtAnnotationType.ABSTRACT in declarationType.annotations) {
                throw LineException("module cannot be abstract", declarationType)
            }

            val ports = ArrayList<VkxPort>()
            val properties = ArrayList<VkxProperty>()
            val componentInstances = ArrayList<VkxComponentInstance>()
            val actionBlocks = ArrayList<VkxActionBlock>()
            for (memberDeclaration in declarationType.declarations) {
                when {
                    VkxPort.isPort(memberDeclaration) -> {
                        ports.add(VkxPort(memberDeclaration))
                    }
                    VkxProperty.isProperty(memberDeclaration) -> {
                        properties.add(VkxProperty(memberDeclaration))
                    }
                    VkxComponentInstance.isComponentInstance(memberDeclaration) -> {
                        componentInstances.add(VkxComponentInstance(memberDeclaration))
                    }
                    VkxActionBlock.isActionBlock(memberDeclaration) -> {
                        actionBlocks.add(VkxActionBlock(memberDeclaration))
                    }
                    else -> throw LineException("unable to identify declaration ${memberDeclaration.identifier}", memberDeclaration)
                }
            }

            return VkxModule(
                    declarationType.line,
                    declarationType.identifier,
                    declarationType.symbol,
                    ports,
                    isTop,
                    properties,
                    componentInstances,
                    actionBlocks
            )
        }
    }
}