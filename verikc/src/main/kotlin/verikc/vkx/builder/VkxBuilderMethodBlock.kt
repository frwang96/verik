/*
 * Copyright (c) 2021 Francis Wang
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

import verikc.base.ast.AnnotationFunction
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.MethodBlockType
import verikc.gex.ast.GexDeclaration
import verikc.gex.ast.GexFunction
import verikc.vkx.ast.VkxMethodBlock
import verikc.vkx.ast.VkxProperty

object VkxBuilderMethodBlock {

    fun match(declaration: GexDeclaration): Boolean {
        return declaration is GexFunction
                && (declaration.annotations.isEmpty() || declaration.annotations.any { it == AnnotationFunction.TASK })
    }

    fun build(function: GexFunction): VkxMethodBlock {
        val methodBlockType = getMethodBlockType(function.annotations, function.line)

        val parameterProperties = function.parameterProperties.map {
            if (it.expression != null)
                throw LineException("optional parameters not supported", function.line)
            VkxProperty(it.line, it.identifier, it.symbol, it.getTypeGenerifiedNotNull())
        }

        return VkxMethodBlock(
            function.line,
            function.identifier,
            function.symbol,
            methodBlockType,
            parameterProperties,
            function.getReturnTypeGenerifiedNotNull(),
            VkxBuilderBlock.build(function.block)
        )
    }

    private fun getMethodBlockType(annotations: List<AnnotationFunction>, line: Line): MethodBlockType {
        return if (annotations.isEmpty()) {
            MethodBlockType.FUNCTION
        } else {
            if (annotations.size > 1 || annotations[0] != AnnotationFunction.TASK)
                throw LineException("illegal method block type", line)
            MethodBlockType.TASK
        }
    }
}