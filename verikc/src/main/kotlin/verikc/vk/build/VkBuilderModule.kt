/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.vk.build

import verikc.base.ast.AnnotationType
import verikc.base.ast.LineException
import verikc.kt.ast.KtDeclaration
import verikc.kt.ast.KtType
import verikc.lang.LangSymbol.TYPE_MODULE
import verikc.vk.ast.*

object VkBuilderModule {

    fun match(declaration: KtDeclaration): Boolean {
        return declaration is KtType
                && declaration.typeParent.typeSymbol == TYPE_MODULE
    }

    fun build(declaration: KtDeclaration): VkModule {
        val type = declaration.let {
            if (it is KtType) it
            else throw LineException("type declaration expected", it.line)
        }

        if (type.typeParent.typeSymbol != TYPE_MODULE) {
            throw LineException("expected type to inherit from module", type.line)
        }

        val isTop = AnnotationType.TOP in type.annotations

        val ports = ArrayList<VkPort>()
        val primaryProperties = ArrayList<VkPrimaryProperty>()
        val componentInstances = ArrayList<VkComponentInstance>()
        type.properties.forEach {
            when {
                VkBuilderPort.match(it) -> ports.add(VkBuilderPort.build(it))
                VkBuilderPrimaryProperty.match(it) -> primaryProperties.add(VkBuilderPrimaryProperty.build(it))
                VkBuilderComponentInstance.match(it) -> componentInstances.add(VkBuilderComponentInstance.build(it))
                else -> throw LineException("unable to identify property ${it.identifier}", it.line)
            }
        }

        val actionBlocks = ArrayList<VkActionBlock>()
        val methodBlocks = ArrayList<VkMethodBlock>()
        type.functions.forEach {
            when {
                VkBuilderActionBlock.match(it) -> actionBlocks.add(VkBuilderActionBlock.build(it))
                VkBuilderMethodBlock.match(it) -> methodBlocks.add(VkBuilderMethodBlock.build(it))
                else -> throw LineException("unable to identify function ${it.identifier}", it.line)
            }
        }

        return VkModule(
            type.line,
            type.identifier,
            type.symbol,
            ports,
            isTop,
            primaryProperties,
            componentInstances,
            actionBlocks,
            methodBlocks
        )
    }
}
