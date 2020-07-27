package com.verik.core.kt

import com.verik.core.LinePosException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class KtRuleParserTest {

    @Test
    fun `package valid`() {
        KtRuleParser.parseKotlinFile("package com")
    }

    @Test
    fun `import valid`() {
        KtRuleParser.parseKotlinFile("import com")
    }

    @Test
    fun `assignment valid`() {
        KtRuleParser.parseKotlinFile("val x = \"x\"")
    }

    @Test
    fun `function valid`() {
        KtRuleParser.parseKotlinFile("fun f(x: Int, y: Int) = x + y")
        KtRuleParser.parseKotlinFile("""
            fun f(x: Int, y: Int): Int {
                return x + y
            }
        """.trimIndent())
    }

    @Test
    fun `class valid`() {
        KtRuleParser.parseKotlinFile("""
            class c(val x: Int = 0): Any() {
                fun add(y: Int): Int {
                    return x + y
                }
            }
        """.trimIndent())
    }

    @Test
    fun `enum valid`() {
        KtRuleParser.parseKotlinFile("""
            enum class _bool {
                FALSE, TRUE;
            } fun _bool() = _bool.values()[0]
        """.trimIndent())
    }

    @Test
    fun `syntax error`() {
        assertThrows<LinePosException> { KtRuleParser.parseKotlinFile("x") }
    }

    @Test
    fun `syntax illegal unicode`() {
        val exception = assertThrows<LinePosException>("dsfsd") { KtRuleParser.parseKotlinFile("val x = \"αβγ\"") }
        assertEquals("(1, 10) only ASCII characters are permitted", exception.message)
    }

    @Test
    fun `rule unsupported`() {
        val exception = assertThrows<LinePosException>("") { KtRuleParser.parseKotlinFile("#!\n") }
        assertEquals("(1, 1) lexer token type \"ShebangLine\" is not supported", exception.message)
    }

    @Test
    fun `token unsupported`() {
        val exception = assertThrows<LinePosException>("") { KtRuleParser.parseKotlinFile("""
            fun f(x: String) {
                try {
                    print(x)
                } catch (e: Exception) {}
            }
        """.trimIndent()) }
        assertEquals("(2, 5) lexer token type \"TRY\" is not supported", exception.message)
    }
}