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
import io.verik.importer.ast.sv.element.declaration.SvPackage
import io.verik.importer.message.SourceLocation

object PackageCaster {

    fun castPackageFromPackageDeclaration(
        ctx: SystemVerilogParser.PackageDeclarationContext,
        castContext: CastContext
    ): SvPackage {
        val location = SourceLocation.get(ctx.PACKAGE())
        val name = ctx.packageIdentifier()[0].text
        val declarations = ctx.packageItem().mapNotNull { castContext.getDeclaration(it) }
        return SvPackage(location, name, ArrayList(declarations))
    }
}
