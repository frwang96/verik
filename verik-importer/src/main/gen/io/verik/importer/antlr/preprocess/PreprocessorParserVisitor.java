// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/preprocess/PreprocessorParser.g4 by ANTLR 4.9.2
package io.verik.importer.antlr.preprocess;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PreprocessorParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PreprocessorParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(PreprocessorParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText(PreprocessorParser.TextContext ctx);
	/**
	 * Visit a parse tree produced by the {@code directiveIfdef}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveIfdef(PreprocessorParser.DirectiveIfdefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code directiveIfndef}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveIfndef(PreprocessorParser.DirectiveIfndefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code directiveEndif}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveEndif(PreprocessorParser.DirectiveEndifContext ctx);
	/**
	 * Visit a parse tree produced by the {@code directiveIgnored}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveIgnored(PreprocessorParser.DirectiveIgnoredContext ctx);
	/**
	 * Visit a parse tree produced by the {@code directiveUndefineAll}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveUndefineAll(PreprocessorParser.DirectiveUndefineAllContext ctx);
	/**
	 * Visit a parse tree produced by the {@code directiveUndef}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveUndef(PreprocessorParser.DirectiveUndefContext ctx);
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#directiveDefine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveDefine(PreprocessorParser.DirectiveDefineContext ctx);
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#directiveDefineParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveDefineParam(PreprocessorParser.DirectiveDefineParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#parameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameters(PreprocessorParser.ParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(PreprocessorParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#directiveMacro}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveMacro(PreprocessorParser.DirectiveMacroContext ctx);
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#directiveMacroArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectiveMacroArg(PreprocessorParser.DirectiveMacroArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(PreprocessorParser.ArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(PreprocessorParser.ArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link PreprocessorParser#code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCode(PreprocessorParser.CodeContext ctx);
}