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

package verikc.rf.reify

import verikc.rf.ast.RfDeclaration
import verikc.rf.ast.RfFile
import verikc.rf.symbol.RfSymbolTable


object RfReifier {

    fun reifyFile(file: RfFile, symbolTable: RfSymbolTable) {
        RfReifierProperty.reifyFile(file, symbolTable)
        RfReifierStatement.reifyFile(file, symbolTable)
    }

    fun reifyDeclaration(declaration: RfDeclaration, symbolTable: RfSymbolTable) {
        RfReifierProperty.reifyDeclaration(declaration, symbolTable)
        RfReifierStatement.reifyDeclaration(declaration, symbolTable)
    }
}