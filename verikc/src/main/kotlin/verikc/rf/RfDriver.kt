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

package verikc.rf

import verikc.rf.ast.RfCompilationUnit
import verikc.rf.ast.RfFile
import verikc.rf.ast.RfPkg
import verikc.rf.check.RfCheckerConnection
import verikc.rf.reify.RfReifier
import verikc.rf.symbol.RfSymbolTable
import verikc.vk.ast.VkCompilationUnit

object RfDriver {

    fun drive(compilationUnit: VkCompilationUnit): RfCompilationUnit {
        val symbolTable = RfSymbolTable()
        val pkgs = ArrayList<RfPkg>()
        for (pkg in compilationUnit.pkgs) {
            val files = ArrayList<RfFile>()
            for (file in pkg.files) {
                files.add(RfFile(file))
            }
            pkgs.add(RfPkg(pkg.config, files))
        }
        return RfCompilationUnit(pkgs).also {
            reify(it, symbolTable)
            check(it, symbolTable)
        }
    }

    private fun reify(compilationUnit: RfCompilationUnit, symbolTable: RfSymbolTable) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                RfReifier.reifyFile(file, symbolTable)
            }
        }
    }

    private fun check(compilationUnit: RfCompilationUnit, symbolTable: RfSymbolTable) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                RfCheckerConnection.check(file, symbolTable)
            }
        }
    }
}
