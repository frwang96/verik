/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.sv.check

import verikc.sv.ast.*

object SvCheckerIdentifierCollision {

    fun check(compilationUnit: SvCompilationUnit) {
        checkScopeRoot(compilationUnit)
        compilationUnit.pkgs.forEach { checkScopePkg(it) }
    }

    private fun checkScopeRoot(compilationUnit: SvCompilationUnit) {
        val scopeTable = SvScopeTable()
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.components.forEach {
                    scopeTable.add(it.identifier, it.line)
                    checkScopeComponent(it)
                }
            }
        }
    }

    private fun checkScopePkg(pkg: SvPkg) {
        val scopeTable = SvScopeTable()
        for (file in pkg.files) {
            file.primaryProperties.forEach { scopeTable.add(it.property.identifier, it.property.line) }
            file.enums.forEach {
                scopeTable.add(it.identifier, it.line)
                it.properties.forEach { property -> scopeTable.add(property.identifier, property.line) }
            }
            file.structs.forEach {
                scopeTable.add(it.identifier, it.line)
                checkScopeStruct(it)
            }
            file.clses.forEach {
                scopeTable.add(it.identifier, it.line)
                checkScopeCls(it)
            }
        }
    }

    private fun checkScopeComponent(component: SvComponent) {
        val scopeTable = SvScopeTable()
        component.ports.forEach { scopeTable.add(it.property.identifier, it.property.line) }
        component.properties.forEach { scopeTable.add(it.identifier, it.line) }
        component.componentInstances.forEach { scopeTable.add(it.property.identifier, it.property.line) }
        component.actionBlocks.forEach {
            scopeTable.add(it.identifier, it.line)
            checkScopeBlock(it.block)
        }
        component.methodBlocks.forEach {
            scopeTable.add(it.identifier, it.line)
            checkScopeMethodBlock(it)
        }
    }

    private fun checkScopeStruct(struct: SvStruct) {
        val scopeTable = SvScopeTable()
        struct.properties.forEach { scopeTable.add(it.identifier, it.line) }
    }

    private fun checkScopeCls(cls: SvCls) {
        val scopeTable = SvScopeTable()
        cls.methodBlocks.forEach {
            scopeTable.add(it.identifier, it.line)
            checkScopeMethodBlock(it)
        }
    }

    private fun checkScopeMethodBlock(methodBlock: SvMethodBlock) {
        val scopeTable = SvScopeTable()
        methodBlock.parameterProperties.forEach { scopeTable.add(it.identifier, it.line) }
        checkScopeBlock(methodBlock.block)
    }

    private fun checkScopeBlock(block: SvBlock) {
        val scopeTable = SvScopeTable()
        block.lambdaProperties.forEach { scopeTable.add(it.identifier, it.line) }
        block.properties.forEach { scopeTable.add(it.identifier, it.line) }
        for (expression in block.expressions) {
            if (expression is SvExpressionControlBlock) {
                expression.blocks.forEach { checkScopeBlock(it) }
            }
        }
    }
}