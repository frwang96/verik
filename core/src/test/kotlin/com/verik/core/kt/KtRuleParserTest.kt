package com.verik.core.kt

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class KtRuleParserTest {

    @Test
    fun `valid package`() {
        KtRuleParser.parseKotlinFile("package com")
    }

    @Test
    fun `valid import`() {
        KtRuleParser.parseKotlinFile("import com")
    }

    @Test
    fun `valid assignment`() {
        KtRuleParser.parseKotlinFile("val x = \"x\"")
    }

    @Test
    fun `valid function`() {
        KtRuleParser.parseKotlinFile("fun f(x: Int, y: Int) = x + y")
        KtRuleParser.parseKotlinFile("""
            fun f(x: Int, y: Int): Int {
                return x + y
            }
        """.trimIndent())
    }

    @Test
    fun `valid class`() {
        KtRuleParser.parseKotlinFile("""
            class c(val x: Int = 0): Any() {
                fun add(y: Int): Int {
                    return x + y
                }
            }
        """.trimIndent())
    }

    @Test
    fun `valid enum`() {
        KtRuleParser.parseKotlinFile("""
            enum class _bool {
                FALSE, TRUE;
            } fun _bool() = _bool.values()[0]
        """.trimIndent())
    }

    @Test
    fun `syntax error`() {
        assertThrows<KtAntlrException> { KtRuleParser.parseKotlinFile("x") }
    }

    @Test
    fun `unsupported rule`() {
        assertThrows<KtParseException> { KtRuleParser.parseKotlinFile("#!\n") }
    }

    @Test
    fun `unsupported token`() {
        assertThrows<KtParseException> { KtRuleParser.parseKotlinFile("""
            fun f(x: String) {
                try {
                    print(x)
                } catch (e: Exception) {}
            }
        """.trimIndent()) }
    }

    @Test
    fun `illegal unicode character`() {
        assertThrows<KtParseException> { KtRuleParser.parseKotlinFile("val x = \"αβγ\"") }
    }
}