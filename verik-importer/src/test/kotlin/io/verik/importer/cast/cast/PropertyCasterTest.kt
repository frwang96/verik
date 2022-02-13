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

internal class PropertyCasterTest : BaseTest() {

    @Test
    fun `cast property from classProperty`() {
        driveCasterTest(
            SystemVerilogParser.ClassPropertyContext::class,
            """
                class c;
                    static logic x;
                endclass
            """.trimIndent(),
            "Property(x, SimpleDescriptor(Boolean), 1, 1)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast property from dataDeclarationData single`() {
        driveCasterTest(
            SystemVerilogParser.DataDeclarationDataContext::class,
            """
                const logic x;
            """.trimIndent(),
            "Property(x, SimpleDescriptor(Boolean), 0, 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast property from dataDeclarationData multiple`() {
        driveCasterTest(
            SystemVerilogParser.DataDeclarationDataContext::class,
            """
                logic x, y;
            """.trimIndent(),
            """
                Project([
                    Property(x, SimpleDescriptor(Boolean), 0, 1),
                    Property(y, SimpleDescriptor(Boolean), 0, 1)
                ])
            """.trimIndent()
        ) { it }
    }
}
