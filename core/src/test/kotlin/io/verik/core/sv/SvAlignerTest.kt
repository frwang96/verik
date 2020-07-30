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

package io.verik.core.sv

import io.verik.core.LinePos
import io.verik.core.SourceBuilder
import io.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

internal class SvAlignerTest {

    @Test
    fun alignment() {
        val lines = listOf(
                SvAlignerLine(listOf("#", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("##", "##"), LinePos.ZERO),
                SvAlignerLine(listOf("###", "###"), LinePos.ZERO)
        )
        val expected = """
            #   #
            ##  ##
            ### ###
        """.trimIndent()
        val builder = SourceBuilder()
        SvAligner.build(lines, "", "", builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun delimiter() {
        val lines = listOf(
                SvAlignerLine(listOf("#", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("#", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("#", "#"), LinePos.ZERO)
        )
        val expected = """
            # #,
            # #,
            # #;
        """.trimIndent()
        val builder = SourceBuilder()
        SvAligner.build(lines, ",", ";", builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `column empty`() {
        val lines = listOf(
                SvAlignerLine(listOf("#", "", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("#", "", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("#", "", "#"), LinePos.ZERO)
        )
        val expected = """
            # #
            # #
            # #
        """.trimIndent()
        val builder = SourceBuilder()
        SvAligner.build(lines, "", "", builder)
        assertStringEquals(expected, builder)
    }

    @Test
    fun `column overflow`() {
        val lines = listOf(
                SvAlignerLine(listOf("#####", "", "", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("###", "", "#", "#"), LinePos.ZERO),
                SvAlignerLine(listOf("#", "#", "#", "#"), LinePos.ZERO)
        )
        val expected = """
            ##### #
            ### # #
            # # # #
        """.trimIndent()
        val builder = SourceBuilder()
        SvAligner.build(lines, "", "", builder)
        assertStringEquals(expected, builder)
    }
}