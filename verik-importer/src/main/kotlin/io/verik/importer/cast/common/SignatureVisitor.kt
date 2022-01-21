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
import org.antlr.v4.runtime.RuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

class SignatureVisitor : SystemVerilogParserBaseVisitor<Unit>() {

    val signatureFragments = ArrayList<SignatureFragment>()

    init {
        add(SignatureFragmentKind.NULL)
    }

    override fun visitTerminal(node: TerminalNode?) {
        val signatureFragment = when (val text = node!!.text) {
            ";" -> SignatureFragment(SignatureFragmentKind.SEMICOLON)
            ":" -> SignatureFragment(SignatureFragmentKind.COLON)
            "#" -> SignatureFragment(SignatureFragmentKind.SHARP)
            "," -> SignatureFragment(SignatureFragmentKind.COMMA)
            "[" -> SignatureFragment(SignatureFragmentKind.LBRACK)
            "]" -> SignatureFragment(SignatureFragmentKind.RBRACK)
            "(" -> SignatureFragment(SignatureFragmentKind.LPAREN)
            ")" -> SignatureFragment(SignatureFragmentKind.RPAREN)
            else -> SignatureFragment(text)
        }
        signatureFragments.add(signatureFragment)
    }

// A.1.2 SystemVerilog Source Text /////////////////////////////////////////////////////////////////////////////////////

    override fun visitModuleNonAnsiHeader(ctx: SystemVerilogParser.ModuleNonAnsiHeaderContext?) {
        accept(ctx!!.moduleKeyword())
        accept(ctx.lifetime())
        add(SignatureFragmentKind.NAME)
        accept(ctx.listOfPorts())
        accept(ctx.SEMICOLON())
    }

    override fun visitModuleAnsiHeader(ctx: SystemVerilogParser.ModuleAnsiHeaderContext?) {
        accept(ctx!!.moduleKeyword())
        accept(ctx.lifetime())
        add(SignatureFragmentKind.NAME)
        accept(ctx.listOfPortDeclarations())
        accept(ctx.SEMICOLON())
    }

    override fun visitModuleDeclarationNonAnsi(ctx: SystemVerilogParser.ModuleDeclarationNonAnsiContext?) {
        accept(ctx!!.moduleNonAnsiHeader())
        val moduleItemPortDeclarations = ctx.moduleItem()
            .filterIsInstance<SystemVerilogParser.ModuleItemPortDeclarationContext>()
        acceptWrapBreak(moduleItemPortDeclarations)
        add(SignatureFragmentKind.BREAK)
        accept(ctx.ENDMODULE())
    }

    override fun visitModuleDeclarationAnsi(ctx: SystemVerilogParser.ModuleDeclarationAnsiContext?) {
        accept(ctx!!.moduleAnsiHeader())
        add(SignatureFragmentKind.BREAK)
        accept(ctx.ENDMODULE())
    }

    override fun visitClassDeclaration(ctx: SystemVerilogParser.ClassDeclarationContext?) {
        accept(ctx!!.VIRTUAL())
        accept(ctx.CLASS())
        accept(ctx.lifetime())
        add(SignatureFragmentKind.NAME)
        accept(ctx.parameterPortList())
        accept(ctx.EXTENDS())
        accept(ctx.classType())
        accept(ctx.LPAREN())
        accept(ctx.listOfArguments())
        accept(ctx.RPAREN())
        accept(ctx.SEMICOLON())
        add(SignatureFragmentKind.BREAK)
        accept(ctx.ENDCLASS())
    }

// A.1.3 Module Parameters and Ports ///////////////////////////////////////////////////////////////////////////////////

    override fun visitListOfPorts(ctx: SystemVerilogParser.ListOfPortsContext?) {
        acceptWrapParenthesisBreak(ctx!!.port())
    }

    override fun visitListOfPortDeclarations(ctx: SystemVerilogParser.ListOfPortDeclarationsContext?) {
        acceptWrapParenthesisBreak(ctx!!.ansiPortDeclaration())
    }

// A.2.1.3 Type Declarations ///////////////////////////////////////////////////////////////////////////////////////////

    override fun visitDataDeclarationData(ctx: SystemVerilogParser.DataDeclarationDataContext?) {
        accept(ctx!!.CONST())
        accept(ctx.VAR())
        accept(ctx.lifetime())
        accept(ctx.dataTypeOrImplicit())
        add(SignatureFragmentKind.NAME)
        accept(ctx.SEMICOLON())
    }

// A.9.1 Attributes ////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitAttributeInstance(ctx: SystemVerilogParser.AttributeInstanceContext?) {}

    private fun add(kind: SignatureFragmentKind) {
        signatureFragments.add(SignatureFragment(kind))
    }

    private fun acceptWrapParenthesisBreak(ctxs: List<RuleContext>) {
        if (ctxs.isEmpty()) {
            add(SignatureFragmentKind.LPAREN)
            add(SignatureFragmentKind.RPAREN)
        } else {
            add(SignatureFragmentKind.LPAREN_BREAK)
            accept(ctxs[0])
            ctxs.drop(1).forEach {
                add(SignatureFragmentKind.COMMA_BREAK)
                accept(it)
            }
            add(SignatureFragmentKind.RPAREN_BREAK)
        }
    }

    private fun acceptWrapBreak(ctxs: List<RuleContext>) {
        if (ctxs.isNotEmpty()) {
            add(SignatureFragmentKind.INDENT_IN)
            add(SignatureFragmentKind.BREAK)
            accept(ctxs[0])
            ctxs.drop(1).forEach {
                add(SignatureFragmentKind.BREAK)
                accept(it)
            }
            add(SignatureFragmentKind.INDENT_OUT)
        }
    }

    fun accept(parseTree: ParseTree?) {
        if (parseTree != null) {
            visit(parseTree)
        }
    }

    fun accept(parseTrees: List<ParseTree>?) {
        parseTrees?.forEach { visit(it) }
    }
}
