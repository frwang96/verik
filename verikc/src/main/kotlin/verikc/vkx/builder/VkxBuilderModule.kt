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

package verikc.vkx.builder

import verikc.base.ast.AnnotationType
import verikc.base.ast.LineException
import verikc.gex.ast.GexDeclaration
import verikc.gex.ast.GexType
import verikc.lang.LangSymbol.TYPE_MODULE
import verikc.vkx.ast.*

object VkxBuilderModule {

    fun match(declaration: GexDeclaration): Boolean {
        return declaration is GexType
                && declaration.typeParent.typeSymbol == TYPE_MODULE
    }

    fun build(declaration: GexDeclaration): VkxModule {
        val type = declaration.let {
            if (it is GexType) it
            else throw LineException("type declaration expected", it.line)
        }

        if (type.typeParent.typeSymbol != TYPE_MODULE)
            throw LineException("expected type to inherit from module", type.line)

        val isTop = AnnotationType.TOP in type.annotations

        val ports = ArrayList<VkxPort>()
        val primaryProperties = ArrayList<VkxPrimaryProperty>()
        val componentInstances = ArrayList<VkxComponentInstance>()
        type.properties.forEach {
            when {
                VkxBuilderPort.match(it) -> ports.add(VkxBuilderPort.build(it))
                VkxBuilderPrimaryProperty.match(it) -> primaryProperties.add(VkxBuilderPrimaryProperty.build(it))
                VkxBuilderComponentInstance.match(it) -> componentInstances.add(VkxBuilderComponentInstance.build(it))
                else -> throw LineException("unable to identify property ${it.identifier}", it.line)
            }
        }

        val actionBlocks = ArrayList<VkxActionBlock>()
        val methodBlocks = ArrayList<VkxMethodBlock>()
        type.functions.forEach {
            when {
                VkxBuilderActionBlock.match(it) -> actionBlocks.add(VkxBuilderActionBlock.build(it))
                VkxBuilderMethodBlock.match(it) -> methodBlocks.add(VkxBuilderMethodBlock.build(it))
                else -> throw LineException("unable to identify function ${it.identifier}", it.line)
            }
        }

        return VkxModule(
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
