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

package verikc.ge

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.ge.ast.*
import verikc.rs.RsResolveUtil

object GeGenerifyUtil {

    fun generifyCompilationUnit(string: String): GeCompilationUnit {
        val compilationUnit = GeStageDriver.build(RsResolveUtil.resolveCompilationUnit(string))
        GeStageDriver.generify(compilationUnit)
        return compilationUnit
    }

    fun generifyFunction(fileContext: String, string: String): GeFunction {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = generifyFile(fileString)
        return file.functions.last()
    }

    fun generifyExpression(fileContext: String, string: String): GeExpression {
        val statement = generifyStatement(fileContext, string)
        return if (statement is GeStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }

    private fun generifyFile(string: String): GeFile {
        return generifyCompilationUnit(string).pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    private fun generifyStatement(fileContext: String, string: String): GeStatement {
        val functionString = """
            fun f() {
                $string
            }
        """.trimIndent()
        val function = generifyFunction(fileContext, functionString)
        return function.block.statements.last()
    }
}