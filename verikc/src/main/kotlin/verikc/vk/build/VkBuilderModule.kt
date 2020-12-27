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

package verikc.vk.build

import verikc.base.ast.LineException
import verikc.kt.ast.*
import verikc.lang.LangSymbol
import verikc.vk.ast.*

object VkBuilderModule {

    fun match(declaration: KtDeclaration): Boolean {
        return declaration is KtType
                && declaration.typeParent.typeSymbol == LangSymbol.TYPE_MODULE
    }

    fun build(declaration: KtDeclaration): VkModule {
        val type = declaration.let {
            if (it is KtType) it
            else throw LineException("type declaration expected", it.line)
        }

        if (type.typeParent.typeSymbol != LangSymbol.TYPE_MODULE) {
            throw LineException("expected type to inherit from module", type.line)
        }

        val isTop = KtAnnotationType.TOP in type.annotations

        val ports = ArrayList<VkPort>()
        val primaryProperties = ArrayList<VkPrimaryProperty>()
        val componentInstances = ArrayList<VkComponentInstance>()
        val actionBlocks = ArrayList<VkActionBlock>()
        for (memberDeclaration in type.declarations) {
            when {
                VkBuilderPort.match(memberDeclaration) -> {
                    ports.add(VkBuilderPort.build(memberDeclaration))
                }
                VkBuilderPrimaryProperty.match(memberDeclaration) -> {
                    primaryProperties.add(VkBuilderPrimaryProperty.build(memberDeclaration))
                }
                VkBuilderComponentInstance.match(memberDeclaration) -> {
                    componentInstances.add(VkBuilderComponentInstance.build(memberDeclaration))
                }
                VkBuilderActionBlock.match(memberDeclaration) -> {
                    actionBlocks.add(VkBuilderActionBlock.build(memberDeclaration))
                }
                memberDeclaration is KtFunction && memberDeclaration.type == KtFunctionType.TYPE_CONSTRUCTOR -> {}
                else -> throw LineException(
                    "unable to identify declaration ${memberDeclaration.identifier}",
                    memberDeclaration.line
                )
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
            actionBlocks
        )
    }
}
