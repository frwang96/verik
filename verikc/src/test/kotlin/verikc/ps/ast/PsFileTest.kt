/*
 * Copyright 2020 Francis Wang
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

package verikc.ps.ast

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import verikc.line
import verikc.ps.PsUtil
import verikc.sv.ast.*

internal class PsFileTest {

    @Test
    fun `extract module file`() {
        val string = """
            package test
            class _m: _module
        """.trimIndent()
        val expected = SvFile(
            listOf(
                SvModule(
                    line(2),
                    "m",
                    listOf(),
                    listOf(),
                    listOf(),
                    listOf()
                )
            )
        )
        Assertions.assertEquals(expected, PsUtil.extractModuleFile(string))
    }

    @Test
    fun `extract package file`() {
        val string = """
            package test
            enum class _e(override val value: _ubit = enum_sequential()): _enum { E }
        """.trimIndent()
        val expected = SvFile(
            listOf(
                SvEnum(
                    line(2),
                    "e",
                    listOf(
                        SvEnumProperty(
                            line(2),
                            "E_E",
                            SvExpressionLiteral(line(2), "1'h0")
                        )
                    ),
                    1
                )
            )
        )
        Assertions.assertEquals(expected, PsUtil.extractPkgFile(string))
    }
}
