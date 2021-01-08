/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.sv.extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import verikc.base.ast.PortType
import verikc.line
import verikc.sv.SvExtractUtil
import verikc.sv.ast.SvPort
import verikc.sv.ast.SvTypeExtracted

internal class SvExtractorPortTest {

    @Test
    fun `port bool`() {
        val string = "@input val x = _bool()"
        val expected = SvPort(
            line(4),
            "x",
            PortType.INPUT,
            SvTypeExtracted("logic", "", "")
        )
        assertEquals(
            expected,
            SvExtractUtil.extractModulePort("", string)
        )
    }

    @Test
    fun `port ubit`() {
        val string = "@input val x = _ubit(8)"
        val expected = SvPort(
            line(4),
            "x",
            PortType.INPUT,
            SvTypeExtracted("logic", "[7:0]", "")
        )
        assertEquals(
            expected,
            SvExtractUtil.extractModulePort("", string)
        )
    }
}
