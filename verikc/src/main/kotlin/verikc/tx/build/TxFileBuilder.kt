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

package verikc.tx.build

import verikc.base.ast.LineException
import verikc.sv.ast.SvDeclaration
import verikc.sv.ast.SvEnum
import verikc.sv.ast.SvFile
import verikc.sv.ast.SvModule

object TxFileBuilder {

    fun build(file: SvFile, builder: TxSourceBuilder) {
        if (file.declarations.isNotEmpty()) {
            for (declaration in file.declarations.dropLast(1)) {
                buildDeclaration(declaration, builder)
                builder.appendln()
            }
            buildDeclaration(file.declarations.last(), builder)
        }
    }

    private fun buildDeclaration(declaration: SvDeclaration, builder: TxSourceBuilder) {
        when (declaration) {
            is SvModule -> TxModuleBuilder.build(declaration, builder)
            is SvEnum -> TxEnumBuilder.build(declaration, builder)
            else -> throw LineException("declaration type not supported", declaration.line)
        }
    }
}