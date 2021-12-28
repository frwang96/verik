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

package io.verik.importer.antlr;// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/antlr/io/verik/importer/antlr/SystemVerilogPreprocessorParser.g4 by ANTLR 4.9.2
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