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

package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.LinePosException
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.SvModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class VkModuleTest {

    @Test
    fun `parse module`() {
        val rule = KtRuleParser.parseDeclaration("class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assert(VkModule.isModule(classDeclaration))
        val module = VkModule(classDeclaration)
        val expected = VkModule(VkModuleElabType.REGULAR, false, "_m", listOf(), listOf(), listOf(), LinePos(1, 1))
        assertEquals(expected, module)
    }

    @Test
    fun `parse module with port`() {
        val rule = KtRuleParser.parseDeclaration("""
            class _m: _module {
                @input val a = _bool()
            }
        """.trimIndent())
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assert(VkModule.isModule(classDeclaration))
        val module = VkModule(classDeclaration)
        val expected = VkModule(VkModuleElabType.REGULAR, false, "_m",
                listOf(VkInstance(VkInstanceUsageType.INPUT, "a", VkBoolType, LinePos(2, 12))),
                listOf(), listOf(), LinePos(1, 1))
        assertEquals(expected, module)
    }

    @Test
    fun `parse module illegal elaboration type`() {
        val rule = KtRuleParser.parseDeclaration("@top @extern class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assert(VkModule.isModule(classDeclaration))
        val exception = assertThrows<LinePosException> {
            VkModule(classDeclaration)
        }
        assertEquals("(1, 14) illegal module elaboration type", exception.message)
    }

    @Test
    fun `parse module illegal modifier type`() {
        val rule = KtRuleParser.parseDeclaration("enum class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assert(VkModule.isModule(classDeclaration))
        val exception = assertThrows<LinePosException> {
            VkModule(classDeclaration)
        }
        assertEquals("(1, 6) class modifiers are not permitted here", exception.message)
    }

    @Test
    fun `extract module`() {
        val module = VkModule(VkModuleElabType.REGULAR, true, "_m", listOf(), listOf(), listOf(), LinePos.ZERO)
        val expected = SvModule("m", listOf(), listOf(), listOf(), listOf(), listOf(), LinePos.ZERO)
        assertEquals(expected, module.extract())
    }

    @Test
    fun `end to end module`() {
        val rule = KtRuleParser.parseDeclaration("class _m: _module")
        val declaration = VkDeclaration(rule)
        assert(declaration is VkClassDeclaration)
        val classDeclaration = declaration as VkClassDeclaration
        assert(VkModule.isModule(classDeclaration))
        val module = VkModule(classDeclaration)
        val builder = SourceBuilder()
        module.extract().build(builder)
        val expected = """
            module m;
              timeunit 1ns / 1ns;

            endmodule: m
        """.trimIndent()
        assertStringEquals(expected, builder)
    }
}
