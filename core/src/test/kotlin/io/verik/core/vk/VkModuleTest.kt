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

import io.verik.core.main.LineException
import io.verik.core.main.SourceBuilder
import io.verik.core.al.AlRuleParser
import io.verik.core.assertStringEquals
import io.verik.core.assertThrowsMessage
import io.verik.core.sv.SvModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class VkModuleTest {

    @Test
    fun `parse module`() {
        val rule = AlRuleParser.parseDeclaration("class _m: _module")
        val declaration = VkDeclaration(rule)
        val classDeclaration = declaration as VkClassDeclaration
        assert(VkModule.isModule(classDeclaration))
        val module = VkModule(classDeclaration)
        val expected = VkModule(1, false, "_m", listOf(), listOf(), listOf())
        assertEquals(expected, module)
    }

    @Test
    fun `parse module with port`() {
        val rule = AlRuleParser.parseDeclaration("""
            class _m: _module {
                @input val a = _bool()
            }
        """.trimIndent())
        val declaration = VkDeclaration(rule)
        val classDeclaration = declaration as VkClassDeclaration
        assert(VkModule.isModule(classDeclaration))
        val module = VkModule(classDeclaration)
        val expected = VkModule(1, false, "_m",
                listOf(VkInstanceDeclaration(2, VkInstancePortType.INPUT, "a", VkBoolType)),
                listOf(), listOf())
        assertEquals(expected, module)
    }

    @Test
    fun `parse module illegal annotation abstract`() {
        val rule = AlRuleParser.parseDeclaration("@abstract class _m: _module")
        val declaration = VkDeclaration(rule)
        val classDeclaration = declaration as VkClassDeclaration
        assert(VkModule.isModule(classDeclaration))
        assertThrowsMessage<LineException>("modules cannot be abstract") {
            VkModule(classDeclaration)
        }
    }

    @Test
    fun `parse module illegal modifier type`() {
        val rule = AlRuleParser.parseDeclaration("enum class _m: _module")
        val declaration = VkDeclaration(rule)
        val classDeclaration = declaration as VkClassDeclaration
        assert(VkModule.isModule(classDeclaration))
        assertThrowsMessage<LineException>("class modifiers are not permitted here") {
            VkModule(classDeclaration)
        }
    }

    @Test
    fun `extract module`() {
        val module = VkModule(0, false, "_m", listOf(), listOf(), listOf())
        val expected = SvModule(0, "m", listOf(), listOf(), listOf(), listOf(), listOf())
        assertEquals(expected, module.extract())
    }

    @Test
    fun `end to end module`() {
        val rule = AlRuleParser.parseDeclaration("class _m: _module")
        val declaration = VkDeclaration(rule)
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
