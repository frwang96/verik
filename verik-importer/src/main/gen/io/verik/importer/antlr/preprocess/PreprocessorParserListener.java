// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/preprocess/PreprocessorParser.g4 by ANTLR 4.9.2
package io.verik.importer.antlr.preprocess;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PreprocessorParser}.
 */
public interface PreprocessorParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(PreprocessorParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(PreprocessorParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#text}.
	 * @param ctx the parse tree
	 */
	void enterText(PreprocessorParser.TextContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#text}.
	 * @param ctx the parse tree
	 */
	void exitText(PreprocessorParser.TextContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directiveIfdef}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveIfdef(PreprocessorParser.DirectiveIfdefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directiveIfdef}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveIfdef(PreprocessorParser.DirectiveIfdefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directiveIfndef}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveIfndef(PreprocessorParser.DirectiveIfndefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directiveIfndef}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveIfndef(PreprocessorParser.DirectiveIfndefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directiveEndif}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveEndif(PreprocessorParser.DirectiveEndifContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directiveEndif}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveEndif(PreprocessorParser.DirectiveEndifContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directiveIgnored}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveIgnored(PreprocessorParser.DirectiveIgnoredContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directiveIgnored}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveIgnored(PreprocessorParser.DirectiveIgnoredContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directiveUndefineAll}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveUndefineAll(PreprocessorParser.DirectiveUndefineAllContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directiveUndefineAll}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveUndefineAll(PreprocessorParser.DirectiveUndefineAllContext ctx);
	/**
	 * Enter a parse tree produced by the {@code directiveUndef}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveUndef(PreprocessorParser.DirectiveUndefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code directiveUndef}
	 * labeled alternative in {@link PreprocessorParser#directive}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveUndef(PreprocessorParser.DirectiveUndefContext ctx);
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#directiveDefine}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveDefine(PreprocessorParser.DirectiveDefineContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#directiveDefine}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveDefine(PreprocessorParser.DirectiveDefineContext ctx);
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#directiveDefineArg}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveDefineArg(PreprocessorParser.DirectiveDefineArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#directiveDefineArg}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveDefineArg(PreprocessorParser.DirectiveDefineArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(PreprocessorParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(PreprocessorParser.ArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(PreprocessorParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(PreprocessorParser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#directiveMacro}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveMacro(PreprocessorParser.DirectiveMacroContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#directiveMacro}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveMacro(PreprocessorParser.DirectiveMacroContext ctx);
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#directiveMacroArg}.
	 * @param ctx the parse tree
	 */
	void enterDirectiveMacroArg(PreprocessorParser.DirectiveMacroArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#directiveMacroArg}.
	 * @param ctx the parse tree
	 */
	void exitDirectiveMacroArg(PreprocessorParser.DirectiveMacroArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#runArguments}.
	 * @param ctx the parse tree
	 */
	void enterRunArguments(PreprocessorParser.RunArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#runArguments}.
	 * @param ctx the parse tree
	 */
	void exitRunArguments(PreprocessorParser.RunArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#runArgument}.
	 * @param ctx the parse tree
	 */
	void enterRunArgument(PreprocessorParser.RunArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#runArgument}.
	 * @param ctx the parse tree
	 */
	void exitRunArgument(PreprocessorParser.RunArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PreprocessorParser#code}.
	 * @param ctx the parse tree
	 */
	void enterCode(PreprocessorParser.CodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PreprocessorParser#code}.
	 * @param ctx the parse tree
	 */
	void exitCode(PreprocessorParser.CodeContext ctx);
}