/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.sv.element.SvClass
import io.verik.importer.message.SourceLocation

object ClassCaster {

    fun castClassFromClassDeclaration(
        ctx: SystemVerilogParser.ClassDeclarationContext
    ): SvClass {
        val location = SourceLocation.get(ctx.CLASS())
        val name = ctx.classIdentifier()[0].text
        return SvClass(location, name)
    }
}
