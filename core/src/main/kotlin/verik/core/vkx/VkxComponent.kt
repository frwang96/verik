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
import verik.core.kt.KtConstructorInvocation
import verik.core.kt.KtDeclaration
import verik.core.kt.KtDeclarationType
import verik.core.main.LineException
import verik.core.svx.SvxModule
import verik.core.symbol.Symbol

enum class VkxComponentType {
    MODULE,
    INTERF,
    MODPORT;

    companion object {

        operator fun invoke(constructorInvocation: KtConstructorInvocation): VkxComponentType {
            return when (constructorInvocation.typeIdentifier) {
                "_module" -> MODULE
                "_interf" -> INTERF
                "_modport" -> MODPORT
                else -> throw LineException("illegal component type", constructorInvocation)
            }
        }
    }
}

data class VkxComponent(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val componentType: VkxComponentType,
        val isTop: Boolean,
        val ports: List<VkxPort>,
        val properties: List<VkxProperty>,
        val componentInstances: List<VkxComponentInstance>,
        val actionBlocks: List<VkxActionBlock>
): VkxDeclaration {

    fun extractModule(): SvxModule {
        if (componentType != VkxComponentType.MODULE) {
            throw LineException("component cannot be extracted as module", this)
        }
        return SvxModule(
                line,
                identifier.substring(1),
                listOf(),
                actionBlocks.map { it.extract() }
        )
    }

    companion object {

        fun isComponent(declaration: KtDeclaration): Boolean {
            return declaration is KtDeclarationType && declaration.constructorInvocation.typeIdentifier in listOf(
                    "_module",
                    "_interf",
                    "_modport"
            )
        }

        operator fun invoke(declaration: KtDeclaration): VkxComponent {
            val declarationType = declaration.let {
                if (it is KtDeclarationType) it
                else throw LineException("type declaration expected", it)
            }

            if (!declaration.identifier.matches(Regex("_[a-zA-Z].*"))) {
                throw LineException("component identifier should begin with a single underscore", declaration)
            }

            val componentType = VkxComponentType(declarationType.constructorInvocation)
            val isTop = KtAnnotationType.TOP in declarationType.annotations
            if (KtAnnotationType.EXPORT in declarationType.annotations) {
                throw LineException("components cannot be exported", declarationType)
            }
            if (KtAnnotationType.ABSTRACT in declarationType.annotations) {
                throw LineException("components cannot be abstract", declarationType)
            }
            if (componentType != VkxComponentType.MODULE && isTop) {
                throw LineException("component cannot be top", declarationType)
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

            return VkxComponent(
                    declarationType.line,
                    declarationType.identifier,
                    declarationType.symbol,
                    componentType,
                    isTop,
                    ports,
                    properties,
                    componentInstances,
                    actionBlocks
            )
        }
    }
}