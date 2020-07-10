package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.SvFile
import com.verik.core.sv.SvModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class VkFileTest {

    @Nested
    inner class Parse {

        @Test
        fun `simple file`() {
            val rule = KtRuleParser.parseKotlinFile("@top class _m: _module")
            val file = VkFile(rule)
            val expectedModule = VkModule(VkModuleElabType.TOP, false, "_m", listOf(), listOf(), listOf(), LinePos(1, 6))
            val expected = VkFile(listOf(expectedModule), expectedModule)
            assertEquals(expected, file)
        }
    }

    @Nested
    inner class Build {

        @Test
        fun `simple file`() {
            val module = VkModule(VkModuleElabType.TOP, true, "_m", listOf(), listOf(), listOf(), LinePos.ZERO)
            val file = VkFile(listOf(module), module)
            val expected = SvFile(listOf(SvModule("m", listOf(), listOf(), listOf(), listOf(), listOf(), LinePos.ZERO)))
            assertEquals(expected, file.extract())
        }
    }
}
