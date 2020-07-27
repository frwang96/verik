package com.verik.core.kt

import com.verik.core.LinePosException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class KtRuleReducerTest {

    @Test
    fun `primary constructor reduce`() {
        val exception = assertThrows<LinePosException> {
            KtRuleParser.parseKotlinFile("""
                class c constructor(val x: Int)
            """.trimIndent())
        }
        assertEquals("(1, 9) \"constructor\" keyword is not permitted in primary constructor", exception.message)
    }

    @Test
    fun `unary prefix annotation reduce`() {
        val exception = assertThrows<LinePosException> {
            KtRuleParser.parseKotlinFile("""
                val x = @input 0
            """.trimIndent())
        }
        assertEquals("(1, 8) annotations are not permitted here", exception.message)
    }

    @Test
    fun `function modifier reduce`() {
        val exception = assertThrows<LinePosException> {
            KtRuleParser.parseKotlinFile("""
                inline fun f() {}
            """.trimIndent())
        }
        assertEquals("(1, 1) parser rule type \"functionModifier\" is not supported", exception.message)
    }
}

