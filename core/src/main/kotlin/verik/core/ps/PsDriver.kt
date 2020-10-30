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

import verik.core.base.LineException
import verik.core.ps.ast.PsCompilationUnit
import verik.core.ps.ast.PsFile
import verik.core.ps.ast.PsPkg
import verik.core.rf.ast.RfCompilationUnit

object PsDriver {

    fun drive(compilationUnit: RfCompilationUnit): PsCompilationUnit {
        val pkgs = ArrayList<PsPkg>()
        for (pkg in compilationUnit.pkgs) {
            val files = ArrayList<PsFile>()
            for (file in pkg.files) {
                try {
                    files.add(PsFile(file))
                } catch (exception: LineException) {
                    exception.file = file.file
                    throw exception
                }
            }
            pkgs.add(PsPkg(pkg.pkg, files))
        }
        return PsCompilationUnit(pkgs)
    }
}