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
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.cast.cast.ClassCaster
import io.verik.importer.cast.cast.ConstructorCaster
import io.verik.importer.cast.cast.DescriptorCaster
import io.verik.importer.cast.cast.ExpressionCaster
import io.verik.importer.cast.cast.FunctionCaster
import io.verik.importer.cast.cast.ModuleCaster
import io.verik.importer.cast.cast.PackageCaster
import io.verik.importer.cast.cast.PortCaster
import io.verik.importer.cast.cast.PropertyCaster
import io.verik.importer.cast.cast.TaskCaster
import io.verik.importer.cast.cast.TypeDeclarationCaster
import io.verik.importer.cast.cast.TypeParameterCaster
import io.verik.importer.cast.cast.ValueParameterCaster
import org.antlr.v4.runtime.RuleContext

class CasterVisitor(
    val castContext: CastContext
) : SystemVerilogParserBaseVisitor<EElement>() {

    inline fun <reified E : EElement> getElement(ctx: RuleContext): E? {
        return ctx.accept(this)?.cast()
    }

    override fun aggregateResult(aggregate: EElement?, nextResult: EElement?): EElement? {
        return nextResult ?: aggregate
    }

// A.1.2 SystemVerilog Source Text /////////////////////////////////////////////////////////////////////////////////////

    override fun visitModuleDeclarationNonAnsi(ctx: SystemVerilogParser.ModuleDeclarationNonAnsiContext?): EElement? {
        return ModuleCaster.castModuleFromModuleDeclarationNonAnsi(ctx!!, castContext)
    }

    override fun visitModuleDeclarationAnsi(ctx: SystemVerilogParser.ModuleDeclarationAnsiContext?): EElement? {
        return ModuleCaster.castModuleFromModuleDeclarationAnsi(ctx!!, castContext)
    }

    override fun visitClassDeclaration(ctx: SystemVerilogParser.ClassDeclarationContext?): EElement? {
        return ClassCaster.castClassFromClassDeclaration(ctx!!, castContext)
    }

    override fun visitPackageDeclaration(ctx: SystemVerilogParser.PackageDeclarationContext?): EElement {
        return PackageCaster.castPackageFromPackageDeclaration(ctx!!, castContext)
    }

// A.1.3 Module Parameters and Ports ///////////////////////////////////////////////////////////////////////////////////

    override fun visitParameterPortListDeclaration(
        ctx: SystemVerilogParser.ParameterPortListDeclarationContext?
    ): EElement {
        return TypeParameterCaster.castTypeParametersFromParameterPortListDeclaration(ctx!!, castContext)
    }

    override fun visitParameterPortDeclarationType(
        ctx: SystemVerilogParser.ParameterPortDeclarationTypeContext?
    ): EElement {
        return TypeParameterCaster.castTypeParameterFromParameterPortDeclarationType(ctx!!, castContext)
    }

    override fun visitAnsiPortDeclaration(ctx: SystemVerilogParser.AnsiPortDeclarationContext?): EElement? {
        return PortCaster.castPortFromAnsiPortDeclaration(ctx!!, castContext)
    }

// A.1.9 Class Items ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitClassMethodTask(ctx: SystemVerilogParser.ClassMethodTaskContext?): EElement? {
        return TaskCaster.castTaskFromClassMethodTask(ctx!!, castContext)
    }

    override fun visitClassMethodFunction(ctx: SystemVerilogParser.ClassMethodFunctionContext?): EElement? {
        return FunctionCaster.castFunctionFromClassMethodFunction(ctx!!, castContext)
    }

    override fun visitClassMethodExternMethod(ctx: SystemVerilogParser.ClassMethodExternMethodContext?): EElement? {
        return FunctionCaster.castFunctionFromClassMethodExternMethod(ctx!!, castContext)
    }

    override fun visitClassMethodConstructor(ctx: SystemVerilogParser.ClassMethodConstructorContext?): EElement? {
        return ConstructorCaster.castConstructorFromClassMethodConstructor(ctx!!, castContext)
    }

    override fun visitClassMethodExternConstructor(
        ctx: SystemVerilogParser.ClassMethodExternConstructorContext?
    ): EElement {
        return ConstructorCaster.castConstructorFromClassMethodExternConstructorContext(ctx!!, castContext)
    }

    override fun visitClassConstructorDeclaration(
        ctx: SystemVerilogParser.ClassConstructorDeclarationContext?
    ): EElement? {
        return ConstructorCaster.castConstructorFromClassConstructorDeclaration(ctx!!, castContext)
    }

