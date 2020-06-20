package com.verik.core

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

// Copyright (c) 2020 Francis Wang

internal class AntlrTreeTest {

    @Nested
    inner class ParseKolinFile {
        @Test
        fun `valid input`() {
            AntlrTree.parseKotlinFile("val x = 0")
        }
        @Test
        fun `invalid input`() {
            org.junit.jupiter.api.assertThrows<AntlrTreeParseException> { AntlrTree.parseKotlinFile("x") }
        }
    }

    @Nested
    inner class TreeUtils {
        @Test
        fun `count rules`() {
            val tree = AntlrTree.parseKotlinFile("val x = 0")
            assertEquals(26, tree.countRuleNodes())
        }
        @Test
        fun `count terminals`() {
            val tree = AntlrTree.parseKotlinFile("val x = 0")
            assertEquals(6, tree.countTerminalNodes())
        }
        @Test
        fun `to string`() {
            val tree = AntlrTree.parseKotlinFile("")
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