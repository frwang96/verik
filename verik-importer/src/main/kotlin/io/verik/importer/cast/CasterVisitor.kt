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
import io.verik.importer.ast.element.EElement
import io.verik.importer.ast.element.EModule
import io.verik.importer.ast.element.EPort
import io.verik.importer.ast.element.EProperty
import io.verik.importer.ast.property.Type
import io.verik.importer.common.Castable
import io.verik.importer.message.Messages
import org.antlr.v4.runtime.RuleContext

class CasterVisitor(
    val castContext: CastContext
) : SystemVerilogParserBaseVisitor<Castable>() {

    inline fun <reified E : EElement> getElement(ctx: RuleContext): E? {
        return when (val castable = ctx.accept(this)) {
            null -> null
            is EElement -> castable.cast()
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

    override fun visitModuleDeclarationNonAnsi(ctx: SystemVerilogParser.ModuleDeclarationNonAnsiContext?): EModule? {
        return ModuleCaster.castModuleFromModuleDeclarationNonAnsi(ctx!!, castContext)
    }

    override fun visitModuleDeclarationAnsi(ctx: SystemVerilogParser.ModuleDeclarationAnsiContext?): EModule? {
        return ModuleCaster.castModuleFromModuleDeclarationAnsi(ctx!!, castContext)
    }

    override fun visitDataDeclaration(ctx: SystemVerilogParser.DataDeclarationContext?): EProperty? {
        return PropertyCaster.castProperty(ctx!!, castContext)
    }

// A.1.3 Module Parameters and Ports ///////////////////////////////////////////////////////////////////////////////////

    override fun visitPortDeclaration(ctx: SystemVerilogParser.PortDeclarationContext?): EPort? {
        return PortCaster.castPortFromPortDeclaration(ctx!!, castContext)
    }

    override fun visitAnsiPortDeclaration(ctx: SystemVerilogParser.AnsiPortDeclarationContext?): EPort? {
        return PortCaster.castPortFromAnsiPortDeclaration(ctx!!, castContext)
    }

// A.2.1.2 Port Declarations ///////////////////////////////////////////////////////////////////////////////////////////

    override fun visitInputDeclaration(ctx: SystemVerilogParser.InputDeclarationContext?): EPort? {
        return PortCaster.castPortFromInputDeclaration(ctx!!, castContext)
    }

    override fun visitOutputDeclaration(ctx: SystemVerilogParser.OutputDeclarationContext?): EPort? {
        return PortCaster.castPortFromOutputDeclaration(ctx!!, castContext)
    }

// A.2.2.1 Net and Variable Types //////////////////////////////////////////////////////////////////////////////////////

    override fun visitDataType(ctx: SystemVerilogParser.DataTypeContext?): Type? {
        return TypeCaster.castTypeFromDataType(ctx!!, castContext)
    }

    override fun visitDataTypeOrImplicit(ctx: SystemVerilogParser.DataTypeOrImplicitContext?): Type? {
        return TypeCaster.castTypeFromDataTypeOrImplicit(ctx!!, castContext)
    }

    override fun visitImplicitDataType(ctx: SystemVerilogParser.ImplicitDataTypeContext?): Type? {
        return TypeCaster.castTypeFromImplicitDataType(ctx!!, castContext)
    }

// A.2.5 Declaration Ranges ////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitPackedDimension(ctx: SystemVerilogParser.PackedDimensionContext?): Type? {
        return TypeCaster.castTypeFromPackedDimension(ctx!!, castContext)
    }

// A.8.3 Expressions ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitConstantExpression(ctx: SystemVerilogParser.ConstantExpressionContext?): Type? {
        return TypeCaster.castTypeFromConstantExpression(ctx!!)
    }

    override fun visitConstantRange(ctx: SystemVerilogParser.ConstantRangeContext?): Type? {
        return TypeCaster.castTypeFromConstantRange(ctx!!, castContext)
    }
}
