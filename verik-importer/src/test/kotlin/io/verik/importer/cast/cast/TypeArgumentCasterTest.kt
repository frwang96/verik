/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class TypeArgumentCasterTest : BaseTest() {

    @Test
    fun `cast type argument from paramExpressionDataType`() {
        driveCasterTest(
            SystemVerilogParser.ParamExpressionDataTypeContext::class,
            """
                c#(int) x;
            """.trimIndent(),
            """
                Property(
                    x, ReferenceDescriptor(Nothing, c, Nothing, [TypeArgument(null, SimpleDescriptor(Int))]), 0, 1
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast type argument from paramExpressionExpression`() {
        driveCasterTest(
            SystemVerilogParser.ParamExpressionExpressionContext::class,
            """
                c#(1) x;
            """.trimIndent(),
            """
                Property(
                    x, ReferenceDescriptor(Nothing, c, Nothing, [TypeArgument(null, LiteralDescriptor(*))]), 0, 1
                )
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }
}
