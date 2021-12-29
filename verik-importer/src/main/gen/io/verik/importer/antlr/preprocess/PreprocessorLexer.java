// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/preprocess/PreprocessorLexer.g4 by ANTLR 4.9.2
package io.verik.importer.antlr.preprocess;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PreprocessorLexer extends Lexer {
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
		DIRECTIVE_MODE=1, DEFINE_MODE=2, DEFINE_ARG_MODE=3, TEXT_MODE=4, RUN_MODE=5;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "DIRECTIVE_MODE", "DEFINE_MODE", "DEFINE_ARG_MODE", "TEXT_MODE", 
		"RUN_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"BACKTICK", "CODE", "STRING_LITERAL", "BLOCK_COMMENT", "LINE_COMMENT", 
			"WHITESPACE", "DIRECTIVE_WHITESPACE", "DIRECTIVE_BLOCK_COMMENT", "DIRECTIVE_LINE_COMMENT", 
			"DIRECTIVE_LINE_CONTINUATION", "DIRECTIVE_NEW_LINE", "DIRECTIVE_DEFINE", 
			"DIRECTIVE_IFDEF", "DIRECTIVE_IFNDEF", "DIRECTIVE_ENDIF", "DIRECTIVE_LINE", 
			"DIRECTIVE_TIMESCALE", "DIRECTIVE_UNDEFINEALL", "DIRECTIVE_UNDEF", "DIRECTIVE_MACRO_ARG", 
			"DIRECTIVE_MACRO", "DEFINE_WHITESPACE", "DEFINE_LINE_CONTINUATION", "DEFINE_NEW_LINE", 
			"DEFINE_MACRO_ARG", "DEFINE_MACRO", "DEFINE_ARG_WHITESPACE", "DEFINE_ARG_LINE_CONTINUATION", 
			"DEFINE_ARG_NEW_LINE", "DEFINE_ARG_COMMA", "DEFINE_ARG_RP", "DEFINE_ARG_IDENTIFIER", 
			"TEXT_LINE_CONTINUATION", "TEXT_LINE_BACK_SLASH", "TEXT_NEW_LINE", "TEXT_BLOCK_COMMENT", 
			"TEXT_LINE_COMMENT", "TEXT_SLASH", "TEXT_WHITESPACE", "TEXT", "RUN_WHITESPACE", 
			"RUN_COMMA", "RUN_PUSH", "RUN_POP", "RUN_RP", "RUN_TEXT", "IDENTIFIER"
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


	   private int runLevel;


	public PreprocessorLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "PreprocessorLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 19:
			DIRECTIVE_MACRO_ARG_action((RuleContext)_localctx, actionIndex);
			break;
		case 41:
			RUN_COMMA_action((RuleContext)_localctx, actionIndex);
			break;
		case 42:
			RUN_PUSH_action((RuleContext)_localctx, actionIndex);
			break;
		case 43:
			RUN_POP_action((RuleContext)_localctx, actionIndex);
			break;
		case 44:
			RUN_RP_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void DIRECTIVE_MACRO_ARG_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 runLevel++; 
			break;
		}
	}
	private void RUN_COMMA_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:
			 if (runLevel != 1) setType(RUN_TEXT); 
			break;
		}
	}
	private void RUN_PUSH_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:
			 runLevel++; 
			break;
		}
	}
	private void RUN_POP_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3:
			 runLevel--; 
			break;
		}
	}
	private void RUN_RP_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 4:
			 runLevel--; if (runLevel == 0) mode(DEFAULT_MODE); else setType(RUN_TEXT); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\'\u022d\b\1\b\1\b"+
		"\1\b\1\b\1\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b"+
		"\4\t\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t"+
		"\20\4\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t"+
		"\27\4\30\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t"+
		"\36\4\37\t\37\4 \t \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t"+
		"(\4)\t)\4*\t*\4+\t+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\3\2\3\2\3\2\3\2"+
		"\3\3\6\3l\n\3\r\3\16\3m\3\4\3\4\3\4\3\4\3\4\3\4\7\4v\n\4\f\4\16\4y\13"+
		"\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\7\5\u0083\n\5\f\5\16\5\u0086\13\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\7\6\u0091\n\6\f\6\16\6\u0094\13\6\3"+
		"\6\3\6\3\7\6\7\u0099\n\7\r\7\16\7\u009a\3\7\3\7\3\b\6\b\u00a0\n\b\r\b"+
		"\16\b\u00a1\3\b\3\b\3\t\3\t\3\t\3\t\7\t\u00aa\n\t\f\t\16\t\u00ad\13\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\7\n\u00b8\n\n\f\n\16\n\u00bb\13\n"+
		"\3\n\3\n\3\13\3\13\5\13\u00c1\n\13\3\13\3\13\3\13\3\13\3\f\5\f\u00c8\n"+
		"\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\6\r\u00d7\n\r\r"+
		"\r\16\r\u00d8\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\6\16\u00e4\n"+
		"\16\r\16\16\16\u00e5\3\16\3\16\7\16\u00ea\n\16\f\16\16\16\u00ed\13\16"+
		"\3\16\5\16\u00f0\n\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\6\17\u00fc\n\17\r\17\16\17\u00fd\3\17\3\17\7\17\u0102\n\17\f\17\16\17"+
		"\u0105\13\17\3\17\5\17\u0108\n\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\3\20\7\20\u0113\n\20\f\20\16\20\u0116\13\20\3\20\5\20\u0119\n\20\3"+
		"\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\6\21\u0123\n\21\r\21\16\21\u0124"+
		"\3\21\5\21\u0128\n\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\6\22\u0137\n\22\r\22\16\22\u0138\3\22\5\22\u013c\n\22"+
		"\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\7\23\u014d\n\23\f\23\16\23\u0150\13\23\3\23\5\23\u0153\n\23\3\23"+
		"\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\6\24\u015e\n\24\r\24\16\24\u015f"+
		"\3\24\3\24\7\24\u0164\n\24\f\24\16\24\u0167\13\24\3\24\5\24\u016a\n\24"+
		"\3\24\3\24\3\25\3\25\7\25\u0170\n\25\f\25\16\25\u0173\13\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\26\3\26\7\26\u017c\n\26\f\26\16\26\u017f\13\26\3\26"+
		"\3\26\3\27\6\27\u0184\n\27\r\27\16\27\u0185\3\27\3\27\3\30\3\30\5\30\u018c"+
		"\n\30\3\30\3\30\3\30\3\30\3\31\5\31\u0193\n\31\3\31\3\31\3\31\3\31\3\32"+
		"\3\32\7\32\u019b\n\32\f\32\16\32\u019e\13\32\3\32\3\32\3\32\3\32\3\33"+
		"\3\33\7\33\u01a6\n\33\f\33\16\33\u01a9\13\33\3\33\3\33\3\34\6\34\u01ae"+
		"\n\34\r\34\16\34\u01af\3\34\3\34\3\35\3\35\5\35\u01b6\n\35\3\35\3\35\3"+
		"\35\3\35\3\36\5\36\u01bd\n\36\3\36\3\36\3\36\3\36\3\37\3\37\3 \3 \7 \u01c7"+
		"\n \f \16 \u01ca\13 \3 \3 \3!\3!\3\"\3\"\5\"\u01d2\n\"\3\"\3\"\3#\3#\3"+
		"#\3#\3$\5$\u01db\n$\3$\3$\3$\3$\3$\3%\3%\3%\3%\7%\u01e6\n%\f%\16%\u01e9"+
		"\13%\3%\3%\3%\3%\3%\3&\3&\3&\3&\7&\u01f4\n&\f&\16&\u01f7\13&\3&\3&\3\'"+
		"\3\'\3\'\3\'\3(\6(\u0200\n(\r(\16(\u0201\3(\3(\3)\6)\u0207\n)\r)\16)\u0208"+
		"\3*\6*\u020c\n*\r*\16*\u020d\3*\3*\3+\3+\3+\3,\3,\3,\3,\3,\3-\3-\3-\3"+
		"-\3-\3.\3.\3.\3/\6/\u0223\n/\r/\16/\u0224\3\60\3\60\7\60\u0229\n\60\f"+
		"\60\16\60\u022c\13\60\6w\u0084\u00ab\u01e7\2\61\b\3\n\4\f\2\16\2\20\2"+
		"\22\2\24\5\26\6\30\7\32\b\34\t\36\n \13\"\f$\r&\16(\17*\20,\21.\22\60"+
		"\23\62\24\64\25\66\268\27:\30<\31>\32@\33B\34D\35F\36H\37J&L N\2P!R\'"+
		"T\2V\"X\2Z#\\\2^\2`$b%d\2\b\2\3\4\5\6\7\f\7\2\f\f\17\17$$\61\61bb\4\2"+
		"\f\f\17\17\5\2\13\f\17\17\"\"\4\2\13\13\"\"\6\2\f\f\17\17\61\61^^\5\2"+
		"**]]}}\4\2__\177\177\13\2\f\f\17\17$$*+..\61\61]_}}\177\177\5\2C\\aac"+
		"|\7\2&&\62;C\\aac|\2\u0258\2\b\3\2\2\2\2\n\3\2\2\2\2\f\3\2\2\2\2\16\3"+
		"\2\2\2\2\20\3\2\2\2\2\22\3\2\2\2\3\24\3\2\2\2\3\26\3\2\2\2\3\30\3\2\2"+
		"\2\3\32\3\2\2\2\3\34\3\2\2\2\3\36\3\2\2\2\3 \3\2\2\2\3\"\3\2\2\2\3$\3"+
		"\2\2\2\3&\3\2\2\2\3(\3\2\2\2\3*\3\2\2\2\3,\3\2\2\2\3.\3\2\2\2\3\60\3\2"+
		"\2\2\4\62\3\2\2\2\4\64\3\2\2\2\4\66\3\2\2\2\48\3\2\2\2\4:\3\2\2\2\5<\3"+
		"\2\2\2\5>\3\2\2\2\5@\3\2\2\2\5B\3\2\2\2\5D\3\2\2\2\5F\3\2\2\2\6H\3\2\2"+
		"\2\6J\3\2\2\2\6L\3\2\2\2\6N\3\2\2\2\6P\3\2\2\2\6R\3\2\2\2\6T\3\2\2\2\6"+
		"V\3\2\2\2\7X\3\2\2\2\7Z\3\2\2\2\7\\\3\2\2\2\7^\3\2\2\2\7`\3\2\2\2\7b\3"+
		"\2\2\2\bf\3\2\2\2\nk\3\2\2\2\fo\3\2\2\2\16~\3\2\2\2\20\u008c\3\2\2\2\22"+
		"\u0098\3\2\2\2\24\u009f\3\2\2\2\26\u00a5\3\2\2\2\30\u00b3\3\2\2\2\32\u00be"+
		"\3\2\2\2\34\u00c7\3\2\2\2\36\u00ce\3\2\2\2 \u00dc\3\2\2\2\"\u00f3\3\2"+
		"\2\2$\u010b\3\2\2\2&\u011c\3\2\2\2(\u012b\3\2\2\2*\u013f\3\2\2\2,\u0156"+
		"\3\2\2\2.\u016d\3\2\2\2\60\u0179\3\2\2\2\62\u0183\3\2\2\2\64\u0189\3\2"+
		"\2\2\66\u0192\3\2\2\28\u0198\3\2\2\2:\u01a3\3\2\2\2<\u01ad\3\2\2\2>\u01b3"+
		"\3\2\2\2@\u01bc\3\2\2\2B\u01c2\3\2\2\2D\u01c4\3\2\2\2F\u01cd\3\2\2\2H"+
		"\u01cf\3\2\2\2J\u01d5\3\2\2\2L\u01da\3\2\2\2N\u01e1\3\2\2\2P\u01ef\3\2"+
		"\2\2R\u01fa\3\2\2\2T\u01ff\3\2\2\2V\u0206\3\2\2\2X\u020b\3\2\2\2Z\u0211"+
		"\3\2\2\2\\\u0214\3\2\2\2^\u0219\3\2\2\2`\u021e\3\2\2\2b\u0222\3\2\2\2"+
		"d\u0226\3\2\2\2fg\7b\2\2gh\3\2\2\2hi\b\2\2\2i\t\3\2\2\2jl\n\2\2\2kj\3"+
		"\2\2\2lm\3\2\2\2mk\3\2\2\2mn\3\2\2\2n\13\3\2\2\2ow\7$\2\2pq\7^\2\2qv\7"+
		"$\2\2rs\7^\2\2sv\7^\2\2tv\13\2\2\2up\3\2\2\2ur\3\2\2\2ut\3\2\2\2vy\3\2"+
		"\2\2wx\3\2\2\2wu\3\2\2\2xz\3\2\2\2yw\3\2\2\2z{\7$\2\2{|\3\2\2\2|}\b\4"+
		"\3\2}\r\3\2\2\2~\177\7\61\2\2\177\u0080\7,\2\2\u0080\u0084\3\2\2\2\u0081"+
		"\u0083\13\2\2\2\u0082\u0081\3\2\2\2\u0083\u0086\3\2\2\2\u0084\u0085\3"+
		"\2\2\2\u0084\u0082\3\2\2\2\u0085\u0087\3\2\2\2\u0086\u0084\3\2\2\2\u0087"+
		"\u0088\7,\2\2\u0088\u0089\7\61\2\2\u0089\u008a\3\2\2\2\u008a\u008b\b\5"+
		"\3\2\u008b\17\3\2\2\2\u008c\u008d\7\61\2\2\u008d\u008e\7\61\2\2\u008e"+
		"\u0092\3\2\2\2\u008f\u0091\n\3\2\2\u0090\u008f\3\2\2\2\u0091\u0094\3\2"+
		"\2\2\u0092\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0095\3\2\2\2\u0094"+
		"\u0092\3\2\2\2\u0095\u0096\b\6\3\2\u0096\21\3\2\2\2\u0097\u0099\t\4\2"+
		"\2\u0098\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u0098\3\2\2\2\u009a\u009b"+
		"\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009d\b\7\3\2\u009d\23\3\2\2\2\u009e"+
		"\u00a0\t\5\2\2\u009f\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u009f\3\2"+
		"\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\b\b\4\2\u00a4"+
		"\25\3\2\2\2\u00a5\u00a6\7\61\2\2\u00a6\u00a7\7,\2\2\u00a7\u00ab\3\2\2"+
		"\2\u00a8\u00aa\13\2\2\2\u00a9\u00a8\3\2\2\2\u00aa\u00ad\3\2\2\2\u00ab"+
		"\u00ac\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ac\u00ae\3\2\2\2\u00ad\u00ab\3\2"+
		"\2\2\u00ae\u00af\7,\2\2\u00af\u00b0\7\61\2\2\u00b0\u00b1\3\2\2\2\u00b1"+
		"\u00b2\b\t\4\2\u00b2\27\3\2\2\2\u00b3\u00b4\7\61\2\2\u00b4\u00b5\7\61"+
		"\2\2\u00b5\u00b9\3\2\2\2\u00b6\u00b8\n\3\2\2\u00b7\u00b6\3\2\2\2\u00b8"+
		"\u00bb\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bc\3\2"+
		"\2\2\u00bb\u00b9\3\2\2\2\u00bc\u00bd\b\n\4\2\u00bd\31\3\2\2\2\u00be\u00c0"+
		"\7^\2\2\u00bf\u00c1\7\17\2\2\u00c0\u00bf\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1"+
		"\u00c2\3\2\2\2\u00c2\u00c3\7\f\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c5\b\13"+
		"\4\2\u00c5\33\3\2\2\2\u00c6\u00c8\7\17\2\2\u00c7\u00c6\3\2\2\2\u00c7\u00c8"+
		"\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00ca\7\f\2\2\u00ca\u00cb\3\2\2\2\u00cb"+
		"\u00cc\b\f\4\2\u00cc\u00cd\b\f\5\2\u00cd\35\3\2\2\2\u00ce\u00cf\7f\2\2"+
		"\u00cf\u00d0\7g\2\2\u00d0\u00d1\7h\2\2\u00d1\u00d2\7k\2\2\u00d2\u00d3"+
		"\7p\2\2\u00d3\u00d4\7g\2\2\u00d4\u00d6\3\2\2\2\u00d5\u00d7\t\5\2\2\u00d6"+
		"\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d9\3\2"+
		"\2\2\u00d9\u00da\3\2\2\2\u00da\u00db\b\r\6\2\u00db\37\3\2\2\2\u00dc\u00dd"+
		"\7k\2\2\u00dd\u00de\7h\2\2\u00de\u00df\7f\2\2\u00df\u00e0\7g\2\2\u00e0"+
		"\u00e1\7h\2\2\u00e1\u00e3\3\2\2\2\u00e2\u00e4\t\5\2\2\u00e3\u00e2\3\2"+
		"\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6"+
		"\u00e7\3\2\2\2\u00e7\u00eb\5d\60\2\u00e8\u00ea\t\5\2\2\u00e9\u00e8\3\2"+
		"\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec"+
		"\u00ef\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00f0\t\3\2\2\u00ef\u00ee\3\2"+
		"\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f2\b\16\5\2\u00f2"+
		"!\3\2\2\2\u00f3\u00f4\7k\2\2\u00f4\u00f5\7h\2\2\u00f5\u00f6\7p\2\2\u00f6"+
		"\u00f7\7f\2\2\u00f7\u00f8\7g\2\2\u00f8\u00f9\7h\2\2\u00f9\u00fb\3\2\2"+
		"\2\u00fa\u00fc\t\5\2\2\u00fb\u00fa\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd\u00fb"+
		"\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff\u0103\5d\60\2\u0100"+
		"\u0102\t\5\2\2\u0101\u0100\3\2\2\2\u0102\u0105\3\2\2\2\u0103\u0101\3\2"+
		"\2\2\u0103\u0104\3\2\2\2\u0104\u0107\3\2\2\2\u0105\u0103\3\2\2\2\u0106"+
		"\u0108\t\3\2\2\u0107\u0106\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u0109\3\2"+
		"\2\2\u0109\u010a\b\17\5\2\u010a#\3\2\2\2\u010b\u010c\7g\2\2\u010c\u010d"+
		"\7p\2\2\u010d\u010e\7f\2\2\u010e\u010f\7k\2\2\u010f\u0110\7h\2\2\u0110"+
		"\u0114\3\2\2\2\u0111\u0113\t\5\2\2\u0112\u0111\3\2\2\2\u0113\u0116\3\2"+
		"\2\2\u0114\u0112\3\2\2\2\u0114\u0115\3\2\2\2\u0115\u0118\3\2\2\2\u0116"+
		"\u0114\3\2\2\2\u0117\u0119\t\3\2\2\u0118\u0117\3\2\2\2\u0118\u0119\3\2"+
		"\2\2\u0119\u011a\3\2\2\2\u011a\u011b\b\20\5\2\u011b%\3\2\2\2\u011c\u011d"+
		"\7n\2\2\u011d\u011e\7k\2\2\u011e\u011f\7p\2\2\u011f\u0120\7g\2\2\u0120"+
		"\u0122\3\2\2\2\u0121\u0123\n\3\2\2\u0122\u0121\3\2\2\2\u0123\u0124\3\2"+
		"\2\2\u0124\u0122\3\2\2\2\u0124\u0125\3\2\2\2\u0125\u0127\3\2\2\2\u0126"+
		"\u0128\t\3\2\2\u0127\u0126\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0129\3\2"+
		"\2\2\u0129\u012a\b\21\5\2\u012a\'\3\2\2\2\u012b\u012c\7v\2\2\u012c\u012d"+
		"\7k\2\2\u012d\u012e\7o\2\2\u012e\u012f\7g\2\2\u012f\u0130\7u\2\2\u0130"+
		"\u0131\7e\2\2\u0131\u0132\7c\2\2\u0132\u0133\7n\2\2\u0133\u0134\7g\2\2"+
		"\u0134\u0136\3\2\2\2\u0135\u0137\n\3\2\2\u0136\u0135\3\2\2\2\u0137\u0138"+
		"\3\2\2\2\u0138\u0136\3\2\2\2\u0138\u0139\3\2\2\2\u0139\u013b\3\2\2\2\u013a"+
		"\u013c\t\3\2\2\u013b\u013a\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013d\3\2"+
		"\2\2\u013d\u013e\b\22\5\2\u013e)\3\2\2\2\u013f\u0140\7w\2\2\u0140\u0141"+
		"\7p\2\2\u0141\u0142\7f\2\2\u0142\u0143\7g\2\2\u0143\u0144\7h\2\2\u0144"+
		"\u0145\7k\2\2\u0145\u0146\7p\2\2\u0146\u0147\7g\2\2\u0147\u0148\7c\2\2"+
		"\u0148\u0149\7n\2\2\u0149\u014a\7n\2\2\u014a\u014e\3\2\2\2\u014b\u014d"+
		"\t\5\2\2\u014c\u014b\3\2\2\2\u014d\u0150\3\2\2\2\u014e\u014c\3\2\2\2\u014e"+
		"\u014f\3\2\2\2\u014f\u0152\3\2\2\2\u0150\u014e\3\2\2\2\u0151\u0153\t\3"+
		"\2\2\u0152\u0151\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0154\3\2\2\2\u0154"+
		"\u0155\b\23\5\2\u0155+\3\2\2\2\u0156\u0157\7w\2\2\u0157\u0158\7p\2\2\u0158"+
		"\u0159\7f\2\2\u0159\u015a\7g\2\2\u015a\u015b\7h\2\2\u015b\u015d\3\2\2"+
		"\2\u015c\u015e\t\5\2\2\u015d\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015f\u015d"+
		"\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u0165\5d\60\2\u0162"+
		"\u0164\t\5\2\2\u0163\u0162\3\2\2\2\u0164\u0167\3\2\2\2\u0165\u0163\3\2"+
		"\2\2\u0165\u0166\3\2\2\2\u0166\u0169\3\2\2\2\u0167\u0165\3\2\2\2\u0168"+
		"\u016a\t\3\2\2\u0169\u0168\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016b\3\2"+
		"\2\2\u016b\u016c\b\24\5\2\u016c-\3\2\2\2\u016d\u0171\5d\60\2\u016e\u0170"+
		"\t\5\2\2\u016f\u016e\3\2\2\2\u0170\u0173\3\2\2\2\u0171\u016f\3\2\2\2\u0171"+
		"\u0172\3\2\2\2\u0172\u0174\3\2\2\2\u0173\u0171\3\2\2\2\u0174\u0175\7*"+
		"\2\2\u0175\u0176\b\25\7\2\u0176\u0177\3\2\2\2\u0177\u0178\b\25\b\2\u0178"+
		"/\3\2\2\2\u0179\u017d\5d\60\2\u017a\u017c\t\5\2\2\u017b\u017a\3\2\2\2"+
		"\u017c\u017f\3\2\2\2\u017d\u017b\3\2\2\2\u017d\u017e\3\2\2\2\u017e\u0180"+
		"\3\2\2\2\u017f\u017d\3\2\2\2\u0180\u0181\b\26\5\2\u0181\61\3\2\2\2\u0182"+
		"\u0184\t\5\2\2\u0183\u0182\3\2\2\2\u0184\u0185\3\2\2\2\u0185\u0183\3\2"+
		"\2\2\u0185\u0186\3\2\2\2\u0186\u0187\3\2\2\2\u0187\u0188\b\27\4\2\u0188"+
		"\63\3\2\2\2\u0189\u018b\7^\2\2\u018a\u018c\7\17\2\2\u018b\u018a\3\2\2"+
		"\2\u018b\u018c\3\2\2\2\u018c\u018d\3\2\2\2\u018d\u018e\7\f\2\2\u018e\u018f"+
		"\3\2\2\2\u018f\u0190\b\30\4\2\u0190\65\3\2\2\2\u0191\u0193\7\17\2\2\u0192"+
		"\u0191\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u0194\3\2\2\2\u0194\u0195\7\f"+
		"\2\2\u0195\u0196\3\2\2\2\u0196\u0197\b\31\4\2\u0197\67\3\2\2\2\u0198\u019c"+
		"\5d\60\2\u0199\u019b\t\5\2\2\u019a\u0199\3\2\2\2\u019b\u019e\3\2\2\2\u019c"+
		"\u019a\3\2\2\2\u019c\u019d\3\2\2\2\u019d\u019f\3\2\2\2\u019e\u019c\3\2"+
		"\2\2\u019f\u01a0\7*\2\2\u01a0\u01a1\3\2\2\2\u01a1\u01a2\b\32\t\2\u01a2"+
		"9\3\2\2\2\u01a3\u01a7\5d\60\2\u01a4\u01a6\t\5\2\2\u01a5\u01a4\3\2\2\2"+
		"\u01a6\u01a9\3\2\2\2\u01a7\u01a5\3\2\2\2\u01a7\u01a8\3\2\2\2\u01a8\u01aa"+
		"\3\2\2\2\u01a9\u01a7\3\2\2\2\u01aa\u01ab\b\33\n\2\u01ab;\3\2\2\2\u01ac"+
		"\u01ae\t\5\2\2\u01ad\u01ac\3\2\2\2\u01ae\u01af\3\2\2\2\u01af\u01ad\3\2"+
		"\2\2\u01af\u01b0\3\2\2\2\u01b0\u01b1\3\2\2\2\u01b1\u01b2\b\34\4\2\u01b2"+
		"=\3\2\2\2\u01b3\u01b5\7^\2\2\u01b4\u01b6\7\17\2\2\u01b5\u01b4\3\2\2\2"+
		"\u01b5\u01b6\3\2\2\2\u01b6\u01b7\3\2\2\2\u01b7\u01b8\7\f\2\2\u01b8\u01b9"+
		"\3\2\2\2\u01b9\u01ba\b\35\4\2\u01ba?\3\2\2\2\u01bb\u01bd\7\17\2\2\u01bc"+
		"\u01bb\3\2\2\2\u01bc\u01bd\3\2\2\2\u01bd\u01be\3\2\2\2\u01be\u01bf\7\f"+
		"\2\2\u01bf\u01c0\3\2\2\2\u01c0\u01c1\b\36\4\2\u01c1A\3\2\2\2\u01c2\u01c3"+
		"\7.\2\2\u01c3C\3\2\2\2\u01c4\u01c8\7+\2\2\u01c5\u01c7\t\5\2\2\u01c6\u01c5"+
		"\3\2\2\2\u01c7\u01ca\3\2\2\2\u01c8\u01c6\3\2\2\2\u01c8\u01c9\3\2\2\2\u01c9"+
		"\u01cb\3\2\2\2\u01ca\u01c8\3\2\2\2\u01cb\u01cc\b \n\2\u01ccE\3\2\2\2\u01cd"+
		"\u01ce\5d\60\2\u01ceG\3\2\2\2\u01cf\u01d1\7^\2\2\u01d0\u01d2\7\17\2\2"+
		"\u01d1\u01d0\3\2\2\2\u01d1\u01d2\3\2\2\2\u01d2\u01d3\3\2\2\2\u01d3\u01d4"+
		"\7\f\2\2\u01d4I\3\2\2\2\u01d5\u01d6\7^\2\2\u01d6\u01d7\3\2\2\2\u01d7\u01d8"+
		"\b#\13\2\u01d8K\3\2\2\2\u01d9\u01db\7\17\2\2\u01da\u01d9\3\2\2\2\u01da"+
		"\u01db\3\2\2\2\u01db\u01dc\3\2\2\2\u01dc\u01dd\7\f\2\2\u01dd\u01de\3\2"+
		"\2\2\u01de\u01df\b$\4\2\u01df\u01e0\b$\5\2\u01e0M\3\2\2\2\u01e1\u01e2"+
		"\7\61\2\2\u01e2\u01e3\7,\2\2\u01e3\u01e7\3\2\2\2\u01e4\u01e6\13\2\2\2"+
		"\u01e5\u01e4\3\2\2\2\u01e6\u01e9\3\2\2\2\u01e7\u01e8\3\2\2\2\u01e7\u01e5"+
		"\3\2\2\2\u01e8\u01ea\3\2\2\2\u01e9\u01e7\3\2\2\2\u01ea\u01eb\7,\2\2\u01eb"+
		"\u01ec\7\61\2\2\u01ec\u01ed\3\2\2\2\u01ed\u01ee\b%\13\2\u01eeO\3\2\2\2"+
		"\u01ef\u01f0\7\61\2\2\u01f0\u01f1\7\61\2\2\u01f1\u01f5\3\2\2\2\u01f2\u01f4"+
		"\n\3\2\2\u01f3\u01f2\3\2\2\2\u01f4\u01f7\3\2\2\2\u01f5\u01f3\3\2\2\2\u01f5"+
		"\u01f6\3\2\2\2\u01f6\u01f8\3\2\2\2\u01f7\u01f5\3\2\2\2\u01f8\u01f9\b&"+
		"\4\2\u01f9Q\3\2\2\2\u01fa\u01fb\7\61\2\2\u01fb\u01fc\3\2\2\2\u01fc\u01fd"+
		"\b\'\13\2\u01fdS\3\2\2\2\u01fe\u0200\t\5\2\2\u01ff\u01fe\3\2\2\2\u0200"+
		"\u0201\3\2\2\2\u0201\u01ff\3\2\2\2\u0201\u0202\3\2\2\2\u0202\u0203\3\2"+
		"\2\2\u0203\u0204\b(\13\2\u0204U\3\2\2\2\u0205\u0207\n\6\2\2\u0206\u0205"+
		"\3\2\2\2\u0207\u0208\3\2\2\2\u0208\u0206\3\2\2\2\u0208\u0209\3\2\2\2\u0209"+
		"W\3\2\2\2\u020a\u020c\t\5\2\2\u020b\u020a\3\2\2\2\u020c\u020d\3\2\2\2"+
		"\u020d\u020b\3\2\2\2\u020d\u020e\3\2\2\2\u020e\u020f\3\2\2\2\u020f\u0210"+
		"\b*\f\2\u0210Y\3\2\2\2\u0211\u0212\7.\2\2\u0212\u0213\b+\r\2\u0213[\3"+
		"\2\2\2\u0214\u0215\t\7\2\2\u0215\u0216\b,\16\2\u0216\u0217\3\2\2\2\u0217"+
		"\u0218\b,\f\2\u0218]\3\2\2\2\u0219\u021a\t\b\2\2\u021a\u021b\b-\17\2\u021b"+
		"\u021c\3\2\2\2\u021c\u021d\b-\f\2\u021d_\3\2\2\2\u021e\u021f\7+\2\2\u021f"+
		"\u0220\b.\20\2\u0220a\3\2\2\2\u0221\u0223\n\t\2\2\u0222\u0221\3\2\2\2"+
		"\u0223\u0224\3\2\2\2\u0224\u0222\3\2\2\2\u0224\u0225\3\2\2\2\u0225c\3"+
		"\2\2\2\u0226\u022a\t\n\2\2\u0227\u0229\t\13\2\2\u0228\u0227\3\2\2\2\u0229"+
		"\u022c\3\2\2\2\u022a\u0228\3\2\2\2\u022a\u022b\3\2\2\2\u022be\3\2\2\2"+
		"\u022c\u022a\3\2\2\29\2\3\4\5\6\7muw\u0084\u0092\u009a\u00a1\u00ab\u00b9"+
		"\u00c0\u00c7\u00d8\u00e5\u00eb\u00ef\u00fd\u0103\u0107\u0114\u0118\u0124"+
		"\u0127\u0138\u013b\u014e\u0152\u015f\u0165\u0169\u0171\u017d\u0185\u018b"+
		"\u0192\u019c\u01a7\u01af\u01b5\u01bc\u01c8\u01d1\u01da\u01e7\u01f5\u0201"+
		"\u0208\u020d\u0224\u022a\21\4\3\2\t\4\2\2\3\2\4\2\2\4\4\2\3\25\2\4\7\2"+
		"\4\5\2\4\6\2\t\"\2\t%\2\3+\3\3,\4\3-\5\3.\6";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}