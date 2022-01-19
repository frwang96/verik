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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ModuleCasterTest : BaseTest() {

    @Test
    fun `cast module from moduleDeclarationAnsi`() {
        driveCasterTest(
            SystemVerilogParser.ModuleDeclarationAnsiContext::class,
            """
                module m;
                endmodule
            """.trimIndent(),
            "Module(m, [])"
        ) {
            it.findDeclaration("m")
        }
    }

    @Test
    @Disabled
    fun `cast module from moduleDeclarationNonAnsi`() {
        driveCasterTest(
            SystemVerilogParser.ModuleDeclarationNonAnsiContext::class,
            """
                module m(x);
                    input x;
                endmodule
            """.trimIndent(),
            "Module(m, [Port(x, Boolean, INPUT)])"
        ) {
            it.findDeclaration("m")
        }
    }
}
