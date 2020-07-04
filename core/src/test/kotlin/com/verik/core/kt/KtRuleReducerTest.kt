package com.verik.core.kt

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class KtRuleReducerTest {

    @Test
    fun `reduces primary constructor`() {
        assertThrows<KtParseException> {
            KtRuleParser.parseKotlinFile("""
                class c constructor(val x: Int)
            """.trimIndent())
        }
    }

    @Test
    fun `reduces unary prefix annotation`() {
        assertThrows<KtParseException> {
            KtRuleParser.parseKotlinFile("""
                val x = @input 0
            """.trimIndent())
        }
    }

    @Test
    fun `reduces function modifier`() {
        assertThrows<KtParseException> {
            KtRuleParser.parseKotlinFile("""
                inline fun f() {}
            """.trimIndent())
        }
    }
}

