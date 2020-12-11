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

package verikc.sv.build

import org.junit.jupiter.api.Test
import verikc.assertStringEquals
import verikc.base.ast.Line

internal class SvAlignedBlockTest {

    @Test
    fun alignment() {
        val lines = listOf(
            SvAlignedLine(Line(0), listOf("#", "#")),
            SvAlignedLine(Line(0), listOf("##", "##")),
            SvAlignedLine(Line(0), listOf("###", "###"))
        )
        val block = SvAlignedBlock(lines, "", "")
        val expected = """
            #   #
            ##  ##
            ### ###
        """.trimIndent()
        val builder = SvSourceBuilder()
        block.build(builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun delimiter() {
        val lines = listOf(
            SvAlignedLine(Line(0), listOf("#", "#")),
            SvAlignedLine(Line(0), listOf("#", "#")),
            SvAlignedLine(Line(0), listOf("#", "#"))
        )
        val block = SvAlignedBlock(lines, ",", ";")
        val expected = """
            # #,
            # #,
            # #;
        """.trimIndent()
        val builder = SvSourceBuilder()
        block.build(builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `column empty`() {
        val lines = listOf(
            SvAlignedLine(Line(0), listOf("#", "", "#")),
            SvAlignedLine(Line(0), listOf("#", "", "#")),
            SvAlignedLine(Line(0), listOf("#", "", "#"))
        )
        val block = SvAlignedBlock(lines, "", "")
        val expected = """
            # #
            # #
            # #
        """.trimIndent()
        val builder = SvSourceBuilder()
        block.build(builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `column overflow`() {
        val lines = listOf(
            SvAlignedLine(Line(0), listOf("#####", "", "", "#")),
            SvAlignedLine(Line(0), listOf("###", "", "#", "#")),
            SvAlignedLine(Line(0), listOf("#", "#", "#", "#"))
        )
        val block = SvAlignedBlock(lines, "", "")
        val expected = """
            ##### #
            ### # #
            # # # #
        """.trimIndent()
        val builder = SvSourceBuilder()
        block.build(builder)
        assertStringEquals(expected, builder)
    }
}
