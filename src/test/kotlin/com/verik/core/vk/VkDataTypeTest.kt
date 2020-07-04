package com.verik.core.vk

import com.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkDataTypeTest {

    @Test
    fun `bool type`() {
        val rule = KtRuleParser.parseExpression("_bool()")
        val type = VkDataType(rule)
        assertEquals(VkBoolType, type)
    }

    @Test
    fun `bool type illegal`() {
        val rule = KtRuleParser.parseExpression("_bool(1)")
        assertThrows<VkParseException> {
            VkDataType(rule)
        }
    }

    @Test
    fun `sint type`() {
        val rule = KtRuleParser.parseExpression("_sint(1)")
        val type = VkDataType(rule)
        assertEquals(VkSintType(1), type)
    }

    @Test
    fun `uint type`() {
        val rule = KtRuleParser.parseExpression("_uint(1)")
        val type = VkDataType(rule)
        assertEquals(VkUintType(1), type)
    }

    @Test
    fun `uint type illegal parameter`() {
        val rule = KtRuleParser.parseExpression("_uint(0)")
        assertThrows<VkParseException> {
            VkDataType(rule)
        }
    }

    @Test
    fun `uint type no parameter`() {
        val rule = KtRuleParser.parseExpression("_uint()")
        assertThrows<VkParseException> {
            VkDataType(rule)
        }
    }
}