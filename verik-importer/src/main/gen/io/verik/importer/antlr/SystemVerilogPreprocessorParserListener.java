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
	 * Enter a parse tree produced by {@link SystemVerilogPreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirective(SystemVerilogPreprocessorParser.DirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogPreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirective(SystemVerilogPreprocessorParser.DirectiveContext ctx);
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