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

package verikc.gex

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.gex.ast.*
import verikc.rs.RsResolveUtil

object GexGenerifyUtil {

    fun generifyFunction(fileContext: String, string: String): GexFunction {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = generifyFile(fileString)
        return file.functions.last()
    }

    fun generifyExpression(fileContext: String, string: String): GexExpression {
        val statement = generifyStatement(fileContext, string)
        return if (statement is GexStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }

    private fun generifyCompilationUnit(string: String): GexCompilationUnit {
        val compilationUnit = GexStageDriver.build(RsResolveUtil.resolveCompilationUnit(string))
        GexStageDriver.generify(compilationUnit)
        return compilationUnit
    }

    private fun generifyFile(string: String): GexFile {
        return generifyCompilationUnit(string).pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    private fun generifyStatement(fileContext: String, string: String): GexStatement {
        val functionString = """
            fun f() {
                $string
            }
        """.trimIndent()
        val function = generifyFunction(fileContext, functionString)
        return function.block.statements.last()
    }
}