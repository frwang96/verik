/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.core.vk

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.kt.KtRuleParser
import io.verik.core.sv.SvAlignerLine
import io.verik.core.sv.SvInstance
import io.verik.core.sv.SvInstanceUsageType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class VkInstanceTest {

    @Test
    fun `parse bool`() {
        val rule = KtRuleParser.parseDeclaration("val a = _bool()")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val instance = VkInstance(propertyDeclaration)
        assertEquals(VkInstance(VkInstanceUsageType.REGULAR, "a", VkBoolType, FileLine(1)), instance)
    }

    @Test
    fun `parse bool input`() {
        val rule = KtRuleParser.parseDeclaration("@input val a = _bool()")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val instance = VkInstance(propertyDeclaration)
        assertEquals(VkInstance(VkInstanceUsageType.INPUT, "a", VkBoolType, FileLine(1)), instance)
    }

    @Test
    fun `parse uint output`() {
        val rule = KtRuleParser.parseDeclaration("@output val a = _uint(1)")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val instance = VkInstance(propertyDeclaration)
        assertEquals(VkInstance(VkInstanceUsageType.OUTPUT, "a", VkUintType(1), FileLine(1)), instance)
    }

    @Test
    fun `parse uint illegal annotations`() {
        val rule = KtRuleParser.parseDeclaration("@input @rand val a = _uint(1)")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val exception = assertThrows<FileLineException> {
            VkInstance(propertyDeclaration)
        }
        assertEquals("illegal instance usage type", exception.message)
    }

    @Test
    fun `extract bool`() {
        val instance = VkInstance(VkInstanceUsageType.REGULAR, "a", VkBoolType, FileLine())
        val expected = SvInstance(SvInstanceUsageType.REGULAR, null, "a", listOf(), FileLine())
        assertEquals(expected, instance.extract())
    }

    @Test
    fun `extract bool input`() {
        val instance = VkInstance(VkInstanceUsageType.INPUT, "a", VkBoolType, FileLine())
        val expected = SvInstance(SvInstanceUsageType.INPUT, null, "a", listOf(), FileLine())
        assertEquals(expected, instance.extract())
    }

    @Test
    fun `extract uint output`() {
        val instance = VkInstance(VkInstanceUsageType.OUTPUT, "a", VkUintType(8), FileLine())
        val expected = SvInstance(SvInstanceUsageType.OUTPUT, 8, "a", listOf(), FileLine())
        assertEquals(expected, instance.extract())
    }

    @Test
    fun `end to end input bool`() {
        val declaration = VkDeclaration(KtRuleParser.parseDeclaration("@input val a = _bool()"))
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val instance = VkInstance(propertyDeclaration)
        val expected = SvAlignerLine(listOf("input", "logic", "", "a", ""), FileLine(1))
        assertEquals(expected, instance.extract().build())
    }

    @Test
    fun `end to end output uint`() {
        val declaration = VkDeclaration(KtRuleParser.parseDeclaration("@output val a = _uint(8)"))
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val instance = VkInstance(propertyDeclaration)
        val expected = SvAlignerLine(listOf("output", "logic", "[7:0]", "a", ""), FileLine(1))
        assertEquals(expected, instance.extract().build())
    }
}