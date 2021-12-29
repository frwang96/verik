// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/SystemVerilogPreprocessorParser.g4 by ANTLR 4.9.2
package io.verik.importer.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SystemVerilogPreprocessorParser}.
 */
public interface SystemVerilogPreprocessorParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SystemVerilogPreprocessorParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(SystemVerilogPreprocessorParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogPreprocessorParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(SystemVerilogPreprocessorParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogPreprocessorParser#text}.
	 * @param ctx the parse tree
	 */
	void enterText(SystemVerilogPreprocessorParser.TextContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogPreprocessorParser#text}.
	 * @param ctx the parse tree
	 */
	void exitText(SystemVerilogPreprocessorParser.TextContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifndef}
	 * labeled alternative in {@link SystemVerilogPreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterIfndef(SystemVerilogPreprocessorParser.IfndefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifndef}
	 * labeled alternative in {@link SystemVerilogPreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitIfndef(SystemVerilogPreprocessorParser.IfndefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ifdef}
	 * labeled alternative in {@link SystemVerilogPreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterIfdef(SystemVerilogPreprocessorParser.IfdefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ifdef}
	 * labeled alternative in {@link SystemVerilogPreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitIfdef(SystemVerilogPreprocessorParser.IfdefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code endif}
	 * labeled alternative in {@link SystemVerilogPreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterEndif(SystemVerilogPreprocessorParser.EndifContext ctx);
	/**
	 * Exit a parse tree produced by the {@code endif}
	 * labeled alternative in {@link SystemVerilogPreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitEndif(SystemVerilogPreprocessorParser.EndifContext ctx);
	/**
	 * Enter a parse tree produced by the {@code timescale}
	 * labeled alternative in {@link SystemVerilogPreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterTimescale(SystemVerilogPreprocessorParser.TimescaleContext ctx);
	/**
	 * Exit a parse tree produced by the {@code timescale}
	 * labeled alternative in {@link SystemVerilogPreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitTimescale(SystemVerilogPreprocessorParser.TimescaleContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogPreprocessorParser#defineDirective}.
	 * @param ctx the parse tree
	 */
	void enterDefineDirective(SystemVerilogPreprocessorParser.DefineDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogPreprocessorParser#defineDirective}.
	 * @param ctx the parse tree
	 */
	void exitDefineDirective(SystemVerilogPreprocessorParser.DefineDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogPreprocessorParser#macroDirective}.
	 * @param ctx the parse tree
	 */
	void enterMacroDirective(SystemVerilogPreprocessorParser.MacroDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogPreprocessorParser#macroDirective}.
	 * @param ctx the parse tree
	 */
	void exitMacroDirective(SystemVerilogPreprocessorParser.MacroDirectiveContext ctx);
	/**
	 * Enter a parse tree produced by {@link SystemVerilogPreprocessorParser#code}.
	 * @param ctx the parse tree
	 */
	void enterCode(SystemVerilogPreprocessorParser.CodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogPreprocessorParser#code}.
	 * @param ctx the parse tree
	 */
	void exitCode(SystemVerilogPreprocessorParser.CodeContext ctx);
}