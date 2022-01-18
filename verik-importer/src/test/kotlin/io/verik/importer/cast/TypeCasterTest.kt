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

package io.verik.importer.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

internal class TypeCasterTest : BaseTest() {

    @Test
    fun `cast type from dataType`() {
        driveCasterTest(
            SystemVerilogParser.DataTypeContext::class,
            """
                logic x;
            """.trimIndent(),
            """
                Property(x, Boolean)
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast type from packedDimension`() {
        driveCasterTest(
            SystemVerilogParser.PackedDimensionContext::class,
            """
                logic [7:0] x;
            """.trimIndent(),
            """
                Property(x, Ubit<`8`>)
            """.trimIndent()
        ) { it.findDeclaration("x") }
    }
}
