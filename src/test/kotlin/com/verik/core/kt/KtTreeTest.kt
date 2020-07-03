package com.verik.core.kt

import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Assertions.assertEquals
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
            KtTree.parseKotlinFile("val x = \"x\"")
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
                    fun add(y: Int): Int {
                        return x + y
                    }
                }
            """.trimIndent())
        }

        @Test
        fun `valid enum`() {
            KtTree.parseKotlinFile("""
                enum class _bool {
                    FALSE, TRUE;
                } fun _bool() = _bool.values()[0]
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
            assertThrows<KtParseException> { KtTree.parseKotlinFile("""
                fun f(x: String) {
                    try {
                        print(x)
                    } catch (e: Exception) {}
                }
            """.trimIndent()) }
        }

        @Test
        fun `illegal unicode character`() {
            assertThrows<KtParseException> { KtTree.parseKotlinFile("val x = \"αβγ\"") }
        }
    }

    @Nested
    inner class TreeUtils {

        @Test
        fun `count rules`() {
            val tree = KtTree.parseKotlinFile("val x = 0")
            assertEquals(25, tree.countRuleNodes())
        }

        @Test
        fun `count tokens`() {
            val tree = KtTree.parseKotlinFile("val x = 0")
            assertEquals(3, tree.countTokenNodes())
        }

        @Test
        fun `to string`() {
            val tree = KtTree.parseKotlinFile("")
            val expected = """
                KOTLIN_FILE
                ├─ PACKAGE_HEADER
                └─ IMPORT_LIST
            """.trimIndent()
            assertStringEquals(expected, tree)
        }
    }
}