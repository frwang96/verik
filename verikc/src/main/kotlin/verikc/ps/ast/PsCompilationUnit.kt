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

package verikc.ps.ast

import verikc.base.ast.Symbol

data class PsCompilationUnit(
        val pkgs: List<PsPkg>
) {

    fun pkg(pkg: Symbol): PsPkg {
        if (!pkg.isPkgSymbol()) {
            throw IllegalArgumentException("package expected but got $pkg")
        }
        return pkgs.find { it.pkg == pkg }
                ?: throw IllegalArgumentException("could not find package $pkg")
    }

    fun file(file: Symbol): PsFile {
        val pkg = pkg(file.toPkgSymbol())
        return pkg.file(file)
    }
}