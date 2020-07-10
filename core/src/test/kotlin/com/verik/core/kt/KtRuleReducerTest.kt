package com.verik.core.kt

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class KtRuleReducerTest {

    @Test
    fun `primary constructor reduce`() {
        assertThrows<KtParseException> {
            KtRuleParser.parseKotlinFile("""
                class c constructor(val x: Int)
            """.trimIndent())
        }
    }

    @Test
    fun `unary prefix annotation reduce`() {
        assertThrows<KtParseException> {
            KtRuleParser.parseKotlinFile("""
                val x = @input 0
            """.trimIndent())
        }
    }

    @Test
    fun `function modifier reduce`() {
        assertThrows<KtParseException> {
            KtRuleParser.parseKotlinFile("""
                inline fun f() {}
            """.trimIndent())
        }
    }
}

