/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.cast

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.ast.element.declaration.ESvPackage
import io.verik.importer.cast.common.CastContext

object PackageCaster {

    fun castPackageFromPackageDeclaration(
        ctx: SystemVerilogParser.PackageDeclarationContext,
        castContext: CastContext
    ): ESvPackage {
        val location = castContext.getLocation(ctx.PACKAGE())
        val name = ctx.packageIdentifier()[0].text
        val declarations = ctx.packageItem().flatMap { castContext.castDeclarations(it) }
        return ESvPackage(location, name, ArrayList(declarations))
    }
}
