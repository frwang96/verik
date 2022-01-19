/*
 * Copyright (c) 2021 Francis Wang
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
import io.verik.importer.antlr.SystemVerilogParserBaseVisitor
import io.verik.importer.ast.common.Type
import io.verik.importer.ast.sv.element.SvClass
import io.verik.importer.ast.sv.element.SvElement
import io.verik.importer.ast.sv.element.SvModule
import io.verik.importer.ast.sv.element.SvPackage
import io.verik.importer.ast.sv.element.SvPort
import io.verik.importer.ast.sv.element.SvProperty
import io.verik.importer.common.Castable
import io.verik.importer.message.Messages
import org.antlr.v4.runtime.RuleContext

class CasterVisitor(
    val castContext: CastContext
) : SystemVerilogParserBaseVisitor<Castable>() {

    inline fun <reified E : SvElement> getElement(ctx: RuleContext): E? {
        return when (val castable = ctx.accept(this)) {
            null -> null
            is SvElement -> castable.cast()
            else -> {
                Messages.INTERNAL_ERROR.on(
                    castContext.getLocation(ctx),
                    "Could not cast element: Expected ${E::class.simpleName} actual ${castable::class.simpleName}"
                )
            }
        }
    }

    fun getType(ctx: RuleContext): Type? {
        return when (val castable = ctx.accept(this)) {
            null -> null
            is Type -> castable
            else -> {
                Messages.INTERNAL_ERROR.on(
                    castContext.getLocation(ctx),
                    "Expected type but found: ${castable::class.simpleName}"
                )
            }
        }
    }

// A.1.2 SystemVerilog Source Text /////////////////////////////////////////////////////////////////////////////////////

    override fun visitModuleDeclarationNonAnsi(ctx: SystemVerilogParser.ModuleDeclarationNonAnsiContext?): SvModule? {
        return ModuleCaster.castModuleFromModuleDeclarationNonAnsi(ctx!!, castContext)
    }

    override fun visitModuleDeclarationAnsi(ctx: SystemVerilogParser.ModuleDeclarationAnsiContext?): SvModule? {
        return ModuleCaster.castModuleFromModuleDeclarationAnsi(ctx!!, castContext)
    }

    override fun visitClassDeclaration(ctx: SystemVerilogParser.ClassDeclarationContext?): SvClass {
        return ClassCaster.castClassFromClassDeclaration(ctx!!)
    }

    override fun visitPackageDeclaration(ctx: SystemVerilogParser.PackageDeclarationContext?): SvPackage {
        return PackageCaster.castPackageFromPackageDeclaration(ctx!!, castContext)
    }

// A.1.3 Module Parameters and Ports ///////////////////////////////////////////////////////////////////////////////////

    override fun visitPortDeclaration(ctx: SystemVerilogParser.PortDeclarationContext?): SvPort? {
        return PortCaster.castPortFromPortDeclaration(ctx!!, castContext)
    }

    override fun visitAnsiPortDeclaration(ctx: SystemVerilogParser.AnsiPortDeclarationContext?): SvPort? {
        return PortCaster.castPortFromAnsiPortDeclaration(ctx!!, castContext)
    }

// A.2.1.3 Type Declarations ///////////////////////////////////////////////////////////////////////////////////////////

    override fun visitDataDeclarationData(ctx: SystemVerilogParser.DataDeclarationDataContext?): SvProperty? {
        return PropertyCaster.castPropertyFromDataDeclarationData(ctx!!, castContext)
    }

// A.2.2.1 Net and Variable Types //////////////////////////////////////////////////////////////////////////////////////

    override fun visitDataTypeVector(ctx: SystemVerilogParser.DataTypeVectorContext?): Type? {
        return TypeCaster.castTypeFromDataTypeVector(ctx!!, castContext)
    }

    override fun visitDataTypeOrImplicit(ctx: SystemVerilogParser.DataTypeOrImplicitContext?): Type? {
        return TypeCaster.castTypeFromDataTypeOrImplicit(ctx!!, castContext)
    }

    override fun visitImplicitDataType(ctx: SystemVerilogParser.ImplicitDataTypeContext?): Type? {
        return TypeCaster.castTypeFromImplicitDataType(ctx!!, castContext)
    }

// A.2.5 Declaration Ranges ////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitPackedDimensionRange(ctx: SystemVerilogParser.PackedDimensionRangeContext?): Type? {
        return TypeCaster.castTypeFromPackedDimensionRange(ctx!!, castContext)
    }

// A.8.3 Expressions ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitConstantPrimaryLiteral(ctx: SystemVerilogParser.ConstantPrimaryLiteralContext?): Castable? {
        return TypeCaster.castTypeFromConstantPrimaryLiteral(ctx!!)
    }

    override fun visitConstantRange(ctx: SystemVerilogParser.ConstantRangeContext?): Type? {
        return TypeCaster.castTypeFromConstantRange(ctx!!, castContext)
    }
}
