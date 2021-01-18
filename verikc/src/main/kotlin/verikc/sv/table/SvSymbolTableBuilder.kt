/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.sv.table

import verikc.ps.ast.*

object SvSymbolTableBuilder {

    fun build(compilationUnit: PsCompilationUnit, symbolTable: SvSymbolTable) {
        compilationUnit.pkgs.forEach { buildPkg(it, symbolTable) }
    }

    private fun buildPkg(pkg: PsPkg, symbolTable: SvSymbolTable) {
        symbolTable.addPkg(pkg)
        pkg.files.forEach { buildFile(it, symbolTable) }
    }

    private fun buildFile(file: PsFile, symbolTable: SvSymbolTable) {
        symbolTable.addFile(file)
        file.components.forEach { buildComponent(it, symbolTable) }
        file.busses.forEach { buildBus(it, symbolTable) }
        file.primaryProperties.forEach { symbolTable.addProperty(it.property, true) }
        file.enums.forEach { buildEnum(it, symbolTable) }
        file.clses.forEach { buildCls(it, symbolTable) }
    }

    private fun buildComponent(component: PsComponent, symbolTable: SvSymbolTable) {
        component.ports.forEach { symbolTable.addProperty(it.property, false) }
        component.properties.forEach { symbolTable.addProperty(it, false) }
        component.componentInstances.forEach { symbolTable.addProperty(it.property, false) }
        component.actionBlocks.forEach { buildBlock(it.block, symbolTable) }
        component.methodBlocks.forEach { buildMethodBlock(it, symbolTable) }
        symbolTable.addType(component)
    }

    private fun buildBus(bus: PsBus, symbolTable: SvSymbolTable) {
        bus.ports.forEach { symbolTable.addProperty(it.property, false) }
        bus.properties.forEach { symbolTable.addProperty(it, false) }
        symbolTable.addType(bus)
    }

    private fun buildEnum(enum: PsEnum, symbolTable: SvSymbolTable) {
        symbolTable.addType(enum)
        symbolTable.addProperty(enum.typeObject, true)
        enum.entries.forEach { symbolTable.addProperty(it, enum.identifier) }
    }

    private fun buildCls(cls: PsCls, symbolTable: SvSymbolTable) {
        symbolTable.addType(cls)
        symbolTable.addFunction(cls.constructorFunction)
        cls.methodBlocks.forEach { buildMethodBlock(it, symbolTable) }
    }

    private fun buildMethodBlock(methodBlock: PsMethodBlock, symbolTable: SvSymbolTable) {
        symbolTable.addFunction(methodBlock)
        methodBlock.parameterProperties.forEach { parameterProperty ->
            symbolTable.addProperty(parameterProperty, false)
        }
        buildBlock(methodBlock.block, symbolTable)
    }

    private fun buildBlock(block: PsBlock, symbolTable: SvSymbolTable) {
        block.lambdaProperties.forEach {
            symbolTable.addProperty(it, false)
        }
        block.properties.forEach {
            symbolTable.addProperty(it, false)
        }
        block.expressions.forEach { expression ->
            if (expression is PsExpressionOperator) {
                expression.blocks.forEach { buildBlock(it, symbolTable) }
            }
        }
    }
}
