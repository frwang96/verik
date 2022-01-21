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

package io.verik.importer.cast.common

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.antlr.SystemVerilogParserBaseVisitor
import io.verik.importer.ast.sv.element.common.SvElement
import io.verik.importer.ast.sv.element.declaration.SvClass
import io.verik.importer.ast.sv.element.declaration.SvModule
import io.verik.importer.ast.sv.element.declaration.SvPackage
import io.verik.importer.ast.sv.element.declaration.SvPort
import io.verik.importer.ast.sv.element.declaration.SvProperty
import io.verik.importer.ast.sv.element.descriptor.SvDescriptor
import io.verik.importer.ast.sv.element.expression.SvExpression
import io.verik.importer.cast.cast.ClassCaster
import io.verik.importer.cast.cast.DescriptorCaster
import io.verik.importer.cast.cast.ExpressionCaster
import io.verik.importer.cast.cast.ModuleCaster
import io.verik.importer.cast.cast.PackageCaster
import io.verik.importer.cast.cast.PortCaster
import io.verik.importer.cast.cast.PropertyCaster
import org.antlr.v4.runtime.RuleContext

class CasterVisitor(
    val castContext: CastContext
) : SystemVerilogParserBaseVisitor<SvElement>() {

    inline fun <reified E : SvElement> getElement(ctx: RuleContext): E? {
        return ctx.accept(this)?.cast()
    }

    override fun aggregateResult(aggregate: SvElement?, nextResult: SvElement?): SvElement? {
        return nextResult ?: aggregate
    }

// A.1.2 SystemVerilog Source Text /////////////////////////////////////////////////////////////////////////////////////

    override fun visitModuleDeclarationNonAnsi(ctx: SystemVerilogParser.ModuleDeclarationNonAnsiContext?): SvModule? {
        return ModuleCaster.castModuleFromModuleDeclarationNonAnsi(ctx!!, castContext)
    }

    override fun visitModuleDeclarationAnsi(ctx: SystemVerilogParser.ModuleDeclarationAnsiContext?): SvModule? {
        return ModuleCaster.castModuleFromModuleDeclarationAnsi(ctx!!, castContext)
    }

    override fun visitClassDeclaration(ctx: SystemVerilogParser.ClassDeclarationContext?): SvClass {
        return ClassCaster.castClassFromClassDeclaration(ctx!!, castContext)
    }

    override fun visitPackageDeclaration(ctx: SystemVerilogParser.PackageDeclarationContext?): SvPackage {
        return PackageCaster.castPackageFromPackageDeclaration(ctx!!, castContext)
    }

// A.1.3 Module Parameters and Ports ///////////////////////////////////////////////////////////////////////////////////

    override fun visitAnsiPortDeclaration(ctx: SystemVerilogParser.AnsiPortDeclarationContext?): SvPort? {
        return PortCaster.castPortFromAnsiPortDeclaration(ctx!!, castContext)
    }

// A.1.9 Class Items ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitClassMethodExternMethod(ctx: SystemVerilogParser.ClassMethodExternMethodContext?): SvElement? {
        return null
    }

    override fun visitClassMethodExternConstructor(
        ctx: SystemVerilogParser.ClassMethodExternConstructorContext?
    ): SvElement? {
        return null
    }

    override fun visitClassConstructorDeclaration(
        ctx: SystemVerilogParser.ClassConstructorDeclarationContext?
    ): SvElement? {
        return null
    }

// A.2.1.1 Module Parameter Declarations ///////////////////////////////////////////////////////////////////////////////

    override fun visitParameterDeclaration(ctx: SystemVerilogParser.ParameterDeclarationContext?): SvElement? {
        return null
    }

// A.2.1.2 Port Declarations ///////////////////////////////////////////////////////////////////////////////////////////

    override fun visitInputDeclarationNet(ctx: SystemVerilogParser.InputDeclarationNetContext?): SvPort? {
        return PortCaster.castPortFromInputDeclarationNet(ctx!!, castContext)
    }

    override fun visitOutputDeclarationNet(ctx: SystemVerilogParser.OutputDeclarationNetContext?): SvPort? {
        return PortCaster.castPortFromOutputDeclarationNet(ctx!!, castContext)
    }

// A.2.1.3 Type Declarations ///////////////////////////////////////////////////////////////////////////////////////////

    override fun visitDataDeclarationData(ctx: SystemVerilogParser.DataDeclarationDataContext?): SvProperty? {
        return PropertyCaster.castPropertyFromDataDeclarationData(ctx!!, castContext)
    }

    override fun visitTypeDeclarationData(ctx: SystemVerilogParser.TypeDeclarationDataContext?): SvElement? {
        return null
    }

    override fun visitTypeDeclarationMisc(ctx: SystemVerilogParser.TypeDeclarationMiscContext?): SvElement? {
        return null
    }

// A.2.2.1 Net and Variable Types //////////////////////////////////////////////////////////////////////////////////////

    override fun visitDataTypeVector(ctx: SystemVerilogParser.DataTypeVectorContext?): SvDescriptor? {
        return DescriptorCaster.castDescriptorFromDataTypeVector(ctx!!, castContext)
    }

    override fun visitImplicitDataType(ctx: SystemVerilogParser.ImplicitDataTypeContext?): SvDescriptor? {
        return DescriptorCaster.castDescriptorFromImplicitDataType(ctx!!, castContext)
    }

// A.2.6 Function Declarations /////////////////////////////////////////////////////////////////////////////////////////

    override fun visitFunctionDeclaration(ctx: SystemVerilogParser.FunctionDeclarationContext?): SvElement? {
        return null
    }

    override fun visitFunctionPrototype(ctx: SystemVerilogParser.FunctionPrototypeContext?): SvElement? {
        return null
    }

// A.2.7 Task Declarations /////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitTaskDeclaration(ctx: SystemVerilogParser.TaskDeclarationContext?): SvElement? {
        return null
    }

    override fun visitTaskPrototype(ctx: SystemVerilogParser.TaskPrototypeContext?): SvElement {
        return super.visitTaskPrototype(ctx)
    }

// A.6.2 Procedural Blocks and Assignments /////////////////////////////////////////////////////////////////////////////

    override fun visitInitialConstruct(ctx: SystemVerilogParser.InitialConstructContext?): SvElement? {
        return null
    }

    override fun visitAlwaysConstruct(ctx: SystemVerilogParser.AlwaysConstructContext?): SvElement? {
        return null
    }

// A.8.4 Primaries /////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitConstantPrimaryLiteral(ctx: SystemVerilogParser.ConstantPrimaryLiteralContext?): SvExpression {
        return ExpressionCaster.castExpressionFromConstantPrimaryLiteral(ctx!!, castContext)
    }
}
