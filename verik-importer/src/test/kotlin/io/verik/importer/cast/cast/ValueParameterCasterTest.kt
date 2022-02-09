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

internal class ValueParameterCasterTest : BaseTest() {

    @Test
    fun `cast value parameter from tfPortItem`() {
        driveCasterTest(
            SystemVerilogParser.TfPortItemContext::class,
            """
                function void f(int x);
                endfunction
            """.trimIndent(),
            "SvValueParameter(x, SimpleDescriptor(Int), 0)"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast value parameter from tfPortDeclaration multiple`() {
        driveCasterTest(
            SystemVerilogParser.TfPortDeclarationContext::class,
            """
                function void f;
                    input x, y = 1'b1;
                endfunction
            """.trimIndent(),
            """
                SvFunction(
                    f, [
                        SvValueParameter(x, SimpleDescriptor(Boolean), 0),
                        SvValueParameter(y, SimpleDescriptor(Boolean), 1)
                    ], SimpleDescriptor(Unit), 0
                )
            """.trimIndent()
        ) { it.findDeclaration("f") }
    }
}
