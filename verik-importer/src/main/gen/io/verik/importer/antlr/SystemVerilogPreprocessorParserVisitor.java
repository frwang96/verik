// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/SystemVerilogPreprocessorParser.g4 by ANTLR 4.9.2
package io.verik.importer.antlr;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SystemVerilogPreprocessorParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SystemVerilogPreprocessorParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SystemVerilogPreprocessorParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(SystemVerilogPreprocessorParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogPreprocessorParser#text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText(SystemVerilogPreprocessorParser.TextContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogPreprocessorParser#unescapedDirective}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnescapedDirective(SystemVerilogPreprocessorParser.UnescapedDirectiveContext ctx);
	/**
	 * Visit a parse tree produced by {@link SystemVerilogPreprocessorParser#code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCode(SystemVerilogPreprocessorParser.CodeContext ctx);
}