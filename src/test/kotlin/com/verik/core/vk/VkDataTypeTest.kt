package com.verik.core.vk

import com.verik.core.kt.KtTree
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkDataTypeTest {

    @Nested
    inner class Parse {
        @Test
        fun `bool type`() {
            val tree = KtTree.parseExpression("_bool()")
            val type = VkDataType(tree)
            assertEquals(type, VkBoolType)
        }
        @Test
        fun `bool type illegal`() {
            val tree = KtTree.parseExpression("_bool(1)")
            assertThrows<VkParseException> {
                VkDataType(tree)
            }
        }
        @Test
        fun `sint type`() {
            val tree = KtTree.parseExpression("_sint(1)")
            val type = VkDataType(tree)
            assertEquals(type, VkSintType(1))
        }
        @Test
        fun `uint type`() {
            val tree = KtTree.parseExpression("_uint(1)")
            val type = VkDataType(tree)
            assertEquals(type, VkUintType(1))
        }
        @Test
        fun `uint type illegal parameter`() {
            val tree = KtTree.parseExpression("_uint(0)")
            assertThrows<VkParseException> {
                VkDataType(tree)
            }
        }
        @Test
        fun `uint type no parameter`() {
            val tree = KtTree.parseExpression("_uint()")
            assertThrows<VkParseException> {
                VkDataType(tree)
            }
        }
    }
}