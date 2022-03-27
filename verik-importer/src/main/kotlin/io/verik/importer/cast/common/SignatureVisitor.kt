/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.common

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.antlr.SystemVerilogParserBaseVisitor
import org.antlr.v4.runtime.RuleContext
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

class SignatureVisitor(
    private val name: String
) : SystemVerilogParserBaseVisitor<Unit>() {

    val signatureFragments = ArrayList<SignatureFragment>()

    init {
        add(SignatureFragmentKind.NULL)
    }

    override fun visitTerminal(node: TerminalNode?) {
        val signatureFragment = when (val text = node!!.text) {
            ";" -> SignatureFragment(SignatureFragmentKind.SEMICOLON)
            ":" -> SignatureFragment(SignatureFragmentKind.COLON)
            "::" -> SignatureFragment(SignatureFragmentKind.COLON_COLON)
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
        accept(ctx.parameterPortList())
        accept(ctx.listOfPorts())
        accept(ctx.SEMICOLON())
    }

    override fun visitModuleAnsiHeader(ctx: SystemVerilogParser.ModuleAnsiHeaderContext?) {
        accept(ctx!!.moduleKeyword())
        accept(ctx.lifetime())
        add(SignatureFragmentKind.NAME)
        accept(ctx.parameterPortList())
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
    }

// A.1.3 Module Parameters and Ports ///////////////////////////////////////////////////////////////////////////////////

    override fun visitParameterPortListParamAssign(ctx: SystemVerilogParser.ParameterPortListParamAssignContext?) {
        accept(ctx!!.SHARP())
        acceptWrapParenthesisBreak(ctx.listOfParamAssignments().paramAssignment() + ctx.parameterPortDeclaration())
    }

    override fun visitParameterPortListDeclaration(ctx: SystemVerilogParser.ParameterPortListDeclarationContext?) {
        accept(ctx!!.SHARP())
        acceptWrapParenthesisBreak(ctx.parameterPortDeclaration())
    }

    override fun visitListOfPorts(ctx: SystemVerilogParser.ListOfPortsContext?) {
        acceptWrapParenthesisBreak(ctx!!.port())
    }

    override fun visitListOfPortDeclarations(ctx: SystemVerilogParser.ListOfPortDeclarationsContext?) {
        acceptWrapParenthesisBreak(ctx!!.ansiPortDeclaration())
    }

// A.1.9 Class Items ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitClassConstructorPrototype(ctx: SystemVerilogParser.ClassConstructorPrototypeContext?) {
        accept(ctx!!.FUNCTION())
        add(SignatureFragmentKind.NAME)
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        acceptWrapParenthesisBreak(tfPortItems)
        accept(ctx.SEMICOLON())
    }

    override fun visitClassConstructorDeclaration(ctx: SystemVerilogParser.ClassConstructorDeclarationContext?) {
        accept(ctx!!.FUNCTION())
        accept(ctx.classScope())
        add(SignatureFragmentKind.NAME)
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        acceptWrapParenthesisBreak(tfPortItems)
        accept(ctx.SEMICOLON()[0])
    }

// A.2.1.3 Type Declarations ///////////////////////////////////////////////////////////////////////////////////////////

    override fun visitDataDeclarationData(ctx: SystemVerilogParser.DataDeclarationDataContext?) {
        accept(ctx!!.CONST())
        accept(ctx.VAR())
        accept(ctx.lifetime())
        accept(ctx.dataTypeOrImplicit())
        accept(ctx.listOfVariableDeclAssignments())
        accept(ctx.SEMICOLON())
    }

// A.2.2.1 Net and Variable Types //////////////////////////////////////////////////////////////////////////////////////

    override fun visitDataTypeStruct(ctx: SystemVerilogParser.DataTypeStructContext?) {
        accept(ctx!!.structUnion())
        accept(ctx.PACKED())
        accept(ctx.signing())
        acceptWrapBraceBreak(ctx.structUnionMember(), false)
        accept(ctx.packedDimension())
    }

    override fun visitDataTypeEnum(ctx: SystemVerilogParser.DataTypeEnumContext?) {
        accept(ctx!!.ENUM())
        accept(ctx.enumBaseType())
        acceptWrapBraceBreak(ctx.enumNameDeclaration(), true)
        accept(ctx.packedDimension())
    }

    override fun visitStructUnionMember(ctx: SystemVerilogParser.StructUnionMemberContext?) {
        accept(ctx!!.randomQualifier())
        accept(ctx.dataTypeOrVoid())
        ctx.listOfVariableDeclAssignments().children.forEach { child ->
            when (child) {
                is SystemVerilogParser.VariableDeclAssignmentContext -> { child.children.forEach { accept(it) } }
                else -> accept(child)
            }
        }
        accept(ctx.SEMICOLON())
    }

// A.2.3 Declaration Lists /////////////////////////////////////////////////////////////////////////////////////////////

    override fun visitListOfVariableDeclAssignments(ctx: SystemVerilogParser.ListOfVariableDeclAssignmentsContext?) {
        accept(ctx!!.variableDeclAssignment())
    }

// A.2.4 Declaration Assignments ///////////////////////////////////////////////////////////////////////////////////////

    override fun visitVariableDeclAssignmentVariable(ctx: SystemVerilogParser.VariableDeclAssignmentVariableContext?) {
        if (ctx!!.variableIdentifier().text == name) {
            add(SignatureFragmentKind.NAME)
            accept(ctx.variableDimension())
            accept(ctx.EQ())
            accept(ctx.expression())
        }
    }

// A.2.6 Function Declarations /////////////////////////////////////////////////////////////////////////////////////////

    @Suppress("DuplicatedCode")
    override fun visitFunctionBodyDeclarationNoPortList(
        ctx: SystemVerilogParser.FunctionBodyDeclarationNoPortListContext?
    ) {
        accept(ctx!!.functionDataTypeOrImplicit())
        accept(ctx.interfaceIdentifier())
        accept(ctx.DOT())
        accept(ctx.classScope())
        add(SignatureFragmentKind.NAME)
        accept(ctx.SEMICOLON())
        val tfPortDeclarations = ctx.tfItemDeclaration()
            .mapNotNull { it.tfPortDeclaration() }
        acceptWrapBreak(tfPortDeclarations)
        add(SignatureFragmentKind.BREAK)
        accept(ctx.ENDFUNCTION())
    }

    override fun visitFunctionBodyDeclarationPortList(
        ctx: SystemVerilogParser.FunctionBodyDeclarationPortListContext?
    ) {
        accept(ctx!!.functionDataTypeOrImplicit())
        accept(ctx.interfaceIdentifier())
        accept(ctx.DOT())
        accept(ctx.classScope())
        add(SignatureFragmentKind.NAME)
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        acceptWrapParenthesisBreak(tfPortItems)
        accept(ctx.SEMICOLON())
    }

    override fun visitFunctionPrototype(ctx: SystemVerilogParser.FunctionPrototypeContext?) {
        accept(ctx!!.FUNCTION())
        accept(ctx.dataTypeOrVoid())
        add(SignatureFragmentKind.NAME)
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        acceptWrapParenthesisBreak(tfPortItems)
    }

// A.2.7 Task Declarations /////////////////////////////////////////////////////////////////////////////////////////////

    @Suppress("DuplicatedCode")
    override fun visitTaskBodyDeclarationNoPortList(ctx: SystemVerilogParser.TaskBodyDeclarationNoPortListContext?) {
        accept(ctx!!.interfaceIdentifier())
        accept(ctx.DOT())
        accept(ctx.classScope())
        add(SignatureFragmentKind.NAME)
        accept(ctx.SEMICOLON())
        val tfPortDeclarations = ctx.tfItemDeclaration()
            .mapNotNull { it.tfPortDeclaration() }
        acceptWrapBreak(tfPortDeclarations)
        add(SignatureFragmentKind.BREAK)
        accept(ctx.ENDTASK())
    }

    override fun visitTaskBodyDeclarationPortList(ctx: SystemVerilogParser.TaskBodyDeclarationPortListContext?) {
        accept(ctx!!.interfaceIdentifier())
        accept(ctx.DOT())
        accept(ctx.classScope())
        add(SignatureFragmentKind.NAME)
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        acceptWrapParenthesisBreak(tfPortItems)
        accept(ctx.SEMICOLON())
    }

    override fun visitTaskPrototype(ctx: SystemVerilogParser.TaskPrototypeContext?) {
        accept(ctx!!.TASK())
        add(SignatureFragmentKind.NAME)
        val tfPortItems = ctx.tfPortList()?.tfPortItem() ?: listOf()
        acceptWrapParenthesisBreak(tfPortItems)
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

    private fun acceptWrapBraceBreak(ctxs: List<RuleContext>, isCommaBreak: Boolean) {
        if (ctxs.isEmpty()) {
            add(SignatureFragmentKind.LBRACE)
            add(SignatureFragmentKind.RBRACE)
        } else {
            add(SignatureFragmentKind.LBRACE_BREAK)
            accept(ctxs[0])
            ctxs.drop(1).forEach {
                if (isCommaBreak) {
                    add(SignatureFragmentKind.COMMA_BREAK)
                } else {
                    add(SignatureFragmentKind.BREAK)
                }
                accept(it)
            }
            add(SignatureFragmentKind.RBRACE_BREAK)
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
