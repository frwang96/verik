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
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.rs.ast.RsProperty
import verikc.vk.ast.VkConnectionType
import verikc.vk.ast.VkPort
import verikc.vk.ast.VkProperty

object VkBuilderPort {

    fun match(property: RsProperty): Boolean {
        return property.annotations.any { it.isPortAnnotation() }
    }

    fun build(property: RsProperty): VkPort {
        val connectionType = getConnectionType(property.annotations, property.line)

        if (property.expression == null)
            throw LineException("port type expression expected", property.line)

        return VkPort(
            VkProperty(
                property.line,
                property.identifier,
                property.symbol,
                property.mutabilityType,
                property.getTypeGenerifiedNotNull()
            ),
            connectionType,
            null
        )
    }


    private fun getConnectionType(annotations: List<AnnotationProperty>, line: Line): VkConnectionType {
        if (annotations.isEmpty())
            throw LineException("connection type annotations expected", line)
        if (annotations.size > 1)
            throw LineException("illegal connection type", line)

        return when (annotations[0]) {
            AnnotationProperty.INPUT -> VkConnectionType.INPUT
            AnnotationProperty.OUTPUT -> VkConnectionType.OUTPUT
            AnnotationProperty.INOUT -> VkConnectionType.INOUT
            else -> throw LineException("illegal connection type", line)
        }
    }
}
