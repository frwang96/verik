// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/preprocess/PreprocessorParser.g4 by ANTLR 4.9.2
package io.verik.importer.antlr.preprocess;

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
public class PreprocessorParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BACKTICK=1, CODE=2, DIRECTIVE_WHITESPACE=3, DIRECTIVE_BLOCK_COMMENT=4, 
		DIRECTIVE_LINE_COMMENT=5, DIRECTIVE_LINE_CONTINUATION=6, DIRECTIVE_NEW_LINE=7, 
		DIRECTIVE_DEFINE=8, DIRECTIVE_IFDEF=9, DIRECTIVE_IFNDEF=10, DIRECTIVE_ENDIF=11, 
		DIRECTIVE_LINE=12, DIRECTIVE_TIMESCALE=13, DIRECTIVE_UNDEFINEALL=14, DIRECTIVE_UNDEF=15, 
		DIRECTIVE_MACRO_ARG=16, DIRECTIVE_MACRO=17, DEFINE_WHITESPACE=18, DEFINE_LINE_CONTINUATION=19, 
		DEFINE_NEW_LINE=20, DEFINE_MACRO_ARG=21, DEFINE_MACRO=22, DEFINE_ARG_WHITESPACE=23, 
		DEFINE_ARG_LINE_CONTINUATION=24, DEFINE_ARG_NEW_LINE=25, DEFINE_ARG_COMMA=26, 
		DEFINE_ARG_RP=27, DEFINE_ARG_IDENTIFIER=28, TEXT_LINE_CONTINUATION=29, 
		TEXT_NEW_LINE=30, TEXT_LINE_COMMENT=31, TEXT=32, RUN_COMMA=33, RUN_RP=34, 
		RUN_TEXT=35, TEXT_LINE_BACK_SLASH=36, TEXT_SLASH=37;
	public static final int
		RULE_file = 0, RULE_text = 1, RULE_directive = 2, RULE_directiveDefine = 3, 
		RULE_directiveDefineArg = 4, RULE_arguments = 5, RULE_argument = 6, RULE_directiveMacro = 7, 
		RULE_directiveMacroArg = 8, RULE_runArguments = 9, RULE_runArgument = 10, 
		RULE_code = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "text", "directive", "directiveDefine", "directiveDefineArg", 
			"arguments", "argument", "directiveMacro", "directiveMacroArg", "runArguments", 
			"runArgument", "code"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'`'", null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, "')'", null, 
			"'\\'", "'/'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BACKTICK", "CODE", "DIRECTIVE_WHITESPACE", "DIRECTIVE_BLOCK_COMMENT", 
			"DIRECTIVE_LINE_COMMENT", "DIRECTIVE_LINE_CONTINUATION", "DIRECTIVE_NEW_LINE", 
			"DIRECTIVE_DEFINE", "DIRECTIVE_IFDEF", "DIRECTIVE_IFNDEF", "DIRECTIVE_ENDIF", 
			"DIRECTIVE_LINE", "DIRECTIVE_TIMESCALE", "DIRECTIVE_UNDEFINEALL", "DIRECTIVE_UNDEF", 
			"DIRECTIVE_MACRO_ARG", "DIRECTIVE_MACRO", "DEFINE_WHITESPACE", "DEFINE_LINE_CONTINUATION", 
			"DEFINE_NEW_LINE", "DEFINE_MACRO_ARG", "DEFINE_MACRO", "DEFINE_ARG_WHITESPACE", 
			"DEFINE_ARG_LINE_CONTINUATION", "DEFINE_ARG_NEW_LINE", "DEFINE_ARG_COMMA", 
			"DEFINE_ARG_RP", "DEFINE_ARG_IDENTIFIER", "TEXT_LINE_CONTINUATION", "TEXT_NEW_LINE", 
			"TEXT_LINE_COMMENT", "TEXT", "RUN_COMMA", "RUN_RP", "RUN_TEXT", "TEXT_LINE_BACK_SLASH", 
			"TEXT_SLASH"
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
	public String getGrammarFileName() { return "PreprocessorParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PreprocessorParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class FileContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(PreprocessorParser.EOF, 0); }
		public List<TextContext> text() {
			return getRuleContexts(TextContext.class);
		}
		public TextContext text(int i) {
			return getRuleContext(TextContext.class,i);
		}
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_file);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(27);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==BACKTICK || _la==CODE) {
				{
				{
				setState(24);
				text();
				}
				}
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(30);
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

	public static class TextContext extends ParserRuleContext {
		public CodeContext code() {
			return getRuleContext(CodeContext.class,0);
		}
		public DirectiveContext directive() {
			return getRuleContext(DirectiveContext.class,0);
		}
		public DirectiveDefineContext directiveDefine() {
			return getRuleContext(DirectiveDefineContext.class,0);
		}
		public DirectiveDefineArgContext directiveDefineArg() {
			return getRuleContext(DirectiveDefineArgContext.class,0);
		}
		public DirectiveMacroContext directiveMacro() {
			return getRuleContext(DirectiveMacroContext.class,0);
		}
		public DirectiveMacroArgContext directiveMacroArg() {
			return getRuleContext(DirectiveMacroArgContext.class,0);
		}
		public TextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_text; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitText(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TextContext text() throws RecognitionException {
		TextContext _localctx = new TextContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_text);
		try {
			setState(38);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(32);
				code();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(33);
				directive();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(34);
				directiveDefine();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(35);
				directiveDefineArg();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(36);
				directiveMacro();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(37);
				directiveMacroArg();
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

	public static class DirectiveContext extends ParserRuleContext {
		public DirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directive; }
	 
		public DirectiveContext() { }
		public void copyFrom(DirectiveContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DirectiveIfdefContext extends DirectiveContext {
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
		public TerminalNode DIRECTIVE_IFDEF() { return getToken(PreprocessorParser.DIRECTIVE_IFDEF, 0); }
		public DirectiveIfdefContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveIfdef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveIfdef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveIfdef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DirectiveIgnoredContext extends DirectiveContext {
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
		public TerminalNode DIRECTIVE_LINE() { return getToken(PreprocessorParser.DIRECTIVE_LINE, 0); }
		public TerminalNode DIRECTIVE_TIMESCALE() { return getToken(PreprocessorParser.DIRECTIVE_TIMESCALE, 0); }
		public DirectiveIgnoredContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveIgnored(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveIgnored(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveIgnored(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DirectiveUndefContext extends DirectiveContext {
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
		public TerminalNode DIRECTIVE_UNDEF() { return getToken(PreprocessorParser.DIRECTIVE_UNDEF, 0); }
		public DirectiveUndefContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveUndef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveUndef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveUndef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DirectiveEndifContext extends DirectiveContext {
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
		public TerminalNode DIRECTIVE_ENDIF() { return getToken(PreprocessorParser.DIRECTIVE_ENDIF, 0); }
		public DirectiveEndifContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveEndif(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveEndif(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveEndif(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DirectiveIfndefContext extends DirectiveContext {
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
		public TerminalNode DIRECTIVE_IFNDEF() { return getToken(PreprocessorParser.DIRECTIVE_IFNDEF, 0); }
		public DirectiveIfndefContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveIfndef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveIfndef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveIfndef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DirectiveUndefineAllContext extends DirectiveContext {
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
		public TerminalNode DIRECTIVE_UNDEFINEALL() { return getToken(PreprocessorParser.DIRECTIVE_UNDEFINEALL, 0); }
		public DirectiveUndefineAllContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveUndefineAll(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveUndefineAll(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveUndefineAll(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveContext directive() throws RecognitionException {
		DirectiveContext _localctx = new DirectiveContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_directive);
		try {
			setState(54);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new DirectiveIfdefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(40);
				match(BACKTICK);
				setState(41);
				match(DIRECTIVE_IFDEF);
				}
				break;
			case 2:
				_localctx = new DirectiveIfndefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(42);
				match(BACKTICK);
				setState(43);
				match(DIRECTIVE_IFNDEF);
				}
				break;
			case 3:
				_localctx = new DirectiveEndifContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(44);
				match(BACKTICK);
				setState(45);
				match(DIRECTIVE_ENDIF);
				}
				break;
			case 4:
				_localctx = new DirectiveIgnoredContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(46);
				match(BACKTICK);
				setState(47);
				match(DIRECTIVE_LINE);
				}
				break;
			case 5:
				_localctx = new DirectiveIgnoredContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(48);
				match(BACKTICK);
				setState(49);
				match(DIRECTIVE_TIMESCALE);
				}
				break;
			case 6:
				_localctx = new DirectiveUndefineAllContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(50);
				match(BACKTICK);
				setState(51);
				match(DIRECTIVE_UNDEFINEALL);
				}
				break;
			case 7:
				_localctx = new DirectiveUndefContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(52);
				match(BACKTICK);
				setState(53);
				match(DIRECTIVE_UNDEF);
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

	public static class DirectiveDefineContext extends ParserRuleContext {
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
		public TerminalNode DIRECTIVE_DEFINE() { return getToken(PreprocessorParser.DIRECTIVE_DEFINE, 0); }
		public TerminalNode DEFINE_MACRO() { return getToken(PreprocessorParser.DEFINE_MACRO, 0); }
		public List<TerminalNode> TEXT() { return getTokens(PreprocessorParser.TEXT); }
		public TerminalNode TEXT(int i) {
			return getToken(PreprocessorParser.TEXT, i);
		}
		public List<TerminalNode> TEXT_LINE_CONTINUATION() { return getTokens(PreprocessorParser.TEXT_LINE_CONTINUATION); }
		public TerminalNode TEXT_LINE_CONTINUATION(int i) {
			return getToken(PreprocessorParser.TEXT_LINE_CONTINUATION, i);
		}
		public DirectiveDefineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directiveDefine; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveDefine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveDefine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveDefine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveDefineContext directiveDefine() throws RecognitionException {
		DirectiveDefineContext _localctx = new DirectiveDefineContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_directiveDefine);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(BACKTICK);
			setState(57);
			match(DIRECTIVE_DEFINE);
			setState(58);
			match(DEFINE_MACRO);
			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TEXT_LINE_CONTINUATION || _la==TEXT) {
				{
				{
				setState(59);
				_la = _input.LA(1);
				if ( !(_la==TEXT_LINE_CONTINUATION || _la==TEXT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(64);
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

	public static class DirectiveDefineArgContext extends ParserRuleContext {
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
		public TerminalNode DIRECTIVE_DEFINE() { return getToken(PreprocessorParser.DIRECTIVE_DEFINE, 0); }
		public TerminalNode DEFINE_MACRO_ARG() { return getToken(PreprocessorParser.DEFINE_MACRO_ARG, 0); }
		public TerminalNode DEFINE_ARG_RP() { return getToken(PreprocessorParser.DEFINE_ARG_RP, 0); }
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public List<TerminalNode> TEXT() { return getTokens(PreprocessorParser.TEXT); }
		public TerminalNode TEXT(int i) {
			return getToken(PreprocessorParser.TEXT, i);
		}
		public List<TerminalNode> TEXT_LINE_CONTINUATION() { return getTokens(PreprocessorParser.TEXT_LINE_CONTINUATION); }
		public TerminalNode TEXT_LINE_CONTINUATION(int i) {
			return getToken(PreprocessorParser.TEXT_LINE_CONTINUATION, i);
		}
		public DirectiveDefineArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directiveDefineArg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveDefineArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveDefineArg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveDefineArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveDefineArgContext directiveDefineArg() throws RecognitionException {
		DirectiveDefineArgContext _localctx = new DirectiveDefineArgContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_directiveDefineArg);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			match(BACKTICK);
			setState(66);
			match(DIRECTIVE_DEFINE);
			setState(67);
			match(DEFINE_MACRO_ARG);
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DEFINE_ARG_IDENTIFIER) {
				{
				setState(68);
				arguments();
				}
			}

			setState(71);
			match(DEFINE_ARG_RP);
			setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TEXT_LINE_CONTINUATION || _la==TEXT) {
				{
				{
				setState(72);
				_la = _input.LA(1);
				if ( !(_la==TEXT_LINE_CONTINUATION || _la==TEXT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(77);
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

	public static class ArgumentsContext extends ParserRuleContext {
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
		}
		public List<TerminalNode> DEFINE_ARG_COMMA() { return getTokens(PreprocessorParser.DEFINE_ARG_COMMA); }
		public TerminalNode DEFINE_ARG_COMMA(int i) {
			return getToken(PreprocessorParser.DEFINE_ARG_COMMA, i);
		}
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitArguments(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitArguments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentsContext arguments() throws RecognitionException {
		ArgumentsContext _localctx = new ArgumentsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_arguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			argument();
			setState(83);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEFINE_ARG_COMMA) {
				{
				{
				setState(79);
				match(DEFINE_ARG_COMMA);
				setState(80);
				argument();
				}
				}
				setState(85);
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

	public static class ArgumentContext extends ParserRuleContext {
		public TerminalNode DEFINE_ARG_IDENTIFIER() { return getToken(PreprocessorParser.DEFINE_ARG_IDENTIFIER, 0); }
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			match(DEFINE_ARG_IDENTIFIER);
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

	public static class DirectiveMacroContext extends ParserRuleContext {
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
		public TerminalNode DIRECTIVE_MACRO() { return getToken(PreprocessorParser.DIRECTIVE_MACRO, 0); }
		public DirectiveMacroContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directiveMacro; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveMacro(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveMacro(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveMacro(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveMacroContext directiveMacro() throws RecognitionException {
		DirectiveMacroContext _localctx = new DirectiveMacroContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_directiveMacro);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(BACKTICK);
			setState(89);
			match(DIRECTIVE_MACRO);
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

	public static class DirectiveMacroArgContext extends ParserRuleContext {
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
		public TerminalNode DIRECTIVE_MACRO_ARG() { return getToken(PreprocessorParser.DIRECTIVE_MACRO_ARG, 0); }
		public RunArgumentsContext runArguments() {
			return getRuleContext(RunArgumentsContext.class,0);
		}
		public TerminalNode RUN_RP() { return getToken(PreprocessorParser.RUN_RP, 0); }
		public DirectiveMacroArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_directiveMacroArg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveMacroArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveMacroArg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveMacroArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DirectiveMacroArgContext directiveMacroArg() throws RecognitionException {
		DirectiveMacroArgContext _localctx = new DirectiveMacroArgContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_directiveMacroArg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(BACKTICK);
			setState(92);
			match(DIRECTIVE_MACRO_ARG);
			setState(93);
			runArguments();
			setState(94);
			match(RUN_RP);
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

	public static class RunArgumentsContext extends ParserRuleContext {
		public List<RunArgumentContext> runArgument() {
			return getRuleContexts(RunArgumentContext.class);
		}
		public RunArgumentContext runArgument(int i) {
			return getRuleContext(RunArgumentContext.class,i);
		}
		public List<TerminalNode> RUN_COMMA() { return getTokens(PreprocessorParser.RUN_COMMA); }
		public TerminalNode RUN_COMMA(int i) {
			return getToken(PreprocessorParser.RUN_COMMA, i);
		}
		public RunArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_runArguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterRunArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitRunArguments(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitRunArguments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RunArgumentsContext runArguments() throws RecognitionException {
		RunArgumentsContext _localctx = new RunArgumentsContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_runArguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			runArgument();
			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==RUN_COMMA) {
				{
				{
				setState(97);
				match(RUN_COMMA);
				setState(98);
				runArgument();
				}
				}
				setState(103);
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

	public static class RunArgumentContext extends ParserRuleContext {
		public List<TerminalNode> RUN_TEXT() { return getTokens(PreprocessorParser.RUN_TEXT); }
		public TerminalNode RUN_TEXT(int i) {
			return getToken(PreprocessorParser.RUN_TEXT, i);
		}
		public RunArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_runArgument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterRunArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitRunArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitRunArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RunArgumentContext runArgument() throws RecognitionException {
		RunArgumentContext _localctx = new RunArgumentContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_runArgument);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==RUN_TEXT) {
				{
				{
				setState(104);
				match(RUN_TEXT);
				}
				}
				setState(109);
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

	public static class CodeContext extends ParserRuleContext {
		public TerminalNode CODE() { return getToken(PreprocessorParser.CODE, 0); }
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitCode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeContext code() throws RecognitionException {
		CodeContext _localctx = new CodeContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_code);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			match(CODE);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\'s\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\3\2\7\2\34\n\2\f\2\16\2\37\13\2\3\2\3\2\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\5\3)\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\5\49\n\4\3\5\3\5\3\5\3\5\7\5?\n\5\f\5\16\5B\13\5\3\6\3\6\3\6\3\6"+
		"\5\6H\n\6\3\6\3\6\7\6L\n\6\f\6\16\6O\13\6\3\7\3\7\3\7\7\7T\n\7\f\7\16"+
		"\7W\13\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\7\13f"+
		"\n\13\f\13\16\13i\13\13\3\f\7\fl\n\f\f\f\16\fo\13\f\3\r\3\r\3\r\2\2\16"+
		"\2\4\6\b\n\f\16\20\22\24\26\30\2\3\4\2\37\37\"\"\2x\2\35\3\2\2\2\4(\3"+
		"\2\2\2\68\3\2\2\2\b:\3\2\2\2\nC\3\2\2\2\fP\3\2\2\2\16X\3\2\2\2\20Z\3\2"+
		"\2\2\22]\3\2\2\2\24b\3\2\2\2\26m\3\2\2\2\30p\3\2\2\2\32\34\5\4\3\2\33"+
		"\32\3\2\2\2\34\37\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36 \3\2\2\2\37\35"+
		"\3\2\2\2 !\7\2\2\3!\3\3\2\2\2\")\5\30\r\2#)\5\6\4\2$)\5\b\5\2%)\5\n\6"+
		"\2&)\5\20\t\2\')\5\22\n\2(\"\3\2\2\2(#\3\2\2\2($\3\2\2\2(%\3\2\2\2(&\3"+
		"\2\2\2(\'\3\2\2\2)\5\3\2\2\2*+\7\3\2\2+9\7\13\2\2,-\7\3\2\2-9\7\f\2\2"+
		"./\7\3\2\2/9\7\r\2\2\60\61\7\3\2\2\619\7\16\2\2\62\63\7\3\2\2\639\7\17"+
		"\2\2\64\65\7\3\2\2\659\7\20\2\2\66\67\7\3\2\2\679\7\21\2\28*\3\2\2\28"+
		",\3\2\2\28.\3\2\2\28\60\3\2\2\28\62\3\2\2\28\64\3\2\2\28\66\3\2\2\29\7"+
		"\3\2\2\2:;\7\3\2\2;<\7\n\2\2<@\7\30\2\2=?\t\2\2\2>=\3\2\2\2?B\3\2\2\2"+
		"@>\3\2\2\2@A\3\2\2\2A\t\3\2\2\2B@\3\2\2\2CD\7\3\2\2DE\7\n\2\2EG\7\27\2"+
		"\2FH\5\f\7\2GF\3\2\2\2GH\3\2\2\2HI\3\2\2\2IM\7\35\2\2JL\t\2\2\2KJ\3\2"+
		"\2\2LO\3\2\2\2MK\3\2\2\2MN\3\2\2\2N\13\3\2\2\2OM\3\2\2\2PU\5\16\b\2QR"+
		"\7\34\2\2RT\5\16\b\2SQ\3\2\2\2TW\3\2\2\2US\3\2\2\2UV\3\2\2\2V\r\3\2\2"+
		"\2WU\3\2\2\2XY\7\36\2\2Y\17\3\2\2\2Z[\7\3\2\2[\\\7\23\2\2\\\21\3\2\2\2"+
		"]^\7\3\2\2^_\7\22\2\2_`\5\24\13\2`a\7$\2\2a\23\3\2\2\2bg\5\26\f\2cd\7"+
		"#\2\2df\5\26\f\2ec\3\2\2\2fi\3\2\2\2ge\3\2\2\2gh\3\2\2\2h\25\3\2\2\2i"+
		"g\3\2\2\2jl\7%\2\2kj\3\2\2\2lo\3\2\2\2mk\3\2\2\2mn\3\2\2\2n\27\3\2\2\2"+
		"om\3\2\2\2pq\7\4\2\2q\31\3\2\2\2\13\35(8@GMUgm";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}