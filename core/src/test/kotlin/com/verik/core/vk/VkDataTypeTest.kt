package com.verik.core.vk

import com.verik.core.kt.KtRuleParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkDataTypeTest {

    @Test
    fun `bool type`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_bool()"))
        val type = VkDataType(expression)
        assertEquals(VkBoolType, type)
    }

    @Test
    fun `bool type illegal`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_bool(1)"))
        assertThrows<VkParseException> {
            VkDataType(expression)
        }
    }

    @Test
    fun `sint type`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_sint(1)"))
        val type = VkDataType(expression)
        assertEquals(VkSintType(1), type)
    }

    @Test
    fun `uint type`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_uint(1)"))
        val type = VkDataType(expression)
        assertEquals(VkUintType(1), type)
    }

    @Test
    fun `uint type illegal parameter`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_uint(0)"))
        assertThrows<VkParseException> {
            VkDataType(expression)
        }
    }

    @Test
    fun `uint type no parameter`() {
        val expression = VkExpression(KtRuleParser.parseExpression("_uint()"))
        assertThrows<VkParseException> {
            VkDataType(expression)
        }
    }
}