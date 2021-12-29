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

// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/SystemVerilogParser.g4 by ANTLR 4.9.2
package io.verik.importer.antlr.parse;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SystemVerilogParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		EQ=1, COMMA=2, COLON=3, SEMICOLON=4, UNDERSCORE=5, LBRACK=6, RBRACK=7, 
		LPAREN=8, RPAREN=9, LPAREN_STAR=10, RPAREN_STAR=11, ENDMODULE=12, INPUT=13, 
		LOGIC=14, MODULE=15, OUTPUT=16, SIGNED=17, UNSIGNED=18, WIRE=19, UNSIGNED_NUMBER=20, 
		STRING_LITERAL=21, SIMPLE_IDENTIFIER=22, WS=23;
	public static final int
		RULE_compilationUnit = 0, RULE_description = 1, RULE_moduleNonAnsiHeader = 2, 
		RULE_moduleAnsiHeader = 3, RULE_moduleDeclaration = 4, RULE_listOfPorts = 5, 
		RULE_listOfPortDeclarations = 6, RULE_portDeclaration = 7, RULE_port = 8, 
		RULE_portExpression = 9, RULE_portReference = 10, RULE_portDirection = 11, 
		RULE_netPortHeader = 12, RULE_ansiPortDeclaration = 13, RULE_moduleItem = 14, 
		RULE_packageItem = 15, RULE_packageOrGenerateItemDeclaration = 16, RULE_inputDeclaration = 17, 
		RULE_outputDeclaration = 18, RULE_dataDeclaration = 19, RULE_dataType = 20, 
		RULE_dataTypeOrImplicit = 21, RULE_implicitDataType = 22, RULE_integerType = 23, 
		RULE_integerVectorType = 24, RULE_netType = 25, RULE_netPortType = 26, 
		RULE_variablePortType = 27, RULE_varDataType = 28, RULE_signing = 29, 
		RULE_simpleType = 30, RULE_listOfVariableDeclAssignments = 31, RULE_variableDeclAssignment = 32, 
		RULE_packedDimension = 33, RULE_constantExpression = 34, RULE_constantRange = 35, 
		RULE_constantPrimary = 36, RULE_primaryLiteral = 37, RULE_number = 38, 
		RULE_integralNumber = 39, RULE_decimalNumber = 40, RULE_identifier = 41;
	private static String[] makeRuleNames() {
		return new String[] {
			"compilationUnit", "description", "moduleNonAnsiHeader", "moduleAnsiHeader", 
			"moduleDeclaration", "listOfPorts", "listOfPortDeclarations", "portDeclaration", 
			"port", "portExpression", "portReference", "portDirection", "netPortHeader", 
			"ansiPortDeclaration", "moduleItem", "packageItem", "packageOrGenerateItemDeclaration", 
			"inputDeclaration", "outputDeclaration", "dataDeclaration", "dataType", 
			"dataTypeOrImplicit", "implicitDataType", "integerType", "integerVectorType", 
			"netType", "netPortType", "variablePortType", "varDataType", "signing", 
			"simpleType", "listOfVariableDeclAssignments", "variableDeclAssignment", 
			"packedDimension", "constantExpression", "constantRange", "constantPrimary", 
			"primaryLiteral", "number", "integralNumber", "decimalNumber", "identifier"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'='", "','", "':'", "';'", "'_'", "'['", "']'", "'('", "')'", 
			"'(*'", "'*)'", "'endmodule'", "'input'", "'logic'", "'module'", "'output'", 
			"'signed'", "'unsigned'", "'wire'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "EQ", "COMMA", "COLON", "SEMICOLON", "UNDERSCORE", "LBRACK", "RBRACK", 
			"LPAREN", "RPAREN", "LPAREN_STAR", "RPAREN_STAR", "ENDMODULE", "INPUT", 
			"LOGIC", "MODULE", "OUTPUT", "SIGNED", "UNSIGNED", "WIRE", "UNSIGNED_NUMBER", 
			"STRING_LITERAL", "SIMPLE_IDENTIFIER", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "SystemVerilogParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SystemVerilogParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class CompilationUnitContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SystemVerilogParser.EOF, 0); }
		public List<DescriptionContext> description() {
			return getRuleContexts(DescriptionContext.class);
		}
		public DescriptionContext description(int i) {
			return getRuleContext(DescriptionContext.class,i);
		}
		public CompilationUnitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_compilationUnit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterCompilationUnit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitCompilationUnit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitCompilationUnit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CompilationUnitContext compilationUnit() throws RecognitionException {
		CompilationUnitContext _localctx = new CompilationUnitContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_compilationUnit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACK) | (1L << LOGIC) | (1L << MODULE) | (1L << SIGNED) | (1L << UNSIGNED) | (1L << SIMPLE_IDENTIFIER))) != 0)) {
				{
				{
				setState(84);
				description();
				}
				}
				setState(89);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(90);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DescriptionContext extends ParserRuleContext {
		public ModuleDeclarationContext moduleDeclaration() {
			return getRuleContext(ModuleDeclarationContext.class,0);
		}
		public PackageItemContext packageItem() {
			return getRuleContext(PackageItemContext.class,0);
		}
		public DescriptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_description; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterDescription(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitDescription(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitDescription(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescriptionContext description() throws RecognitionException {
		DescriptionContext _localctx = new DescriptionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_description);
		try {
			setState(94);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MODULE:
				enterOuterAlt(_localctx, 1);
				{
				setState(92);
				moduleDeclaration();
				}
				break;
			case LBRACK:
			case LOGIC:
			case SIGNED:
			case UNSIGNED:
			case SIMPLE_IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(93);
				packageItem();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleNonAnsiHeaderContext extends ParserRuleContext {
		public TerminalNode MODULE() { return getToken(SystemVerilogParser.MODULE, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public ListOfPortsContext listOfPorts() {
			return getRuleContext(ListOfPortsContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(SystemVerilogParser.SEMICOLON, 0); }
		public ModuleNonAnsiHeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleNonAnsiHeader; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterModuleNonAnsiHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitModuleNonAnsiHeader(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitModuleNonAnsiHeader(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleNonAnsiHeaderContext moduleNonAnsiHeader() throws RecognitionException {
		ModuleNonAnsiHeaderContext _localctx = new ModuleNonAnsiHeaderContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_moduleNonAnsiHeader);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			match(MODULE);
			setState(97);
			identifier();
			setState(98);
			listOfPorts();
			setState(99);
			match(SEMICOLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleAnsiHeaderContext extends ParserRuleContext {
		public TerminalNode MODULE() { return getToken(SystemVerilogParser.MODULE, 0); }
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(SystemVerilogParser.SEMICOLON, 0); }
		public ListOfPortDeclarationsContext listOfPortDeclarations() {
			return getRuleContext(ListOfPortDeclarationsContext.class,0);
		}
		public ModuleAnsiHeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleAnsiHeader; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterModuleAnsiHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitModuleAnsiHeader(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitModuleAnsiHeader(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleAnsiHeaderContext moduleAnsiHeader() throws RecognitionException {
		ModuleAnsiHeaderContext _localctx = new ModuleAnsiHeaderContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_moduleAnsiHeader);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(MODULE);
			setState(102);
			identifier();
			setState(104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(103);
				listOfPortDeclarations();
				}
			}

			setState(106);
			match(SEMICOLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleDeclarationContext extends ParserRuleContext {
		public ModuleNonAnsiHeaderContext moduleNonAnsiHeader() {
			return getRuleContext(ModuleNonAnsiHeaderContext.class,0);
		}
		public TerminalNode ENDMODULE() { return getToken(SystemVerilogParser.ENDMODULE, 0); }
		public List<ModuleItemContext> moduleItem() {
			return getRuleContexts(ModuleItemContext.class);
		}
		public ModuleItemContext moduleItem(int i) {
			return getRuleContext(ModuleItemContext.class,i);
		}
		public ModuleAnsiHeaderContext moduleAnsiHeader() {
			return getRuleContext(ModuleAnsiHeaderContext.class,0);
		}
		public ModuleDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterModuleDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitModuleDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitModuleDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleDeclarationContext moduleDeclaration() throws RecognitionException {
		ModuleDeclarationContext _localctx = new ModuleDeclarationContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_moduleDeclaration);
		int _la;
		try {
			setState(120);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(108);
				moduleNonAnsiHeader();
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==INPUT || _la==OUTPUT) {
					{
					{
					setState(109);
					moduleItem();
					}
					}
					setState(114);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(115);
				match(ENDMODULE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(117);
				moduleAnsiHeader();
				setState(118);
				match(ENDMODULE);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListOfPortsContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(SystemVerilogParser.LPAREN, 0); }
		public List<PortContext> port() {
			return getRuleContexts(PortContext.class);
		}
		public PortContext port(int i) {
			return getRuleContext(PortContext.class,i);
		}
		public TerminalNode RPAREN() { return getToken(SystemVerilogParser.RPAREN, 0); }
		public List<TerminalNode> COMMA() { return getTokens(SystemVerilogParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SystemVerilogParser.COMMA, i);
		}
		public ListOfPortsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listOfPorts; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterListOfPorts(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitListOfPorts(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitListOfPorts(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListOfPortsContext listOfPorts() throws RecognitionException {
		ListOfPortsContext _localctx = new ListOfPortsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_listOfPorts);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(LPAREN);
			setState(123);
			port();
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(124);
				match(COMMA);
				setState(125);
				port();
				}
				}
				setState(130);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(131);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListOfPortDeclarationsContext extends ParserRuleContext {
		public TerminalNode LPAREN() { return getToken(SystemVerilogParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(SystemVerilogParser.RPAREN, 0); }
		public List<AnsiPortDeclarationContext> ansiPortDeclaration() {
			return getRuleContexts(AnsiPortDeclarationContext.class);
		}
		public AnsiPortDeclarationContext ansiPortDeclaration(int i) {
			return getRuleContext(AnsiPortDeclarationContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SystemVerilogParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SystemVerilogParser.COMMA, i);
		}
		public ListOfPortDeclarationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listOfPortDeclarations; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterListOfPortDeclarations(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitListOfPortDeclarations(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitListOfPortDeclarations(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListOfPortDeclarationsContext listOfPortDeclarations() throws RecognitionException {
		ListOfPortDeclarationsContext _localctx = new ListOfPortDeclarationsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_listOfPortDeclarations);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			match(LPAREN);
			setState(142);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACK) | (1L << INPUT) | (1L << LOGIC) | (1L << OUTPUT) | (1L << SIGNED) | (1L << UNSIGNED) | (1L << WIRE) | (1L << SIMPLE_IDENTIFIER))) != 0)) {
				{
				setState(134);
				ansiPortDeclaration();
				setState(139);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(135);
					match(COMMA);
					setState(136);
					ansiPortDeclaration();
					}
					}
					setState(141);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(144);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PortDeclarationContext extends ParserRuleContext {
		public InputDeclarationContext inputDeclaration() {
			return getRuleContext(InputDeclarationContext.class,0);
		}
		public OutputDeclarationContext outputDeclaration() {
			return getRuleContext(OutputDeclarationContext.class,0);
		}
		public PortDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_portDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterPortDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitPortDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitPortDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PortDeclarationContext portDeclaration() throws RecognitionException {
		PortDeclarationContext _localctx = new PortDeclarationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_portDeclaration);
		try {
			setState(148);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INPUT:
				enterOuterAlt(_localctx, 1);
				{
				setState(146);
				inputDeclaration();
				}
				break;
			case OUTPUT:
				enterOuterAlt(_localctx, 2);
				{
				setState(147);
				outputDeclaration();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PortContext extends ParserRuleContext {
		public PortExpressionContext portExpression() {
			return getRuleContext(PortExpressionContext.class,0);
		}
		public PortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_port; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterPort(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitPort(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitPort(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PortContext port() throws RecognitionException {
		PortContext _localctx = new PortContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_port);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			portExpression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PortExpressionContext extends ParserRuleContext {
		public PortReferenceContext portReference() {
			return getRuleContext(PortReferenceContext.class,0);
		}
		public PortExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_portExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterPortExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitPortExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitPortExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PortExpressionContext portExpression() throws RecognitionException {
		PortExpressionContext _localctx = new PortExpressionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_portExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(152);
			portReference();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PortReferenceContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public PortReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_portReference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterPortReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitPortReference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitPortReference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PortReferenceContext portReference() throws RecognitionException {
		PortReferenceContext _localctx = new PortReferenceContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_portReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PortDirectionContext extends ParserRuleContext {
		public TerminalNode INPUT() { return getToken(SystemVerilogParser.INPUT, 0); }
		public TerminalNode OUTPUT() { return getToken(SystemVerilogParser.OUTPUT, 0); }
		public PortDirectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_portDirection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterPortDirection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitPortDirection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitPortDirection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PortDirectionContext portDirection() throws RecognitionException {
		PortDirectionContext _localctx = new PortDirectionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_portDirection);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			_la = _input.LA(1);
			if ( !(_la==INPUT || _la==OUTPUT) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NetPortHeaderContext extends ParserRuleContext {
		public NetPortTypeContext netPortType() {
			return getRuleContext(NetPortTypeContext.class,0);
		}
		public PortDirectionContext portDirection() {
			return getRuleContext(PortDirectionContext.class,0);
		}
		public NetPortHeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_netPortHeader; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterNetPortHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitNetPortHeader(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitNetPortHeader(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NetPortHeaderContext netPortHeader() throws RecognitionException {
		NetPortHeaderContext _localctx = new NetPortHeaderContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_netPortHeader);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==INPUT || _la==OUTPUT) {
				{
				setState(158);
				portDirection();
				}
			}

			setState(161);
			netPortType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AnsiPortDeclarationContext extends ParserRuleContext {
		public NetPortHeaderContext netPortHeader() {
			return getRuleContext(NetPortHeaderContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public AnsiPortDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ansiPortDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterAnsiPortDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitAnsiPortDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitAnsiPortDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnsiPortDeclarationContext ansiPortDeclaration() throws RecognitionException {
		AnsiPortDeclarationContext _localctx = new AnsiPortDeclarationContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_ansiPortDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(163);
			netPortHeader();
			setState(164);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModuleItemContext extends ParserRuleContext {
		public PortDeclarationContext portDeclaration() {
			return getRuleContext(PortDeclarationContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(SystemVerilogParser.SEMICOLON, 0); }
		public ModuleItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterModuleItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitModuleItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitModuleItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleItemContext moduleItem() throws RecognitionException {
		ModuleItemContext _localctx = new ModuleItemContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_moduleItem);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			portDeclaration();
			setState(167);
			match(SEMICOLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PackageItemContext extends ParserRuleContext {
		public PackageOrGenerateItemDeclarationContext packageOrGenerateItemDeclaration() {
			return getRuleContext(PackageOrGenerateItemDeclarationContext.class,0);
		}
		public PackageItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packageItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterPackageItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitPackageItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitPackageItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PackageItemContext packageItem() throws RecognitionException {
		PackageItemContext _localctx = new PackageItemContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_packageItem);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			packageOrGenerateItemDeclaration();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PackageOrGenerateItemDeclarationContext extends ParserRuleContext {
		public DataDeclarationContext dataDeclaration() {
			return getRuleContext(DataDeclarationContext.class,0);
		}
		public PackageOrGenerateItemDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packageOrGenerateItemDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterPackageOrGenerateItemDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitPackageOrGenerateItemDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitPackageOrGenerateItemDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PackageOrGenerateItemDeclarationContext packageOrGenerateItemDeclaration() throws RecognitionException {
		PackageOrGenerateItemDeclarationContext _localctx = new PackageOrGenerateItemDeclarationContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_packageOrGenerateItemDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(171);
			dataDeclaration();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InputDeclarationContext extends ParserRuleContext {
		public TerminalNode INPUT() { return getToken(SystemVerilogParser.INPUT, 0); }
		public NetPortTypeContext netPortType() {
			return getRuleContext(NetPortTypeContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public VariablePortTypeContext variablePortType() {
			return getRuleContext(VariablePortTypeContext.class,0);
		}
		public InputDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterInputDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitInputDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitInputDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InputDeclarationContext inputDeclaration() throws RecognitionException {
		InputDeclarationContext _localctx = new InputDeclarationContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_inputDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			match(INPUT);
			setState(180);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(174);
				netPortType();
				setState(175);
				identifier();
				}
				break;
			case 2:
				{
				setState(177);
				variablePortType();
				setState(178);
				identifier();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OutputDeclarationContext extends ParserRuleContext {
		public TerminalNode OUTPUT() { return getToken(SystemVerilogParser.OUTPUT, 0); }
		public NetPortTypeContext netPortType() {
			return getRuleContext(NetPortTypeContext.class,0);
		}
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public VariablePortTypeContext variablePortType() {
			return getRuleContext(VariablePortTypeContext.class,0);
		}
		public OutputDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterOutputDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitOutputDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitOutputDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputDeclarationContext outputDeclaration() throws RecognitionException {
		OutputDeclarationContext _localctx = new OutputDeclarationContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_outputDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(OUTPUT);
			setState(189);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				setState(183);
				netPortType();
				setState(184);
				identifier();
				}
				break;
			case 2:
				{
				setState(186);
				variablePortType();
				setState(187);
				identifier();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DataDeclarationContext extends ParserRuleContext {
		public DataTypeOrImplicitContext dataTypeOrImplicit() {
			return getRuleContext(DataTypeOrImplicitContext.class,0);
		}
		public ListOfVariableDeclAssignmentsContext listOfVariableDeclAssignments() {
			return getRuleContext(ListOfVariableDeclAssignmentsContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(SystemVerilogParser.SEMICOLON, 0); }
		public DataDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterDataDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitDataDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitDataDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataDeclarationContext dataDeclaration() throws RecognitionException {
		DataDeclarationContext _localctx = new DataDeclarationContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_dataDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			dataTypeOrImplicit();
			setState(192);
			listOfVariableDeclAssignments();
			setState(193);
			match(SEMICOLON);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DataTypeContext extends ParserRuleContext {
		public IntegerVectorTypeContext integerVectorType() {
			return getRuleContext(IntegerVectorTypeContext.class,0);
		}
		public SigningContext signing() {
			return getRuleContext(SigningContext.class,0);
		}
		public List<PackedDimensionContext> packedDimension() {
			return getRuleContexts(PackedDimensionContext.class);
		}
		public PackedDimensionContext packedDimension(int i) {
			return getRuleContext(PackedDimensionContext.class,i);
		}
		public DataTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterDataType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitDataType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitDataType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataTypeContext dataType() throws RecognitionException {
		DataTypeContext _localctx = new DataTypeContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_dataType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			integerVectorType();
			setState(197);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SIGNED || _la==UNSIGNED) {
				{
				setState(196);
				signing();
				}
			}

			setState(202);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRACK) {
				{
				{
				setState(199);
				packedDimension();
				}
				}
				setState(204);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DataTypeOrImplicitContext extends ParserRuleContext {
		public DataTypeContext dataType() {
			return getRuleContext(DataTypeContext.class,0);
		}
		public ImplicitDataTypeContext implicitDataType() {
			return getRuleContext(ImplicitDataTypeContext.class,0);
		}
		public DataTypeOrImplicitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dataTypeOrImplicit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterDataTypeOrImplicit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitDataTypeOrImplicit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitDataTypeOrImplicit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DataTypeOrImplicitContext dataTypeOrImplicit() throws RecognitionException {
		DataTypeOrImplicitContext _localctx = new DataTypeOrImplicitContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_dataTypeOrImplicit);
		try {
			setState(207);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LOGIC:
				enterOuterAlt(_localctx, 1);
				{
				setState(205);
				dataType();
				}
				break;
			case LBRACK:
			case SIGNED:
			case UNSIGNED:
			case SIMPLE_IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(206);
				implicitDataType();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ImplicitDataTypeContext extends ParserRuleContext {
		public SigningContext signing() {
			return getRuleContext(SigningContext.class,0);
		}
		public List<PackedDimensionContext> packedDimension() {
			return getRuleContexts(PackedDimensionContext.class);
		}
		public PackedDimensionContext packedDimension(int i) {
			return getRuleContext(PackedDimensionContext.class,i);
		}
		public ImplicitDataTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_implicitDataType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterImplicitDataType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitImplicitDataType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitImplicitDataType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ImplicitDataTypeContext implicitDataType() throws RecognitionException {
		ImplicitDataTypeContext _localctx = new ImplicitDataTypeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_implicitDataType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==SIGNED || _la==UNSIGNED) {
				{
				setState(209);
				signing();
				}
			}

			setState(215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==LBRACK) {
				{
				{
				setState(212);
				packedDimension();
				}
				}
				setState(217);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntegerTypeContext extends ParserRuleContext {
		public IntegerVectorTypeContext integerVectorType() {
			return getRuleContext(IntegerVectorTypeContext.class,0);
		}
		public IntegerTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integerType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterIntegerType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitIntegerType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitIntegerType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntegerTypeContext integerType() throws RecognitionException {
		IntegerTypeContext _localctx = new IntegerTypeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_integerType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
			integerVectorType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntegerVectorTypeContext extends ParserRuleContext {
		public TerminalNode LOGIC() { return getToken(SystemVerilogParser.LOGIC, 0); }
		public IntegerVectorTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integerVectorType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterIntegerVectorType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitIntegerVectorType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitIntegerVectorType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntegerVectorTypeContext integerVectorType() throws RecognitionException {
		IntegerVectorTypeContext _localctx = new IntegerVectorTypeContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_integerVectorType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			match(LOGIC);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NetTypeContext extends ParserRuleContext {
		public TerminalNode WIRE() { return getToken(SystemVerilogParser.WIRE, 0); }
		public NetTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_netType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterNetType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitNetType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitNetType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NetTypeContext netType() throws RecognitionException {
		NetTypeContext _localctx = new NetTypeContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_netType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			match(WIRE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NetPortTypeContext extends ParserRuleContext {
		public DataTypeOrImplicitContext dataTypeOrImplicit() {
			return getRuleContext(DataTypeOrImplicitContext.class,0);
		}
		public NetTypeContext netType() {
			return getRuleContext(NetTypeContext.class,0);
		}
		public NetPortTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_netPortType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterNetPortType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitNetPortType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitNetPortType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NetPortTypeContext netPortType() throws RecognitionException {
		NetPortTypeContext _localctx = new NetPortTypeContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_netPortType);
		try {
			setState(228);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LBRACK:
			case LOGIC:
			case SIGNED:
			case UNSIGNED:
			case SIMPLE_IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(224);
				dataTypeOrImplicit();
				}
				break;
			case WIRE:
				enterOuterAlt(_localctx, 2);
				{
				setState(225);
				netType();
				setState(226);
				dataTypeOrImplicit();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariablePortTypeContext extends ParserRuleContext {
		public VarDataTypeContext varDataType() {
			return getRuleContext(VarDataTypeContext.class,0);
		}
		public VariablePortTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variablePortType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterVariablePortType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitVariablePortType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitVariablePortType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariablePortTypeContext variablePortType() throws RecognitionException {
		VariablePortTypeContext _localctx = new VariablePortTypeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_variablePortType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			varDataType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarDataTypeContext extends ParserRuleContext {
		public DataTypeContext dataType() {
			return getRuleContext(DataTypeContext.class,0);
		}
		public VarDataTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varDataType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterVarDataType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitVarDataType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitVarDataType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarDataTypeContext varDataType() throws RecognitionException {
		VarDataTypeContext _localctx = new VarDataTypeContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_varDataType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(232);
			dataType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SigningContext extends ParserRuleContext {
		public TerminalNode SIGNED() { return getToken(SystemVerilogParser.SIGNED, 0); }
		public TerminalNode UNSIGNED() { return getToken(SystemVerilogParser.UNSIGNED, 0); }
		public SigningContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_signing; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterSigning(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitSigning(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitSigning(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SigningContext signing() throws RecognitionException {
		SigningContext _localctx = new SigningContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_signing);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			_la = _input.LA(1);
			if ( !(_la==SIGNED || _la==UNSIGNED) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SimpleTypeContext extends ParserRuleContext {
		public IntegerTypeContext integerType() {
			return getRuleContext(IntegerTypeContext.class,0);
		}
		public SimpleTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterSimpleType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitSimpleType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitSimpleType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleTypeContext simpleType() throws RecognitionException {
		SimpleTypeContext _localctx = new SimpleTypeContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_simpleType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(236);
			integerType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListOfVariableDeclAssignmentsContext extends ParserRuleContext {
		public List<VariableDeclAssignmentContext> variableDeclAssignment() {
			return getRuleContexts(VariableDeclAssignmentContext.class);
		}
		public VariableDeclAssignmentContext variableDeclAssignment(int i) {
			return getRuleContext(VariableDeclAssignmentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(SystemVerilogParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(SystemVerilogParser.COMMA, i);
		}
		public ListOfVariableDeclAssignmentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listOfVariableDeclAssignments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterListOfVariableDeclAssignments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitListOfVariableDeclAssignments(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitListOfVariableDeclAssignments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListOfVariableDeclAssignmentsContext listOfVariableDeclAssignments() throws RecognitionException {
		ListOfVariableDeclAssignmentsContext _localctx = new ListOfVariableDeclAssignmentsContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_listOfVariableDeclAssignments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(238);
			variableDeclAssignment();
			setState(243);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(239);
				match(COMMA);
				setState(240);
				variableDeclAssignment();
				}
				}
				setState(245);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VariableDeclAssignmentContext extends ParserRuleContext {
		public IdentifierContext identifier() {
			return getRuleContext(IdentifierContext.class,0);
		}
		public VariableDeclAssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_variableDeclAssignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterVariableDeclAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitVariableDeclAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitVariableDeclAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VariableDeclAssignmentContext variableDeclAssignment() throws RecognitionException {
		VariableDeclAssignmentContext _localctx = new VariableDeclAssignmentContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_variableDeclAssignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
			identifier();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PackedDimensionContext extends ParserRuleContext {
		public TerminalNode LBRACK() { return getToken(SystemVerilogParser.LBRACK, 0); }
		public TerminalNode RBRACK() { return getToken(SystemVerilogParser.RBRACK, 0); }
		public ConstantRangeContext constantRange() {
			return getRuleContext(ConstantRangeContext.class,0);
		}
		public ConstantExpressionContext constantExpression() {
			return getRuleContext(ConstantExpressionContext.class,0);
		}
		public PackedDimensionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packedDimension; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterPackedDimension(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitPackedDimension(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitPackedDimension(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PackedDimensionContext packedDimension() throws RecognitionException {
		PackedDimensionContext _localctx = new PackedDimensionContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_packedDimension);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(248);
			match(LBRACK);
			setState(251);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(249);
				constantRange();
				}
				break;
			case 2:
				{
				setState(250);
				constantExpression();
				}
				break;
			}
			setState(253);
			match(RBRACK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantExpressionContext extends ParserRuleContext {
		public ConstantPrimaryContext constantPrimary() {
			return getRuleContext(ConstantPrimaryContext.class,0);
		}
		public ConstantExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterConstantExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitConstantExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitConstantExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantExpressionContext constantExpression() throws RecognitionException {
		ConstantExpressionContext _localctx = new ConstantExpressionContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_constantExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			constantPrimary();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantRangeContext extends ParserRuleContext {
		public List<ConstantExpressionContext> constantExpression() {
			return getRuleContexts(ConstantExpressionContext.class);
		}
		public ConstantExpressionContext constantExpression(int i) {
			return getRuleContext(ConstantExpressionContext.class,i);
		}
		public TerminalNode COLON() { return getToken(SystemVerilogParser.COLON, 0); }
		public ConstantRangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantRange; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterConstantRange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitConstantRange(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitConstantRange(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantRangeContext constantRange() throws RecognitionException {
		ConstantRangeContext _localctx = new ConstantRangeContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_constantRange);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			constantExpression();
			setState(258);
			match(COLON);
			setState(259);
			constantExpression();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantPrimaryContext extends ParserRuleContext {
		public PrimaryLiteralContext primaryLiteral() {
			return getRuleContext(PrimaryLiteralContext.class,0);
		}
		public ConstantPrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantPrimary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterConstantPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitConstantPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitConstantPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantPrimaryContext constantPrimary() throws RecognitionException {
		ConstantPrimaryContext _localctx = new ConstantPrimaryContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_constantPrimary);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			primaryLiteral();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimaryLiteralContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public PrimaryLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterPrimaryLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitPrimaryLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitPrimaryLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryLiteralContext primaryLiteral() throws RecognitionException {
		PrimaryLiteralContext _localctx = new PrimaryLiteralContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_primaryLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(263);
			number();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public IntegralNumberContext integralNumber() {
			return getRuleContext(IntegralNumberContext.class,0);
		}
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_number);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(265);
			integralNumber();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntegralNumberContext extends ParserRuleContext {
		public DecimalNumberContext decimalNumber() {
			return getRuleContext(DecimalNumberContext.class,0);
		}
		public IntegralNumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integralNumber; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterIntegralNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitIntegralNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitIntegralNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntegralNumberContext integralNumber() throws RecognitionException {
		IntegralNumberContext _localctx = new IntegralNumberContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_integralNumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(267);
			decimalNumber();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DecimalNumberContext extends ParserRuleContext {
		public TerminalNode UNSIGNED_NUMBER() { return getToken(SystemVerilogParser.UNSIGNED_NUMBER, 0); }
		public DecimalNumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decimalNumber; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterDecimalNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitDecimalNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitDecimalNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DecimalNumberContext decimalNumber() throws RecognitionException {
		DecimalNumberContext _localctx = new DecimalNumberContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_decimalNumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(269);
			match(UNSIGNED_NUMBER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentifierContext extends ParserRuleContext {
		public TerminalNode SIMPLE_IDENTIFIER() { return getToken(SystemVerilogParser.SIMPLE_IDENTIFIER, 0); }
		public IdentifierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_identifier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).enterIdentifier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogParserListener ) ((SystemVerilogParserListener)listener).exitIdentifier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogParserVisitor ) return ((SystemVerilogParserVisitor<? extends T>)visitor).visitIdentifier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IdentifierContext identifier() throws RecognitionException {
		IdentifierContext _localctx = new IdentifierContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_identifier);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(271);
			match(SIMPLE_IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\31\u0114\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\3"+
		"\2\7\2X\n\2\f\2\16\2[\13\2\3\2\3\2\3\3\3\3\5\3a\n\3\3\4\3\4\3\4\3\4\3"+
		"\4\3\5\3\5\3\5\5\5k\n\5\3\5\3\5\3\6\3\6\7\6q\n\6\f\6\16\6t\13\6\3\6\3"+
		"\6\3\6\3\6\3\6\5\6{\n\6\3\7\3\7\3\7\3\7\7\7\u0081\n\7\f\7\16\7\u0084\13"+
		"\7\3\7\3\7\3\b\3\b\3\b\3\b\7\b\u008c\n\b\f\b\16\b\u008f\13\b\5\b\u0091"+
		"\n\b\3\b\3\b\3\t\3\t\5\t\u0097\n\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3"+
		"\16\5\16\u00a2\n\16\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21"+
		"\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u00b7\n\23\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\5\24\u00c0\n\24\3\25\3\25\3\25\3\25\3\26\3\26"+
		"\5\26\u00c8\n\26\3\26\7\26\u00cb\n\26\f\26\16\26\u00ce\13\26\3\27\3\27"+
		"\5\27\u00d2\n\27\3\30\5\30\u00d5\n\30\3\30\7\30\u00d8\n\30\f\30\16\30"+
		"\u00db\13\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34\3\34\5\34\u00e7"+
		"\n\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3!\7!\u00f4\n!\f!\16!"+
		"\u00f7\13!\3\"\3\"\3#\3#\3#\5#\u00fe\n#\3#\3#\3$\3$\3%\3%\3%\3%\3&\3&"+
		"\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3+\2\2,\2\4\6\b\n\f\16\20\22\24\26\30"+
		"\32\34\36 \"$&(*,.\60\62\64\668:<>@BDFHJLNPRT\2\4\4\2\17\17\22\22\3\2"+
		"\23\24\2\u00fd\2Y\3\2\2\2\4`\3\2\2\2\6b\3\2\2\2\bg\3\2\2\2\nz\3\2\2\2"+
		"\f|\3\2\2\2\16\u0087\3\2\2\2\20\u0096\3\2\2\2\22\u0098\3\2\2\2\24\u009a"+
		"\3\2\2\2\26\u009c\3\2\2\2\30\u009e\3\2\2\2\32\u00a1\3\2\2\2\34\u00a5\3"+
		"\2\2\2\36\u00a8\3\2\2\2 \u00ab\3\2\2\2\"\u00ad\3\2\2\2$\u00af\3\2\2\2"+
		"&\u00b8\3\2\2\2(\u00c1\3\2\2\2*\u00c5\3\2\2\2,\u00d1\3\2\2\2.\u00d4\3"+
		"\2\2\2\60\u00dc\3\2\2\2\62\u00de\3\2\2\2\64\u00e0\3\2\2\2\66\u00e6\3\2"+
		"\2\28\u00e8\3\2\2\2:\u00ea\3\2\2\2<\u00ec\3\2\2\2>\u00ee\3\2\2\2@\u00f0"+
		"\3\2\2\2B\u00f8\3\2\2\2D\u00fa\3\2\2\2F\u0101\3\2\2\2H\u0103\3\2\2\2J"+
		"\u0107\3\2\2\2L\u0109\3\2\2\2N\u010b\3\2\2\2P\u010d\3\2\2\2R\u010f\3\2"+
		"\2\2T\u0111\3\2\2\2VX\5\4\3\2WV\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2"+
		"Z\\\3\2\2\2[Y\3\2\2\2\\]\7\2\2\3]\3\3\2\2\2^a\5\n\6\2_a\5 \21\2`^\3\2"+
		"\2\2`_\3\2\2\2a\5\3\2\2\2bc\7\21\2\2cd\5T+\2de\5\f\7\2ef\7\6\2\2f\7\3"+
		"\2\2\2gh\7\21\2\2hj\5T+\2ik\5\16\b\2ji\3\2\2\2jk\3\2\2\2kl\3\2\2\2lm\7"+
		"\6\2\2m\t\3\2\2\2nr\5\6\4\2oq\5\36\20\2po\3\2\2\2qt\3\2\2\2rp\3\2\2\2"+
		"rs\3\2\2\2su\3\2\2\2tr\3\2\2\2uv\7\16\2\2v{\3\2\2\2wx\5\b\5\2xy\7\16\2"+
		"\2y{\3\2\2\2zn\3\2\2\2zw\3\2\2\2{\13\3\2\2\2|}\7\n\2\2}\u0082\5\22\n\2"+
		"~\177\7\4\2\2\177\u0081\5\22\n\2\u0080~\3\2\2\2\u0081\u0084\3\2\2\2\u0082"+
		"\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\3\2\2\2\u0084\u0082\3\2"+
		"\2\2\u0085\u0086\7\13\2\2\u0086\r\3\2\2\2\u0087\u0090\7\n\2\2\u0088\u008d"+
		"\5\34\17\2\u0089\u008a\7\4\2\2\u008a\u008c\5\34\17\2\u008b\u0089\3\2\2"+
		"\2\u008c\u008f\3\2\2\2\u008d\u008b\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u0091"+
		"\3\2\2\2\u008f\u008d\3\2\2\2\u0090\u0088\3\2\2\2\u0090\u0091\3\2\2\2\u0091"+
		"\u0092\3\2\2\2\u0092\u0093\7\13\2\2\u0093\17\3\2\2\2\u0094\u0097\5$\23"+
		"\2\u0095\u0097\5&\24\2\u0096\u0094\3\2\2\2\u0096\u0095\3\2\2\2\u0097\21"+
		"\3\2\2\2\u0098\u0099\5\24\13\2\u0099\23\3\2\2\2\u009a\u009b\5\26\f\2\u009b"+
		"\25\3\2\2\2\u009c\u009d\5T+\2\u009d\27\3\2\2\2\u009e\u009f\t\2\2\2\u009f"+
		"\31\3\2\2\2\u00a0\u00a2\5\30\r\2\u00a1\u00a0\3\2\2\2\u00a1\u00a2\3\2\2"+
		"\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\5\66\34\2\u00a4\33\3\2\2\2\u00a5\u00a6"+
		"\5\32\16\2\u00a6\u00a7\5T+\2\u00a7\35\3\2\2\2\u00a8\u00a9\5\20\t\2\u00a9"+
		"\u00aa\7\6\2\2\u00aa\37\3\2\2\2\u00ab\u00ac\5\"\22\2\u00ac!\3\2\2\2\u00ad"+
		"\u00ae\5(\25\2\u00ae#\3\2\2\2\u00af\u00b6\7\17\2\2\u00b0\u00b1\5\66\34"+
		"\2\u00b1\u00b2\5T+\2\u00b2\u00b7\3\2\2\2\u00b3\u00b4\58\35\2\u00b4\u00b5"+
		"\5T+\2\u00b5\u00b7\3\2\2\2\u00b6\u00b0\3\2\2\2\u00b6\u00b3\3\2\2\2\u00b7"+
		"%\3\2\2\2\u00b8\u00bf\7\22\2\2\u00b9\u00ba\5\66\34\2\u00ba\u00bb\5T+\2"+
		"\u00bb\u00c0\3\2\2\2\u00bc\u00bd\58\35\2\u00bd\u00be\5T+\2\u00be\u00c0"+
		"\3\2\2\2\u00bf\u00b9\3\2\2\2\u00bf\u00bc\3\2\2\2\u00c0\'\3\2\2\2\u00c1"+
		"\u00c2\5,\27\2\u00c2\u00c3\5@!\2\u00c3\u00c4\7\6\2\2\u00c4)\3\2\2\2\u00c5"+
		"\u00c7\5\62\32\2\u00c6\u00c8\5<\37\2\u00c7\u00c6\3\2\2\2\u00c7\u00c8\3"+
		"\2\2\2\u00c8\u00cc\3\2\2\2\u00c9\u00cb\5D#\2\u00ca\u00c9\3\2\2\2\u00cb"+
		"\u00ce\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd+\3\2\2\2"+
		"\u00ce\u00cc\3\2\2\2\u00cf\u00d2\5*\26\2\u00d0\u00d2\5.\30\2\u00d1\u00cf"+
		"\3\2\2\2\u00d1\u00d0\3\2\2\2\u00d2-\3\2\2\2\u00d3\u00d5\5<\37\2\u00d4"+
		"\u00d3\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d9\3\2\2\2\u00d6\u00d8\5D"+
		"#\2\u00d7\u00d6\3\2\2\2\u00d8\u00db\3\2\2\2\u00d9\u00d7\3\2\2\2\u00d9"+
		"\u00da\3\2\2\2\u00da/\3\2\2\2\u00db\u00d9\3\2\2\2\u00dc\u00dd\5\62\32"+
		"\2\u00dd\61\3\2\2\2\u00de\u00df\7\20\2\2\u00df\63\3\2\2\2\u00e0\u00e1"+
		"\7\25\2\2\u00e1\65\3\2\2\2\u00e2\u00e7\5,\27\2\u00e3\u00e4\5\64\33\2\u00e4"+
		"\u00e5\5,\27\2\u00e5\u00e7\3\2\2\2\u00e6\u00e2\3\2\2\2\u00e6\u00e3\3\2"+
		"\2\2\u00e7\67\3\2\2\2\u00e8\u00e9\5:\36\2\u00e99\3\2\2\2\u00ea\u00eb\5"+
		"*\26\2\u00eb;\3\2\2\2\u00ec\u00ed\t\3\2\2\u00ed=\3\2\2\2\u00ee\u00ef\5"+
		"\60\31\2\u00ef?\3\2\2\2\u00f0\u00f5\5B\"\2\u00f1\u00f2\7\4\2\2\u00f2\u00f4"+
		"\5B\"\2\u00f3\u00f1\3\2\2\2\u00f4\u00f7\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f5"+
		"\u00f6\3\2\2\2\u00f6A\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f8\u00f9\5T+\2\u00f9"+
		"C\3\2\2\2\u00fa\u00fd\7\b\2\2\u00fb\u00fe\5H%\2\u00fc\u00fe\5F$\2\u00fd"+
		"\u00fb\3\2\2\2\u00fd\u00fc\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0100\7\t"+
		"\2\2\u0100E\3\2\2\2\u0101\u0102\5J&\2\u0102G\3\2\2\2\u0103\u0104\5F$\2"+
		"\u0104\u0105\7\5\2\2\u0105\u0106\5F$\2\u0106I\3\2\2\2\u0107\u0108\5L\'"+
		"\2\u0108K\3\2\2\2\u0109\u010a\5N(\2\u010aM\3\2\2\2\u010b\u010c\5P)\2\u010c"+
		"O\3\2\2\2\u010d\u010e\5R*\2\u010eQ\3\2\2\2\u010f\u0110\7\26\2\2\u0110"+
		"S\3\2\2\2\u0111\u0112\7\30\2\2\u0112U\3\2\2\2\26Y`jrz\u0082\u008d\u0090"+
		"\u0096\u00a1\u00b6\u00bf\u00c7\u00cc\u00d1\u00d4\u00d9\u00e6\u00f5\u00fd";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}