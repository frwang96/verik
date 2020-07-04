package com.verik.core.kt

import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class KtNodeTest {

    @Test
    fun `count rules`() {
        val rule = KtRuleParser.parseKotlinFile("val x = 0")
        assertEquals(25, rule.countRuleNodes())
    }

    @Test
    fun `count tokens`() {
        val rule = KtRuleParser.parseKotlinFile("val x = 0")
        assertEquals(3, rule.countTokenNodes())
    }

    @Test
    fun `to string`() {
        val rule = KtRuleParser.parseKotlinFile("")
        val expected = """
            KOTLIN_FILE
            ├─ PACKAGE_HEADER
            └─ IMPORT_LIST
        """.trimIndent()
        assertStringEquals(expected, rule)
    }
}