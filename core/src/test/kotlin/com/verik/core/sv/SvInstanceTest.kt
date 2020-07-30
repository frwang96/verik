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

package com.verik.core.sv

import com.verik.core.LinePos
import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Test

internal class SvInstanceTest {

    @Test
    fun boolean() {
        val port = SvInstance(SvInstanceUsageType.REGULAR, listOf(), "x", listOf(), LinePos.ZERO)
        val expected = SvAlignerLine(listOf("", "logic", "", "x", ""), LinePos.ZERO)
        assertStringEquals(expected, port.build())
    }

    @Test
    fun `boolean input`() {
        val port = SvInstance(SvInstanceUsageType.INPUT, listOf(), "x", listOf(), LinePos.ZERO)
        val expected = SvAlignerLine(listOf("input", "logic", "", "x", ""), LinePos.ZERO)
        assertStringEquals(expected, port.build())
    }

    @Test
    fun `byte output`() {
        val port = SvInstance(SvInstanceUsageType.INPUT, listOf(SvRange(7, 0)), "x", listOf(), LinePos.ZERO)
        val expected = SvAlignerLine(listOf("input", "logic", "[7:0]", "x", ""), LinePos.ZERO)
        assertStringEquals(expected, port.build())
    }
}