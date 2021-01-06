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

import verikc.base.ast.AnnotationProperty
import verikc.base.ast.LineException
import verikc.rs.ast.RsDeclaration
import verikc.rs.ast.RsProperty
import verikc.vk.ast.VkExpression
import verikc.vk.ast.VkPrimaryProperty

object VkBuilderPrimaryProperty {

    fun match(declaration: RsDeclaration): Boolean {
        return declaration is RsProperty && declaration.annotations.none {
            it in listOf(
                AnnotationProperty.INPUT,
                AnnotationProperty.OUTPUT,
                AnnotationProperty.INOUT,
                AnnotationProperty.BUS,
                AnnotationProperty.BUSPORT,
                AnnotationProperty.MAKE
            )
        }
    }

    fun build(declaration: RsDeclaration): VkPrimaryProperty {
        val property = declaration.let {
            if (it is RsProperty) it
            else throw LineException("primary property declaration expected", it.line)
        }
        if (property.annotations.isNotEmpty()) {
            throw LineException("property annotations are not supported here", property.line)
        }

        val typeSymbol = property.typeSymbol
            ?: throw LineException("primary property has not been assigned a type", property.line)

        return VkPrimaryProperty(
            property.line,
            property.identifier,
            property.symbol,
            typeSymbol,
            property.expression?.let { VkExpression(it) }
        )
    }
}