// A.1.10 Constraints //////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitConstraintDeclaration(ctx: SystemVerilogParser.ConstraintDeclarationContext?): EElement? {
        return null
    }

// A.2.1.1 Module Parameter Declarations ///////////////////////////////////////////////////////////////////////////////

    override fun visitParameterDeclaration(ctx: SystemVerilogParser.ParameterDeclarationContext?): EElement? {
        return null
    }

// A.2.1.2 Port Declarations ///////////////////////////////////////////////////////////////////////////////////////////

    override fun visitInputDeclarationNet(ctx: SystemVerilogParser.InputDeclarationNetContext?): EElement? {
        return PortCaster.castPortFromInputDeclarationNet(ctx!!, castContext)
    }

    override fun visitOutputDeclarationNet(ctx: SystemVerilogParser.OutputDeclarationNetContext?): EElement? {
        return PortCaster.castPortFromOutputDeclarationNet(ctx!!, castContext)
    }

// A.2.1.3 Type Declarations ///////////////////////////////////////////////////////////////////////////////////////////

    override fun visitDataDeclarationData(ctx: SystemVerilogParser.DataDeclarationDataContext?): EElement? {
        return PropertyCaster.castPropertiesFromDataDeclarationData(ctx!!, castContext)
    }

    override fun visitTypeDeclarationData(ctx: SystemVerilogParser.TypeDeclarationDataContext?): EElement? {
        return TypeDeclarationCaster.castTypeDeclarationFromTypeDeclarationData(ctx!!, castContext)
    }

    override fun visitTypeDeclarationMisc(ctx: SystemVerilogParser.TypeDeclarationMiscContext?): EElement? {
        return null
    }

// A.2.2.1 Net and Variable Types //////////////////////////////////////////////////////////////////////////////////////

    override fun visitDataTypeVector(ctx: SystemVerilogParser.DataTypeVectorContext?): EElement? {
        return DescriptorCaster.castDescriptorFromDataTypeVector(ctx!!, castContext)
    }

    override fun visitDataTypeInteger(ctx: SystemVerilogParser.DataTypeIntegerContext?): EElement? {
        return DescriptorCaster.castDescriptorFromDataTypeInteger(ctx!!, castContext)
    }

    override fun visitDataTypeString(ctx: SystemVerilogParser.DataTypeStringContext?): EElement {
        return DescriptorCaster.castDescriptorFromDataTypeString(ctx!!, castContext)
    }

    override fun visitDataTypeTypeIdentifier(ctx: SystemVerilogParser.DataTypeTypeIdentifierContext?): EElement {
        return DescriptorCaster.castDescriptorFromDataTypeTypeIdentifier(ctx!!, castContext)
    }

    override fun visitImplicitDataType(ctx: SystemVerilogParser.ImplicitDataTypeContext?): EElement? {
        return DescriptorCaster.castDescriptorFromImplicitDataType(ctx!!, castContext)
    }

    override fun visitClassType(ctx: SystemVerilogParser.ClassTypeContext?): EElement {
        return DescriptorCaster.castDescriptorFromClassType(ctx!!, castContext)
    }

    override fun visitDataTypeOrVoid(ctx: SystemVerilogParser.DataTypeOrVoidContext?): EElement? {
        return DescriptorCaster.castDescriptorFromDataTypeOrVoid(ctx!!, castContext)
    }

