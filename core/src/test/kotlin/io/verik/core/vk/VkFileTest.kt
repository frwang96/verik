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

package io.verik.core.vk

import io.verik.core.LinePos
import io.verik.core.kt.KtRuleParser
import io.verik.core.sv.SvFile
import io.verik.core.sv.SvModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkFileTest {

    @Test
    fun `parse file`() {
        val rule = KtRuleParser.parseKotlinFile("@top class _m: _module")
        val file = VkFile(rule)
        val expectedModule = VkModule(VkModuleElabType.TOP, false, "_m", listOf(), listOf(), listOf(), LinePos(1, 6))
        val expected = VkFile(listOf(expectedModule), expectedModule)
        assertEquals(expected, file)
    }

    @Test
    fun `extract file`() {
        val module = VkModule(VkModuleElabType.TOP, true, "_m", listOf(), listOf(), listOf(), LinePos.ZERO)
        val file = VkFile(listOf(module), module)
        val expected = SvFile(listOf(SvModule("m", listOf(), listOf(), listOf(), listOf(), listOf(), LinePos.ZERO)))
        assertEquals(expected, file.extract())
    }
}
