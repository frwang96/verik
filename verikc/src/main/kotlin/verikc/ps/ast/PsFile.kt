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

package verikc.ps.ast

import verikc.base.ast.LineException
import verikc.base.config.FileConfig
import verikc.ge.ast.GeEnum
import verikc.ge.ast.GeFile
import verikc.ge.ast.GeModule

data class PsFile(
    val config: FileConfig,
    val declarations: List<PsDeclaration>
) {

    companion object {

        operator fun invoke(file: GeFile): PsFile {
            val declarations = ArrayList<PsDeclaration>()
            for (declaration in file.declarations) {
                when (declaration) {
                    is GeModule -> declarations.add(PsModule(declaration))
                    is GeEnum -> declarations.add(PsEnum(declaration))
                    else -> throw LineException("top level declaration not supported", declaration.line)
                }
            }

            return PsFile(
                file.config,
                declarations
            )
        }
    }
}
