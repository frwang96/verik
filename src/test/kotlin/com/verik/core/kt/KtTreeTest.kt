package com.verik.core.kt

import com.verik.core.assertStringEquals
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class KtTreeTest {

    @Nested
    @Disabled
    inner class ParseKotlinFile {
        @Test
        fun `valid input`() {
            KtTree.parseKotlinFile("val x = 0")
        }
        @Test
        fun `invalid input`() {
            assertThrows<KtAntlrException> { KtTree.parseKotlinFile("x") }
        }
    }

    @Nested
    @Disabled
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
        @Disabled
        fun `to string`() {
            val tree = KtTree.parseKotlinFile("")
            val expected = """
                kotlinFile
                ├─ packageHeader
                ├─ importList
                └─ EOF <EOF>
            """.trimIndent()
            assertStringEquals(expected, tree)
        }
    }
}