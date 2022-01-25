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
    fun `cast valueParameter from tfPortItem`() {
        driveCasterTest(
            SystemVerilogParser.TfPortItemContext::class,
            """
                function void f(int x);
                endfunction
            """.trimIndent(),
            "ValueParameter(x, SimpleDescriptor(Int))"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `cast valueParameter from tfPortDeclaration multiple`() {
        driveCasterTest(
            SystemVerilogParser.TfPortDeclarationContext::class,
            """
                function void f;
                    input x, y;
                endfunction
            """.trimIndent(),
            """
                Function(
                    f, [
                        ValueParameter(x, SimpleDescriptor(Boolean)),
                        ValueParameter(y, SimpleDescriptor(Boolean))
                    ], SimpleDescriptor(Unit)
                )
            """.trimIndent()
        ) { it.findDeclaration("f") }
    }
}
