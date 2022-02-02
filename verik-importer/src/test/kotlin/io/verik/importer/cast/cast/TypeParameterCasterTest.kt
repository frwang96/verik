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

internal class TypeParameterCasterTest : BaseTest() {

    @Test
    fun `cast typeParameter from parameterPortDeclarationType`() {
        driveCasterTest(
            SystemVerilogParser.ParameterPortDeclarationTypeContext::class,
            """
                class c #(type T = int);
                endclass
            """.trimIndent(),
            "SvClass(c, [], [TypeParameter(T, 0)], SimpleDescriptor(Any))"
        ) { it.findDeclaration("c") }
    }

    @Test
    fun `cast typeParameter from parameterPortDeclarationType multiple`() {
        driveCasterTest(
            SystemVerilogParser.ParameterPortDeclarationTypeContext::class,
            """
                module m #(type T = int, type U = int);
                endmodule
            """.trimIndent(),
            "Module(m, [], [TypeParameter(T, 0), TypeParameter(U, 0)], [])"
        ) { it.findDeclaration("m") }
    }
}
