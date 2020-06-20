package com.verik.core.kt

import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class KtTreeTest {

    @Nested
    inner class Constructor {
        @Test
        fun `valid package`() {
            KtTree.parseKotlinFile("package com")
        }
        @Test
        fun `valid import`() {
            KtTree.parseKotlinFile("import com")
        }
        @Test
        fun `valid assignment`() {
            KtTree.parseKotlinFile("const val x = \"x\"")
        }
        @Test
        fun `valid function`() {
            KtTree.parseKotlinFile("fun f(x: Int, y: Int) = x + y")
            KtTree.parseKotlinFile("""
                fun f(x: Int, y: Int): Int {
                    return x + y
                }
            """.trimIndent())
        }
        @Test
        fun `valid class`() {
            KtTree.parseKotlinFile("""
                class c(val x: Int = 0): Any() {
                    fun add() {
                        x++
                    }
                }
            """.trimIndent())
        }

        @Test
        fun `syntax error`() {
            assertThrows<KtAntlrException> { KtTree.parseKotlinFile("x") }
        }
        @Test
        fun `unsupported rule`() {
            assertThrows<KtParseException> { KtTree.parseKotlinFile("#!\n") }
        }
        @Test
        fun `unsupported token`() {
            assertThrows<KtParseException> { KtTree.parseKotlinFile("inline fun f()") }
        }
    }

    @Nested
    inner class TreeUtils {
        @Test
        fun `count rules`() {
            val tree = KtTree.parseKotlinFile("val x = 0")
            assertEquals(26, tree.countRuleNodes())
        }
        @Test
        fun `count tokens`() {
            val tree = KtTree.parseKotlinFile("val x = 0")
            assertEquals(6, tree.countTokenNodes())
        }
        @Test
        fun `to string`() {
            val tree = KtTree.parseKotlinFile("")
            val expected = """
                KOTLIN_FILE
                ├─ PACKAGE_HEADER
                ├─ IMPORT_LIST
                └─ EOF <EOF>
            """.trimIndent()
            assertStringEquals(expected, tree)
        }
    }
}