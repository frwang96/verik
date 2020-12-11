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
import verikc.base.ast.ActionBlockType
import verikc.base.ast.Line
import verikc.ps.PsUtil
import verikc.sv.ast.SvActionBlock
import verikc.sv.ast.SvBlock

internal class PsActionBlockTest {

    @Test
    fun `run simple`() {
        val string = "@run fun f() {}"
        val expected = SvActionBlock(
            Line(1),
            ActionBlockType.RUN,
            listOf(),
            SvBlock(Line(1), listOf())
        )
        assertEquals(expected, PsUtil.extractActionBlock(string))
    }
}
