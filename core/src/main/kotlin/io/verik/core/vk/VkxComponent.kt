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

import io.verik.core.LineException
import io.verik.core.kt.KtAnnotationType
import io.verik.core.kt.KtConstructorInvocation
import io.verik.core.kt.KtDeclaration
import io.verik.core.kt.KtDeclarationType
import io.verik.core.symbol.Symbol

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
        val componentInstantiations: List<VkxComponentInstantiation>,
        val actionBlocks: List<VkxActionBlock>
): VkxDeclaration {

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
            val componentInstantiations = ArrayList<VkxComponentInstantiation>()
            val actionBlocks = ArrayList<VkxActionBlock>()
            for (memberDeclaration in declarationType.declarations) {
                when {
                    VkxPort.isPort(memberDeclaration) -> {
                        ports.add(VkxPort(memberDeclaration))
                    }
                    VkxProperty.isProperty(memberDeclaration) -> {
                        properties.add(VkxProperty(memberDeclaration))
                    }
                    VkxComponentInstantiation.isComponentInstantiation(memberDeclaration) -> {
                        componentInstantiations.add(VkxComponentInstantiation(memberDeclaration))
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
                    componentInstantiations,
                    actionBlocks
            )
        }
    }
}