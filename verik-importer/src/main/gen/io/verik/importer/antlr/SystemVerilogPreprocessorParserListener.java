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
	 * Enter a parse tree produced by {@link SystemVerilogPreprocessorParser#unescapedDirective}.
	 * @param ctx the parse tree
	 */
	void enterUnescapedDirective(SystemVerilogPreprocessorParser.UnescapedDirectiveContext ctx);
	/**
	 * Exit a parse tree produced by {@link SystemVerilogPreprocessorParser#unescapedDirective}.
	 * @param ctx the parse tree
	 */
	void exitUnescapedDirective(SystemVerilogPreprocessorParser.UnescapedDirectiveContext ctx);
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