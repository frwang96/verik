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

package io.verik.importer.cast.common

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.test.BaseTest
import io.verik.importer.test.findDeclaration
import org.junit.jupiter.api.Test

class SignatureBuilderTest : BaseTest() {

    @Test
    fun `signature moduleDeclarationNonAnsi`() {
        driveSignatureTest(
            SystemVerilogParser.ModuleDeclarationNonAnsiContext::class,
            """
                module m(x, y);
                    input x;
                    output y;
                    logic z;
                endmodule
            """.trimIndent(),
            """
                module m(
                    x,
                    y
                );
                    input x;
                    output y;
                endmodule
            """.trimIndent()
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `signature moduleDeclarationAnsi`() {
        driveSignatureTest(
            SystemVerilogParser.ModuleDeclarationAnsiContext::class,
            """
                module m(input x, output y);
                    logic z;
                endmodule
            """.trimIndent(),
            """
                module m(
                    input x,
                    output y
                );
                endmodule
            """.trimIndent()
        ) { it.findDeclaration("m") }
    }

    @Test
    fun `signature dataDeclarationData`() {
        driveSignatureTest(
            SystemVerilogParser.DataDeclarationDataContext::class,
            """
                const var automatic logic x;
            """.trimIndent(),
            "const var automatic logic x;"
        ) { it.findDeclaration("x") }
    }

    @Test
    fun `signature dataTypeVector`() {
        driveSignatureTest(
            SystemVerilogParser.DataTypeVectorContext::class,
            """
                logic [3:0][1:0] x;
            """.trimIndent(),
            "logic [3:0][1:0] x;"
        ) { it.findDeclaration("x") }
    }
}
