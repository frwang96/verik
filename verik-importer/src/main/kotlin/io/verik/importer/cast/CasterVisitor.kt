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
import io.verik.importer.ast.element.EDeclaration
import io.verik.importer.ast.element.EElement
import io.verik.importer.ast.element.EModule
import io.verik.importer.ast.element.EPort
import io.verik.importer.ast.element.EProperty
import io.verik.importer.message.Messages
import org.antlr.v4.runtime.RuleContext
import org.antlr.v4.runtime.tree.ParseTree

class CasterVisitor(
    private val castContext: CastContext
) : SystemVerilogParserBaseVisitor<EElement>() {

    inline fun <reified E : EElement> getElement(element: RuleContext): E? {
        val castedElement = element.accept(this)
        return castedElement?.cast()
    }

    fun getDeclaration(declaration: ParseTree): EDeclaration? {
        return when (val element = declaration.accept(this)) {
            null -> null
            is EDeclaration -> element
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Declaration expected but got: ${element::class.simpleName}")
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
}
