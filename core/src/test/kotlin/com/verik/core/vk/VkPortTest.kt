package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.SvAlignerLine
import com.verik.core.sv.SvPort
import com.verik.core.sv.SvPortType
import com.verik.core.sv.SvRanges
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkPortTest {

    @Nested
    inner class Parse {

        @Test
        fun `input bool`() {
            val rule = KtRuleParser.parseDeclaration("@input val a = _bool()")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            assert(VkPort.isPort(propertyDeclaration))
            val port = VkPort(propertyDeclaration)
            assertEquals(VkPort(VkPortType.INPUT, "a", VkBoolType, LinePos(1, 8)), port)
        }

        @Test
        fun `output uint`() {
            val rule = KtRuleParser.parseDeclaration("@output val a = _uint(1)")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            assert(VkPort.isPort(propertyDeclaration))
            val port = VkPort(propertyDeclaration)
            assertEquals(VkPort(VkPortType.OUTPUT, "a", VkUintType(1), LinePos(1, 9)), port)
        }

        @Test
        fun `not a port`() {
            val rule = KtRuleParser.parseDeclaration("val a = _uint(1)")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            assert(!VkPort.isPort(propertyDeclaration))
        }

        @Test
        fun `illegal annotations`() {
            val rule = KtRuleParser.parseDeclaration("@input @rand val a = _uint(1)")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            assert(VkPort.isPort(propertyDeclaration))
            assertThrows<VkParseException> {
                VkPort(propertyDeclaration)
            }
        }
    }

    @Nested
    inner class Extract {

        @Test
        fun `input bool`() {
            val port = VkPort(VkPortType.INPUT, "a", VkBoolType, LinePos.ZERO)
            val expected = SvPort(SvPortType.INPUT, SvRanges(listOf()), "a", SvRanges(listOf()), LinePos.ZERO)
            assertEquals(expected, port.extract())
        }

        @Test
        fun `output uint`() {
            val port = VkPort(VkPortType.OUTPUT, "a", VkUintType(8), LinePos.ZERO)
            val expected = SvPort(SvPortType.OUTPUT, SvRanges(listOf(Pair(7, 0))), "a", SvRanges(listOf()), LinePos.ZERO)
            assertEquals(expected, port.extract())
        }

        @Test
        fun `input bool end to end`() {
            val rule = KtRuleParser.parseDeclaration("@input val a = _bool()")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            assert(VkPort.isPort(propertyDeclaration))
            val port = VkPort(propertyDeclaration)
            val expected = SvAlignerLine(listOf("input", "logic", "", "a", ""), LinePos(1, 8))
            assertEquals(expected, port.extract().build())
        }

        @Test
        fun `output uint end to end`() {
            val rule = KtRuleParser.parseDeclaration("@output val a = _uint(8)")
            val declaration = VkDeclaration(rule)
            assert(declaration is VkPropertyDeclaration)
            val propertyDeclaration = declaration as VkPropertyDeclaration
            assert(VkPort.isPort(propertyDeclaration))
            val port = VkPort(propertyDeclaration)
            val expected = SvAlignerLine(listOf("output", "logic", "[7:0]", "a", ""), LinePos(1, 9))
            assertEquals(expected, port.extract().build())
        }
    }
}