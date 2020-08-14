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

package verik.core.vk

import verik.core.main.LineException
import verik.core.al.AlRuleParser
import verik.core.assertThrowsMessage
import verik.core.sv.SvAlignerLine
import verik.core.sv.SvInstanceDeclaration
import verik.core.sv.SvInstancePortType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkInstanceDeclarationTest {

    @Test
    fun `parse bool`() {
        val rule = AlRuleParser.parseDeclaration("val a = _bool()")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val instanceDeclaration = VkInstanceDeclaration(propertyDeclaration)
        assertEquals(VkInstanceDeclaration(1, VkInstancePortType.NONE, "a", VkBoolType), instanceDeclaration)
    }

    @Test
    fun `parse bool input`() {
        val rule = AlRuleParser.parseDeclaration("@input val a = _bool()")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val instanceDeclaration = VkInstanceDeclaration(propertyDeclaration)
        assertEquals(VkInstanceDeclaration(1, VkInstancePortType.INPUT, "a", VkBoolType), instanceDeclaration)
    }

    @Test
    fun `parse uint output`() {
        val rule = AlRuleParser.parseDeclaration("@output val a = _uint(1)")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        val instanceDeclaration = VkInstanceDeclaration(propertyDeclaration)
        assertEquals(VkInstanceDeclaration(1, VkInstancePortType.OUTPUT, "a", VkUintType(1)), instanceDeclaration)
    }

    @Test
    fun `parse uint illegal annotations`() {
        val rule = AlRuleParser.parseDeclaration("@input @rand val a = _uint(1)")
        val propertyDeclaration = VkDeclaration(rule) as VkPropertyDeclaration
        assertThrowsMessage<LineException>("illegal instance port type") {
            VkInstanceDeclaration(propertyDeclaration)
        }
    }

    @Test
    fun `extract bool`() {
        val instanceDeclaration = VkInstanceDeclaration(0, VkInstancePortType.NONE, "a", VkBoolType)
        val expected = SvInstanceDeclaration(0, SvInstancePortType.NONE, null, "a", listOf())
        assertEquals(expected, instanceDeclaration.extract())
    }

    @Test
    fun `extract bool input`() {
        val instanceDeclaration = VkInstanceDeclaration(0, VkInstancePortType.INPUT, "a", VkBoolType)
        val expected = SvInstanceDeclaration(0, SvInstancePortType.INPUT, null, "a", listOf())
        assertEquals(expected, instanceDeclaration.extract())
    }

    @Test
    fun `extract uint output`() {
        val instanceDeclaration = VkInstanceDeclaration(0, VkInstancePortType.OUTPUT, "a", VkUintType(8))
        val expected = SvInstanceDeclaration(0, SvInstancePortType.OUTPUT, 8, "a", listOf())
        assertEquals(expected, instanceDeclaration.extract())
    }

    @Test
    fun `end to end input bool`() {
        val declaration = VkDeclaration(AlRuleParser.parseDeclaration("@input val a = _bool()"))
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val instanceDeclaration = VkInstanceDeclaration(propertyDeclaration)
        val expected = SvAlignerLine(1, listOf("input", "logic", "", "a", ""))
        assertEquals(expected, instanceDeclaration.extract().build())
    }

    @Test
    fun `end to end output uint`() {
        val declaration = VkDeclaration(AlRuleParser.parseDeclaration("@output val a = _uint(8)"))
        val propertyDeclaration = declaration as VkPropertyDeclaration
        val instanceDeclaration = VkInstanceDeclaration(propertyDeclaration)
        val expected = SvAlignerLine(1, listOf("output", "logic", "[7:0]", "a", ""))
        assertEquals(expected, instanceDeclaration.extract().build())
    }
}