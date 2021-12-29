// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/SystemVerilogPreprocessorParser.g4 by ANTLR 4.9.2
package io.verik.importer.antlr;

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
public class SystemVerilogPreprocessorParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BACKTICK=1, CODE=2, DIRECTIVE_WHITESPACE=3, DIRECTIVE_BLOCK_COMMENT=4, 
		DIRECTIVE_LINE_COMMENT=5, DIRECTIVE_LINE_CONTINUATION=6, DIRECTIVE_NEW_LINE=7, 
		DEFINE=8, IFDEF=9, IFNDEF=10, ENDIF=11, TIMESCALE=12, UNDEF_ALL=13, UNDEF=14, 
		DEFINED_MACRO=15, DEFINE_WHITESPACE=16, DEFINE_LINE_CONTINUATION=17, DEFINE_NEW_LINE=18, 
		DEFINE_MACRO_ARG=19, DEFINE_MACRO=20, DEFINE_ARG_WHITESPACE=21, DEFINE_ARG_LINE_CONTINUATION=22, 
		DEFINE_ARG_NEW_LINE=23, DEFINE_ARG_COMMA=24, DEFINE_ARG_RP=25, DEFINE_ARG_IDENTIFIER=26, 
		TEXT_LINE_CONTINUATION=27, TEXT_NEW_LINE=28, TEXT=29, TEXT_LINE_BACK_SLASH=30, 
		TEXT_SLASH=31;
	public static final int
		RULE_file = 0, RULE_text = 1, RULE_directive = 2, RULE_defineDirective = 3, 
		RULE_argumentsDefineDirective = 4, RULE_arguments = 5, RULE_argument = 6, 
		RULE_macroDirective = 7, RULE_code = 8;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "text", "directive", "defineDirective", "argumentsDefineDirective", 
			"arguments", "argument", "macroDirective", "code"
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
			"DEFINE", "IFDEF", "IFNDEF", "ENDIF", "TIMESCALE", "UNDEF_ALL", "UNDEF", 
			"DEFINED_MACRO", "DEFINE_WHITESPACE", "DEFINE_LINE_CONTINUATION", "DEFINE_NEW_LINE", 
			"DEFINE_MACRO_ARG", "DEFINE_MACRO", "DEFINE_ARG_WHITESPACE", "DEFINE_ARG_LINE_CONTINUATION", 
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
	public String getGrammarFileName() { return "SystemVerilogPreprocessorParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SystemVerilogPreprocessorParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class FileContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SystemVerilogPreprocessorParser.EOF, 0); }
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
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitFile(this);
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
		public TerminalNode BACKTICK() { return getToken(SystemVerilogPreprocessorParser.BACKTICK, 0); }
		public DirectiveContext directive() {
			return getRuleContext(DirectiveContext.class,0);
		}
		public DefineDirectiveContext defineDirective() {
			return getRuleContext(DefineDirectiveContext.class,0);
		}
		public ArgumentsDefineDirectiveContext argumentsDefineDirective() {
			return getRuleContext(ArgumentsDefineDirectiveContext.class,0);
		}
		public MacroDirectiveContext macroDirective() {
			return getRuleContext(MacroDirectiveContext.class,0);
		}
		public TextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_text; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitText(this);
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
				defineDirective();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(31);
				match(BACKTICK);
				setState(32);
				argumentsDefineDirective();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(33);
				match(BACKTICK);
				setState(34);
				macroDirective();
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
	public static class UndefContext extends DirectiveContext {
		public TerminalNode UNDEF() { return getToken(SystemVerilogPreprocessorParser.UNDEF, 0); }
		public UndefContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterUndef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitUndef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitUndef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfndefContext extends DirectiveContext {
		public TerminalNode IFNDEF() { return getToken(SystemVerilogPreprocessorParser.IFNDEF, 0); }
		public IfndefContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterIfndef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitIfndef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitIfndef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class EndifContext extends DirectiveContext {
		public TerminalNode ENDIF() { return getToken(SystemVerilogPreprocessorParser.ENDIF, 0); }
		public EndifContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterEndif(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitEndif(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitEndif(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class TimescaleContext extends DirectiveContext {
		public TerminalNode TIMESCALE() { return getToken(SystemVerilogPreprocessorParser.TIMESCALE, 0); }
		public TimescaleContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterTimescale(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitTimescale(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitTimescale(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfdefContext extends DirectiveContext {
		public TerminalNode IFDEF() { return getToken(SystemVerilogPreprocessorParser.IFDEF, 0); }
		public IfdefContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterIfdef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitIfdef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitIfdef(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UndefAllContext extends DirectiveContext {
		public TerminalNode UNDEF_ALL() { return getToken(SystemVerilogPreprocessorParser.UNDEF_ALL, 0); }
		public UndefAllContext(DirectiveContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterUndefAll(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitUndefAll(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitUndefAll(this);
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
			case IFDEF:
				_localctx = new IfdefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(37);
				match(IFDEF);
				}
				break;
			case IFNDEF:
				_localctx = new IfndefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(38);
				match(IFNDEF);
				}
				break;
			case ENDIF:
				_localctx = new EndifContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(39);
				match(ENDIF);
				}
				break;
			case TIMESCALE:
				_localctx = new TimescaleContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(40);
				match(TIMESCALE);
				}
				break;
			case UNDEF_ALL:
				_localctx = new UndefAllContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(41);
				match(UNDEF_ALL);
				}
				break;
			case UNDEF:
				_localctx = new UndefContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(42);
				match(UNDEF);
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

	public static class DefineDirectiveContext extends ParserRuleContext {
		public TerminalNode DEFINE() { return getToken(SystemVerilogPreprocessorParser.DEFINE, 0); }
		public TerminalNode DEFINE_MACRO() { return getToken(SystemVerilogPreprocessorParser.DEFINE_MACRO, 0); }
		public List<TerminalNode> TEXT() { return getTokens(SystemVerilogPreprocessorParser.TEXT); }
		public TerminalNode TEXT(int i) {
			return getToken(SystemVerilogPreprocessorParser.TEXT, i);
		}
		public List<TerminalNode> TEXT_LINE_CONTINUATION() { return getTokens(SystemVerilogPreprocessorParser.TEXT_LINE_CONTINUATION); }
		public TerminalNode TEXT_LINE_CONTINUATION(int i) {
			return getToken(SystemVerilogPreprocessorParser.TEXT_LINE_CONTINUATION, i);
		}
		public DefineDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defineDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterDefineDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitDefineDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitDefineDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefineDirectiveContext defineDirective() throws RecognitionException {
		DefineDirectiveContext _localctx = new DefineDirectiveContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_defineDirective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			match(DEFINE);
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

	public static class ArgumentsDefineDirectiveContext extends ParserRuleContext {
		public TerminalNode DEFINE() { return getToken(SystemVerilogPreprocessorParser.DEFINE, 0); }
		public TerminalNode DEFINE_MACRO_ARG() { return getToken(SystemVerilogPreprocessorParser.DEFINE_MACRO_ARG, 0); }
		public TerminalNode DEFINE_ARG_RP() { return getToken(SystemVerilogPreprocessorParser.DEFINE_ARG_RP, 0); }
		public ArgumentsContext arguments() {
			return getRuleContext(ArgumentsContext.class,0);
		}
		public List<TerminalNode> TEXT() { return getTokens(SystemVerilogPreprocessorParser.TEXT); }
		public TerminalNode TEXT(int i) {
			return getToken(SystemVerilogPreprocessorParser.TEXT, i);
		}
		public List<TerminalNode> TEXT_LINE_CONTINUATION() { return getTokens(SystemVerilogPreprocessorParser.TEXT_LINE_CONTINUATION); }
		public TerminalNode TEXT_LINE_CONTINUATION(int i) {
			return getToken(SystemVerilogPreprocessorParser.TEXT_LINE_CONTINUATION, i);
		}
		public ArgumentsDefineDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentsDefineDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterArgumentsDefineDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitArgumentsDefineDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitArgumentsDefineDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentsDefineDirectiveContext argumentsDefineDirective() throws RecognitionException {
		ArgumentsDefineDirectiveContext _localctx = new ArgumentsDefineDirectiveContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_argumentsDefineDirective);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			match(DEFINE);
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
		public List<TerminalNode> DEFINE_ARG_COMMA() { return getTokens(SystemVerilogPreprocessorParser.DEFINE_ARG_COMMA); }
		public TerminalNode DEFINE_ARG_COMMA(int i) {
			return getToken(SystemVerilogPreprocessorParser.DEFINE_ARG_COMMA, i);
		}
		public ArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitArguments(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitArguments(this);
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
		public TerminalNode DEFINE_ARG_IDENTIFIER() { return getToken(SystemVerilogPreprocessorParser.DEFINE_ARG_IDENTIFIER, 0); }
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitArgument(this);
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

	public static class MacroDirectiveContext extends ParserRuleContext {
		public TerminalNode DEFINED_MACRO() { return getToken(SystemVerilogPreprocessorParser.DEFINED_MACRO, 0); }
		public MacroDirectiveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macroDirective; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterMacroDirective(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitMacroDirective(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitMacroDirective(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MacroDirectiveContext macroDirective() throws RecognitionException {
		MacroDirectiveContext _localctx = new MacroDirectiveContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_macroDirective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(DEFINED_MACRO);
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
		public TerminalNode CODE() { return getToken(SystemVerilogPreprocessorParser.CODE, 0); }
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).enterCode(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof SystemVerilogPreprocessorParserListener ) ((SystemVerilogPreprocessorParserListener)listener).exitCode(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SystemVerilogPreprocessorParserVisitor ) return ((SystemVerilogPreprocessorParserVisitor<? extends T>)visitor).visitCode(this);
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