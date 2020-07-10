package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.SvAlignerLine
import com.verik.core.sv.SvInstance
import com.verik.core.sv.SvInstanceUsageType
import com.verik.core.sv.SvRange
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkInstanceTest {

    @Nested
    inner class Parse {

        @Test
        fun `simple bool`() {
            val rule = KtRuleParser.parseDeclaration("val a = _bool()")
            val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
            val instance = VkInstance(propertyDeclaration)
            assertEquals(VkInstance(VkInstanceUsageType.REGULAR, "a", VkBoolType, LinePos(1, 1)), instance)
        }

        @Test
        fun `input bool`() {
            val rule = KtRuleParser.parseDeclaration("@input val a = _bool()")
            val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
            val instance = VkInstance(propertyDeclaration)
            assertEquals(VkInstance(VkInstanceUsageType.INPUT, "a", VkBoolType, LinePos(1, 8)), instance)
        }

        @Test
        fun `output uint`() {
            val rule = KtRuleParser.parseDeclaration("@output val a = _uint(1)")
            val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
            val instance = VkInstance(propertyDeclaration)
            assertEquals(VkInstance(VkInstanceUsageType.OUTPUT, "a", VkUintType(1), LinePos(1, 9)), instance)
        }

        @Test
        fun `illegal annotations`() {
            val rule = KtRuleParser.parseDeclaration("@input @rand val a = _uint(1)")
            val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
            assertThrows<VkParseException> {
                VkInstance(propertyDeclaration)
            }
        }
    }

    @Nested
    inner class Extract {

        @Test
        fun `simple bool`() {
            val instance = VkInstance(VkInstanceUsageType.REGULAR, "a", VkBoolType, LinePos.ZERO)
            val expected = SvInstance(SvInstanceUsageType.REGULAR, listOf(), "a", listOf(), LinePos.ZERO)
            assertEquals(expected, instance.extract())
        }

        @Test
        fun `input bool`() {
            val instance = VkInstance(VkInstanceUsageType.INPUT, "a", VkBoolType, LinePos.ZERO)
            val expected = SvInstance(SvInstanceUsageType.INPUT, listOf(), "a", listOf(), LinePos.ZERO)
            assertEquals(expected, instance.extract())
        }

        @Test
        fun `output uint`() {
            val instance = VkInstance(VkInstanceUsageType.OUTPUT, "a", VkUintType(8), LinePos.ZERO)
            val expected = SvInstance(SvInstanceUsageType.OUTPUT, listOf(SvRange(7, 0)), "a", listOf(), LinePos.ZERO)
            assertEquals(expected, instance.extract())
        }

        @Test
        fun `input bool end to end`() {
            val declaration = VkDeclaration(KtRuleParser.parseDeclaration("@input val a = _bool()"))
            val propertyDeclaration = declaration as VkPropertyDeclaration
            val instance = VkInstance(propertyDeclaration)
            val expected = SvAlignerLine(listOf("input", "logic", "", "a", ""), LinePos(1, 8))
            assertEquals(expected, instance.extract().build())
        }

        @Test
        fun `output uint end to end`() {
            val declaration = VkDeclaration(KtRuleParser.parseDeclaration("@output val a = _uint(8)"))
            val propertyDeclaration = declaration as VkPropertyDeclaration
            val instance = VkInstance(propertyDeclaration)
            val expected = SvAlignerLine(listOf("output", "logic", "[7:0]", "a", ""), LinePos(1, 9))
            assertEquals(expected, instance.extract().build())
        }
    }
}