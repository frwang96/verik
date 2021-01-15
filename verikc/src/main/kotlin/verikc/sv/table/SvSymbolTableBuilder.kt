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
        file.modules.forEach { buildModule(it, symbolTable) }
        file.primaryProperties.forEach { symbolTable.addProperty(it.property, true) }
        file.enums.forEach { buildEnum(it, symbolTable) }
        file.clses.forEach { buildCls(it, symbolTable) }
    }

    private fun buildModule(module: PsModule, symbolTable: SvSymbolTable) {
        module.ports.forEach { symbolTable.addProperty(it.property, false) }
        module.properties.forEach { symbolTable.addProperty(it, false) }
        module.actionBlocks.forEach { buildBlock(it.block, symbolTable) }
        module.methodBlocks.forEach {
            symbolTable.addFunction(it)
            it.parameterProperties.forEach { parameterProperty ->
                symbolTable.addProperty(parameterProperty, false)
            }
            buildBlock(it.block, symbolTable)
        }
        symbolTable.addType(module)
    }

    private fun buildEnum(enum: PsEnum, symbolTable: SvSymbolTable) {
        symbolTable.addType(enum)
        enum.entries.forEach { symbolTable.addProperty(it, enum.identifier) }
    }

    private fun buildCls(cls: PsCls, symbolTable: SvSymbolTable) {
        symbolTable.addType(cls)
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
