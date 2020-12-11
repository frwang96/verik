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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.Line
import verikc.base.ast.PortType
import verikc.ps.PsUtil
import verikc.sv.ast.SvExtractedType
import verikc.sv.ast.SvPort

internal class PsPortTest {

    @Test
    fun `extract port bool`() {
        val string = "@input val x = _bool()"
        val expected = SvPort(
                Line(1),
                PortType.INPUT,
                SvExtractedType("logic", "", ""),
                "x"
        )
        assertEquals(expected, PsUtil.extractPort(string))
    }

    @Test
    fun `extract port ubit`() {
        val string = "@input val x = _ubit(8)"
        val expected = SvPort(
                Line(1),
                PortType.INPUT,
                SvExtractedType("logic", "[7:0]", ""),
                "x"
        )
        assertEquals(expected, PsUtil.extractPort(string))
    }
}
