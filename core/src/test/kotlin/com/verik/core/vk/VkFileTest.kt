package com.verik.core.vk

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
            val rule = KtRuleParser.parseKotlinFile("class _m: _module")
            val file = VkFile(rule)
            assertEquals(VkModule(VkModuleElabType.REGULAR, false, "_m", listOf(), listOf()), file.module)
        }
    }

    @Nested
    inner class Build {

        @Test
        fun `simple file`() {
            val module = VkModule(VkModuleElabType.REGULAR, true, "_m", listOf(), listOf())
            val file = VkFile(module)
            val expected = SvFile(SvModule("m", listOf(), listOf()))
            assertEquals(expected, file.extract())
        }
    }
}
