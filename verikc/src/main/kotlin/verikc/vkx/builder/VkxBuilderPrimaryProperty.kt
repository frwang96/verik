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

import verikc.base.ast.AnnotationProperty
import verikc.base.ast.LineException
import verikc.gex.ast.GexDeclaration
import verikc.gex.ast.GexProperty
import verikc.vkx.ast.VkxExpression
import verikc.vkx.ast.VkxPrimaryProperty

object VkxBuilderPrimaryProperty {

    fun match(declaration: GexDeclaration): Boolean {
        return declaration is GexProperty && declaration.annotations.none {
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

    fun build(property: GexProperty): VkxPrimaryProperty {
        if (property.annotations.isNotEmpty())
            throw LineException("property annotations are not supported here", property.line)

        return VkxPrimaryProperty(
            property.line,
            property.identifier,
            property.symbol,
            property.typeSymbol,
            property.expression?.let { VkxExpression(it) }
        )
    }
}
