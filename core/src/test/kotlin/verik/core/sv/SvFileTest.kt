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

package verik.core.sv

import org.junit.jupiter.api.Test
import verik.core.assertStringEquals
import verik.core.main.SourceBuilder

internal class SvFileTest {

    @Test
    fun `module empty`() {
        val file = SvFile(listOf(SvModule(
                0,
                "m",
                listOf(),
                listOf()
        )))
        val expected = """
            module m;
              timeunit 1ns / 1ns;

            endmodule: m
        """.trimIndent()
        val builder = SourceBuilder()
        file.build(builder)
        assertStringEquals(expected, builder)
    }
}