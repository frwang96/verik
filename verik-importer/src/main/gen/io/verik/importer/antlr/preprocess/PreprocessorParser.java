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
		DIRECTIVE_TIMESCALE=12, DIRECTIVE_UNDEFINEALL=13, DIRECTIVE_UNDEF=14, 
		DIRECTIVE_MACRO=15, DEFINE_WHITESPACE=16, DEFINE_LINE_CONTINUATION=17, 
		DEFINE_NEW_LINE=18, DEFINE_MACRO_ARG=19, DEFINE_MACRO=20, DEFINE_ARG_WHITESPACE=21, 
		DEFINE_ARG_LINE_CONTINUATION=22, DEFINE_ARG_NEW_LINE=23, DEFINE_ARG_COMMA=24, 
		DEFINE_ARG_RP=25, DEFINE_ARG_IDENTIFIER=26, TEXT_LINE_CONTINUATION=27, 
		TEXT_NEW_LINE=28, TEXT=29, TEXT_LINE_BACK_SLASH=30, TEXT_SLASH=31;
	public static final int
		RULE_file = 0, RULE_text = 1, RULE_directive = 2, RULE_directiveDefine = 3, 
		RULE_directiveDefineArg = 4, RULE_arguments = 5, RULE_argument = 6, RULE_directiveMacro = 7, 
		RULE_code = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "text", "directive", "directiveDefine", "directiveDefineArg", 
			"arguments", "argument", "directiveMacro", "code"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'`'", null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"','", null, null, null, null, null, "'\\'", "'/'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BACKTICK", "CODE", "DIRECTIVE_WHITESPACE", "DIRECTIVE_BLOCK_COMMENT", 
			"DIRECTIVE_LINE_COMMENT", "DIRECTIVE_LINE_CONTINUATION", "DIRECTIVE_NEW_LINE", 
			"DIRECTIVE_DEFINE", "DIRECTIVE_IFDEF", "DIRECTIVE_IFNDEF", "DIRECTIVE_ENDIF", 
			"DIRECTIVE_TIMESCALE", "DIRECTIVE_UNDEFINEALL", "DIRECTIVE_UNDEF", "DIRECTIVE_MACRO", 
			"DEFINE_WHITESPACE", "DEFINE_LINE_CONTINUATION", "DEFINE_NEW_LINE", "DEFINE_MACRO_ARG", 
			"DEFINE_MACRO", "DEFINE_ARG_WHITESPACE", "DEFINE_ARG_LINE_CONTINUATION", 
			"DEFINE_ARG_NEW_LINE", "DEFINE_ARG_COMMA", "DEFINE_ARG_RP", "DEFINE_ARG_IDENTIFIER", 
			"TEXT_LINE_CONTINUATION", "TEXT_NEW_LINE", "TEXT", "TEXT_LINE_BACK_SLASH", 
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
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==BACKTICK || _la==CODE) {
				{
				{
				setState(18);
				text();
				}
				}
				setState(23);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(24);
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
		public TerminalNode BACKTICK() { return getToken(PreprocessorParser.BACKTICK, 0); }
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
			setState(35);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(26);
				code();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(27);
				match(BACKTICK);
				setState(28);
				directive();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(29);
				match(BACKTICK);
				setState(30);
				directiveDefine();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(31);
				match(BACKTICK);
				setState(32);
				directiveDefineArg();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(33);
				match(BACKTICK);
				setState(34);
				directiveMacro();
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
	public static class DirectiveTimescaleContext extends DirectiveContext {
		public TerminalNode DIRECTIVE_TIMESCALE() { return getToken(PreprocessorParser.DIRECTIVE_TIMESCALE, 0); }
		public DirectiveTimescaleContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).enterDirectiveTimescale(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PreprocessorParserListener ) ((PreprocessorParserListener)listener).exitDirectiveTimescale(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PreprocessorParserVisitor ) return ((PreprocessorParserVisitor<? extends T>)visitor).visitDirectiveTimescale(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DirectiveUndefContext extends DirectiveContext {
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
			setState(43);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DIRECTIVE_IFDEF:
				_localctx = new DirectiveIfdefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(37);
				match(DIRECTIVE_IFDEF);
				}
				break;
			case DIRECTIVE_IFNDEF:
				_localctx = new DirectiveIfndefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(38);
				match(DIRECTIVE_IFNDEF);
				}
				break;
			case DIRECTIVE_ENDIF:
				_localctx = new DirectiveEndifContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(39);
				match(DIRECTIVE_ENDIF);
				}
				break;
			case DIRECTIVE_TIMESCALE:
				_localctx = new DirectiveTimescaleContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(40);
				match(DIRECTIVE_TIMESCALE);
				}
				break;
			case DIRECTIVE_UNDEFINEALL:
				_localctx = new DirectiveUndefineAllContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(41);
				match(DIRECTIVE_UNDEFINEALL);
				}
				break;
			case DIRECTIVE_UNDEF:
				_localctx = new DirectiveUndefContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(42);
				match(DIRECTIVE_UNDEF);
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

	public static class DirectiveDefineContext extends ParserRuleContext {
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
			setState(45);
			match(DIRECTIVE_DEFINE);
			setState(46);
			match(DEFINE_MACRO);
			setState(50);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TEXT_LINE_CONTINUATION || _la==TEXT) {
				{
				{
				setState(47);
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
				setState(52);
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
			setState(53);
			match(DIRECTIVE_DEFINE);
			setState(54);
			match(DEFINE_MACRO_ARG);
			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DEFINE_ARG_IDENTIFIER) {
				{
				setState(55);
				arguments();
				}
			}

			setState(58);
			match(DEFINE_ARG_RP);
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
			setState(65);
			argument();
			setState(70);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEFINE_ARG_COMMA) {
				{
				{
				setState(66);
				match(DEFINE_ARG_COMMA);
				setState(67);
				argument();
				}
				}
				setState(72);
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
			setState(73);
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
			setState(75);
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
		enterRule(_localctx, 16, RULE_code);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(77);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3!R\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\7\2\26\n"+
		"\2\f\2\16\2\31\13\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3&\n"+
		"\3\3\4\3\4\3\4\3\4\3\4\3\4\5\4.\n\4\3\5\3\5\3\5\7\5\63\n\5\f\5\16\5\66"+
		"\13\5\3\6\3\6\3\6\5\6;\n\6\3\6\3\6\7\6?\n\6\f\6\16\6B\13\6\3\7\3\7\3\7"+
		"\7\7G\n\7\f\7\16\7J\13\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\2\2\13\2\4\6\b\n"+
		"\f\16\20\22\2\3\4\2\35\35\37\37\2V\2\27\3\2\2\2\4%\3\2\2\2\6-\3\2\2\2"+
		"\b/\3\2\2\2\n\67\3\2\2\2\fC\3\2\2\2\16K\3\2\2\2\20M\3\2\2\2\22O\3\2\2"+
		"\2\24\26\5\4\3\2\25\24\3\2\2\2\26\31\3\2\2\2\27\25\3\2\2\2\27\30\3\2\2"+
		"\2\30\32\3\2\2\2\31\27\3\2\2\2\32\33\7\2\2\3\33\3\3\2\2\2\34&\5\22\n\2"+
		"\35\36\7\3\2\2\36&\5\6\4\2\37 \7\3\2\2 &\5\b\5\2!\"\7\3\2\2\"&\5\n\6\2"+
		"#$\7\3\2\2$&\5\20\t\2%\34\3\2\2\2%\35\3\2\2\2%\37\3\2\2\2%!\3\2\2\2%#"+
		"\3\2\2\2&\5\3\2\2\2\'.\7\13\2\2(.\7\f\2\2).\7\r\2\2*.\7\16\2\2+.\7\17"+
		"\2\2,.\7\20\2\2-\'\3\2\2\2-(\3\2\2\2-)\3\2\2\2-*\3\2\2\2-+\3\2\2\2-,\3"+
		"\2\2\2.\7\3\2\2\2/\60\7\n\2\2\60\64\7\26\2\2\61\63\t\2\2\2\62\61\3\2\2"+
		"\2\63\66\3\2\2\2\64\62\3\2\2\2\64\65\3\2\2\2\65\t\3\2\2\2\66\64\3\2\2"+
		"\2\678\7\n\2\28:\7\25\2\29;\5\f\7\2:9\3\2\2\2:;\3\2\2\2;<\3\2\2\2<@\7"+
		"\33\2\2=?\t\2\2\2>=\3\2\2\2?B\3\2\2\2@>\3\2\2\2@A\3\2\2\2A\13\3\2\2\2"+
		"B@\3\2\2\2CH\5\16\b\2DE\7\32\2\2EG\5\16\b\2FD\3\2\2\2GJ\3\2\2\2HF\3\2"+
		"\2\2HI\3\2\2\2I\r\3\2\2\2JH\3\2\2\2KL\7\34\2\2L\17\3\2\2\2MN\7\21\2\2"+
		"N\21\3\2\2\2OP\7\4\2\2P\23\3\2\2\2\t\27%-\64:@H";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}