package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.SourceBuilder
import com.verik.core.assertStringEquals
import com.verik.core.kt.KtTree
import com.verik.core.sv.SvModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Copyright (c) 2020 Francis Wang

internal class VkModuleTest {

    @Nested
    inner class Parse {
        @Test
        fun `simple module`() {
            val tree = KtTree.parseTopLevelObject("class _m: _module")
            assert(VkClassDeclaration.isClassDeclaration(tree))
            val classDeclaration = VkClassDeclaration(tree)
            assert(VkModule.isModule(classDeclaration))
            val module = VkModule(classDeclaration)
            assertEquals(VkModule(VkModuleElabType.REGULAR, false, "_m", listOf()), module)
        }

        @Test
        fun `module with port`() {
            val tree = KtTree.parseTopLevelObject("""
                class _m: _module {
                    @input val a = _bool()
                }
            """.trimIndent())
            assert(VkClassDeclaration.isClassDeclaration(tree))
            val classDeclaration = VkClassDeclaration(tree)
            assert(VkModule.isModule(classDeclaration))
            val module = VkModule(classDeclaration)
            assertEquals(VkModule(VkModuleElabType.REGULAR, false, "_m",
                    listOf(VkPort(VkPortType.INPUT, "a", VkBoolType, LinePos(1, 0)))), module)
        }

        @Test
        fun `illegal elaboration type`() {
            val tree = KtTree.parseTopLevelObject("@top @extern class _m: _module")
            assert(VkClassDeclaration.isClassDeclaration(tree))
            val classDeclaration = VkClassDeclaration(tree)
            assert(VkModule.isModule(classDeclaration))
            assertThrows<VkParseException> {
                VkModule(classDeclaration)
            }
        }

        @Test
        fun `illegal modifier type`() {
            val tree = KtTree.parseTopLevelObject("enum class _m: _module")
            assert(VkClassDeclaration.isClassDeclaration(tree))
            val classDeclaration = VkClassDeclaration(tree)
            assert(VkModule.isModule(classDeclaration))
            assertThrows<VkParseException> {
                VkModule(classDeclaration)
            }
        }
    }

    @Nested
    inner class Build {
        @Test
        fun `simple module`() {
            val module = VkModule(VkModuleElabType.REGULAR, true, "_m", listOf())
            val expected = SvModule("m", listOf())
            assertEquals(expected, module.extract())
        }

        @Test
        fun `simple module end to end`() {
            val tree = KtTree.parseTopLevelObject("class _m: _module")
            val classDeclaration = VkClassDeclaration(tree)
            val module = VkModule(classDeclaration)
            val builder = SourceBuilder()
            module.extract().build(builder)
            val expected = """
                module m;
                  timeunit 1ns/1ns;
                endmodule: m
            """.trimIndent()
            assertStringEquals(expected, builder)
        }
    }
}