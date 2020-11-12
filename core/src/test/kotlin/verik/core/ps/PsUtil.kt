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

package verik.core.ps

import verik.core.ps.ast.*
import verik.core.ps.pass.PsPass
import verik.core.ps.symbol.PsSymbolTable
import verik.core.ps.symbol.PsSymbolTableBuilder
import verik.core.rf.RfUtil
import verik.core.sv.ast.*


object PsUtil {

    fun extractModuleFile(string: String): SvFile {
        val file = PsFile(RfUtil.parseFile(string))
        val symbolTable = PsSymbolTable()
        PsSymbolTableBuilder.buildFile(file, symbolTable)
        PsPass.passFile(file, symbolTable)
        return file.extractModuleFile(symbolTable)!!
    }

    fun extractModule(string: String): SvModule {
        val module = PsModule(RfUtil.parseModule(string))
        val symbolTable = PsSymbolTable()
        PsSymbolTableBuilder.buildDeclaration(module, symbolTable)
        PsPass.passDeclaration(module, symbolTable)
        return module.extract(symbolTable)
    }

    fun extractEnum(string: String): SvEnum {
        val enum = PsEnum(RfUtil.parseEnum(string))
        val symbolTable = PsSymbolTable()
        PsSymbolTableBuilder.buildDeclaration(enum, symbolTable)
        PsPass.passDeclaration(enum, symbolTable)
        return enum.extract()
    }

    fun extractPort(string: String): SvPort {
        val port = PsPort(RfUtil.parsePort(string))
        return port.extract(PsSymbolTable())
    }

    fun extractPrimaryProperty(string: String): SvPrimaryProperty {
        val primaryProperty = PsPrimaryProperty(RfUtil.parsePrimaryProperty(string))
        return primaryProperty.extract(PsSymbolTable())
    }

    fun extractActionBlock(string: String): SvActionBlock {
        val actionBlock = PsActionBlock(RfUtil.parseActionBlock(string))
        val symbolTable = PsSymbolTable()
        PsPass.passDeclaration(actionBlock, symbolTable)
        return actionBlock.extract(symbolTable)
    }

    fun extractExpression(string: String): SvExpression {
        val expression = PsExpression(RfUtil.parseExpression(string))
        return expression.extractAsExpression(PsSymbolTable())
    }

    fun extractStatement(string: String): SvStatement {
        val expression = PsExpression(RfUtil.parseExpression(string))
        return expression.extract(PsSymbolTable())
    }
}