// A.2.5 Declaration Ranges ////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitQueueDimension(ctx: SystemVerilogParser.QueueDimensionContext?): EElement {
        return DescriptorCaster.castDescriptorFromQueueDimension(ctx!!, castContext)
    }

// A.2.6 Function Declarations /////////////////////////////////////////////////////////////////////////////////////////

    override fun visitFunctionDeclaration(ctx: SystemVerilogParser.FunctionDeclarationContext?): EElement? {
        return FunctionCaster.castFunctionFromFunctionDeclaration(ctx!!, castContext)
    }

    override fun visitFunctionBodyDeclarationNoPortList(
        ctx: SystemVerilogParser.FunctionBodyDeclarationNoPortListContext?
    ): EElement? {
        return FunctionCaster.castFunctionFromFunctionBodyDeclarationNoPortList(ctx!!, castContext)
    }

    override fun visitFunctionBodyDeclarationPortList(
        ctx: SystemVerilogParser.FunctionBodyDeclarationPortListContext?
    ): EElement? {
        return FunctionCaster.castFunctionFromFunctionBodyDeclarationPortList(ctx!!, castContext)
    }

    override fun visitFunctionPrototype(ctx: SystemVerilogParser.FunctionPrototypeContext?): EElement? {
        return FunctionCaster.castFunctionFromFunctionPrototype(ctx!!, castContext)
    }

// A.2.7 Task Declarations /////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitTaskDeclaration(ctx: SystemVerilogParser.TaskDeclarationContext?): EElement? {
        return TaskCaster.castTaskFromTaskDeclaration(ctx!!, castContext)
    }

    override fun visitTaskBodyDeclarationNoPortList(
        ctx: SystemVerilogParser.TaskBodyDeclarationNoPortListContext?
    ): EElement? {
        return TaskCaster.castTaskFromTaskBodyDeclarationNoPortList(ctx!!, castContext)
    }

    override fun visitTaskBodyDeclarationPortList(
        ctx: SystemVerilogParser.TaskBodyDeclarationPortListContext?
    ): EElement? {
        return TaskCaster.castTaskFromTaskBodyDeclarationPortList(ctx!!, castContext)
    }

    override fun visitTfPortItem(ctx: SystemVerilogParser.TfPortItemContext?): EElement? {
        return ValueParameterCaster.castValueParameterFromTfPortItem(ctx!!, castContext)
    }

    override fun visitTfPortDeclaration(ctx: SystemVerilogParser.TfPortDeclarationContext?): EElement? {
        return ValueParameterCaster.castValueParameterFromTfPortDeclaration(ctx!!, castContext)
    }

    override fun visitTaskPrototype(ctx: SystemVerilogParser.TaskPrototypeContext?): EElement {
        return TaskCaster.castTaskFromTaskPrototype(ctx!!, castContext)
    }

// A.6.2 Procedural Blocks and Assignments /////////////////////////////////////////////////////////////////////////////

    override fun visitInitialConstruct(ctx: SystemVerilogParser.InitialConstructContext?): EElement? {
        return null
    }

    override fun visitAlwaysConstruct(ctx: SystemVerilogParser.AlwaysConstructContext?): EElement? {
        return null
    }

// A.8.4 Primaries /////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitConstantPrimaryLiteral(ctx: SystemVerilogParser.ConstantPrimaryLiteralContext?): EElement {
        return ExpressionCaster.castLiteralExpressionFromConstantPrimaryLiteral(ctx!!, castContext)
    }

    override fun visitConstantPrimaryParameter(ctx: SystemVerilogParser.ConstantPrimaryParameterContext?): EElement {
        return ExpressionCaster.castReferenceExpressionFromConstantPrimaryParameter(ctx!!, castContext)
    }
}
