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
		DEFINE_MACRO=19, TEXT_LINE_CONTINUATION=20, TEXT_NEW_LINE=21, TEXT=22, 
		TEXT_LINE_BACK_SLASH=23, TEXT_SLASH=24;
	public static final int
		RULE_file = 0, RULE_text = 1, RULE_directive = 2, RULE_defineDirective = 3, 
		RULE_macroDirective = 4, RULE_code = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"file", "text", "directive", "defineDirective", "macroDirective", "code"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'`'", null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, "'\\'", 
			"'/'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BACKTICK", "CODE", "DIRECTIVE_WHITESPACE", "DIRECTIVE_BLOCK_COMMENT", 
			"DIRECTIVE_LINE_COMMENT", "DIRECTIVE_LINE_CONTINUATION", "DIRECTIVE_NEW_LINE", 
			"DEFINE", "IFDEF", "IFNDEF", "ENDIF", "TIMESCALE", "UNDEF_ALL", "UNDEF", 
			"DEFINED_MACRO", "DEFINE_WHITESPACE", "DEFINE_LINE_CONTINUATION", "DEFINE_NEW_LINE", 
			"DEFINE_MACRO", "TEXT_LINE_CONTINUATION", "TEXT_NEW_LINE", "TEXT", "TEXT_LINE_BACK_SLASH", 
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
			setState(15);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==BACKTICK || _la==CODE) {
				{
				{
				setState(12);
				text();
				}
				}
				setState(17);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(18);
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
			setState(27);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(20);
				code();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(21);
				match(BACKTICK);
				setState(22);
				directive();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(23);
				match(BACKTICK);
				setState(24);
				defineDirective();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(25);
				match(BACKTICK);
				setState(26);
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
			setState(35);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IFDEF:
				_localctx = new IfdefContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(29);
				match(IFDEF);
				}
				break;
			case IFNDEF:
				_localctx = new IfndefContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(30);
				match(IFNDEF);
				}
				break;
			case ENDIF:
				_localctx = new EndifContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(31);
				match(ENDIF);
				}
				break;
			case TIMESCALE:
				_localctx = new TimescaleContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(32);
				match(TIMESCALE);
				}
				break;
			case UNDEF_ALL:
				_localctx = new UndefAllContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(33);
				match(UNDEF_ALL);
				}
				break;
			case UNDEF:
				_localctx = new UndefContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(34);
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
			setState(37);
			match(DEFINE);
			setState(38);
			match(DEFINE_MACRO);
			setState(42);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TEXT_LINE_CONTINUATION || _la==TEXT) {
				{
				{
				setState(39);
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
				setState(44);
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
		enterRule(_localctx, 8, RULE_macroDirective);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
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
		enterRule(_localctx, 10, RULE_code);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\32\64\4\2\t\2\4\3"+
		"\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\7\2\20\n\2\f\2\16\2\23\13\2\3"+
		"\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\36\n\3\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\5\4&\n\4\3\5\3\5\3\5\7\5+\n\5\f\5\16\5.\13\5\3\6\3\6\3\7\3\7\3\7\2\2"+
		"\b\2\4\6\b\n\f\2\3\4\2\26\26\30\30\2\67\2\21\3\2\2\2\4\35\3\2\2\2\6%\3"+
		"\2\2\2\b\'\3\2\2\2\n/\3\2\2\2\f\61\3\2\2\2\16\20\5\4\3\2\17\16\3\2\2\2"+
		"\20\23\3\2\2\2\21\17\3\2\2\2\21\22\3\2\2\2\22\24\3\2\2\2\23\21\3\2\2\2"+
		"\24\25\7\2\2\3\25\3\3\2\2\2\26\36\5\f\7\2\27\30\7\3\2\2\30\36\5\6\4\2"+
		"\31\32\7\3\2\2\32\36\5\b\5\2\33\34\7\3\2\2\34\36\5\n\6\2\35\26\3\2\2\2"+
		"\35\27\3\2\2\2\35\31\3\2\2\2\35\33\3\2\2\2\36\5\3\2\2\2\37&\7\13\2\2 "+
		"&\7\f\2\2!&\7\r\2\2\"&\7\16\2\2#&\7\17\2\2$&\7\20\2\2%\37\3\2\2\2% \3"+
		"\2\2\2%!\3\2\2\2%\"\3\2\2\2%#\3\2\2\2%$\3\2\2\2&\7\3\2\2\2\'(\7\n\2\2"+
		"(,\7\25\2\2)+\t\2\2\2*)\3\2\2\2+.\3\2\2\2,*\3\2\2\2,-\3\2\2\2-\t\3\2\2"+
		"\2.,\3\2\2\2/\60\7\21\2\2\60\13\3\2\2\2\61\62\7\4\2\2\62\r\3\2\2\2\6\21"+
		"\35%,";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}