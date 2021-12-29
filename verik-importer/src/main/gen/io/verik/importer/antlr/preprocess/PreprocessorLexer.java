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
		DIRECTIVE_TIMESCALE=12, DIRECTIVE_UNDEFINEALL=13, DIRECTIVE_UNDEF=14, 
		DIRECTIVE_MACRO=15, DEFINE_WHITESPACE=16, DEFINE_LINE_CONTINUATION=17, 
		DEFINE_NEW_LINE=18, DEFINE_MACRO_ARG=19, DEFINE_MACRO=20, DEFINE_ARG_WHITESPACE=21, 
		DEFINE_ARG_LINE_CONTINUATION=22, DEFINE_ARG_NEW_LINE=23, DEFINE_ARG_COMMA=24, 
		DEFINE_ARG_RP=25, DEFINE_ARG_IDENTIFIER=26, TEXT_LINE_CONTINUATION=27, 
		TEXT_NEW_LINE=28, TEXT=29, TEXT_LINE_BACK_SLASH=30, TEXT_SLASH=31;
	public static final int
		DIRECTIVE_MODE=1, DEFINE_MODE=2, DEFINE_ARG_MODE=3, TEXT_MODE=4;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "DIRECTIVE_MODE", "DEFINE_MODE", "DEFINE_ARG_MODE", "TEXT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"BACKTICK", "CODE", "STRING_LITERAL", "BLOCK_COMMENT", "LINE_COMMENT", 
			"WHITESPACE", "DIRECTIVE_WHITESPACE", "DIRECTIVE_BLOCK_COMMENT", "DIRECTIVE_LINE_COMMENT", 
			"DIRECTIVE_LINE_CONTINUATION", "DIRECTIVE_NEW_LINE", "DIRECTIVE_DEFINE", 
			"DIRECTIVE_IFDEF", "DIRECTIVE_IFNDEF", "DIRECTIVE_ENDIF", "DIRECTIVE_TIMESCALE", 
			"DIRECTIVE_UNDEFINEALL", "DIRECTIVE_UNDEF", "DIRECTIVE_MACRO", "DEFINE_WHITESPACE", 
			"DEFINE_LINE_CONTINUATION", "DEFINE_NEW_LINE", "DEFINE_MACRO_ARG", "DEFINE_MACRO", 
			"DEFINE_ARG_WHITESPACE", "DEFINE_ARG_LINE_CONTINUATION", "DEFINE_ARG_NEW_LINE", 
			"DEFINE_ARG_COMMA", "DEFINE_ARG_RP", "DEFINE_ARG_IDENTIFIER", "TEXT_LINE_CONTINUATION", 
			"TEXT_LINE_BACK_SLASH", "TEXT_NEW_LINE", "TEXT_BLOCK_COMMENT", "TEXT_LINE_COMMENT", 
			"TEXT_SLASH", "TEXT_WHITESPACE", "TEXT", "STRING", "IDENTIFIER"
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2!\u01e4\b\1\b\1\b"+
		"\1\b\1\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t"+
		"\t\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4"+
		"\21\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4"+
		"\30\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4"+
		"\37\t\37\4 \t \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)"+
		"\t)\3\2\3\2\3\2\3\2\3\3\6\3]\n\3\r\3\16\3^\3\4\3\4\3\4\3\4\3\5\3\5\3\5"+
		"\3\5\7\5i\n\5\f\5\16\5l\13\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\7\6w"+
		"\n\6\f\6\16\6z\13\6\3\6\3\6\3\7\6\7\177\n\7\r\7\16\7\u0080\3\7\3\7\3\b"+
		"\6\b\u0086\n\b\r\b\16\b\u0087\3\b\3\b\3\t\3\t\3\t\3\t\7\t\u0090\n\t\f"+
		"\t\16\t\u0093\13\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\7\n\u009e\n\n\f"+
		"\n\16\n\u00a1\13\n\3\n\3\n\3\13\3\13\5\13\u00a7\n\13\3\13\3\13\3\13\3"+
		"\13\3\f\5\f\u00ae\n\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\6\r\u00bd\n\r\r\r\16\r\u00be\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\6\16\u00ca\n\16\r\16\16\16\u00cb\3\16\3\16\7\16\u00d0\n\16\f\16"+
		"\16\16\u00d3\13\16\3\16\5\16\u00d6\n\16\3\16\3\16\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\6\17\u00e2\n\17\r\17\16\17\u00e3\3\17\3\17\7\17\u00e8"+
		"\n\17\f\17\16\17\u00eb\13\17\3\17\5\17\u00ee\n\17\3\17\3\17\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\7\20\u00f9\n\20\f\20\16\20\u00fc\13\20\3\20"+
		"\5\20\u00ff\n\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\6\21\u010e\n\21\r\21\16\21\u010f\3\21\5\21\u0113\n\21\3\21"+
		"\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\7\22\u0124\n\22\f\22\16\22\u0127\13\22\3\22\5\22\u012a\n\22\3\22\3\22"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\23\6\23\u0135\n\23\r\23\16\23\u0136\3"+
		"\23\3\23\7\23\u013b\n\23\f\23\16\23\u013e\13\23\3\23\5\23\u0141\n\23\3"+
		"\23\3\23\3\24\3\24\3\24\3\24\3\25\6\25\u014a\n\25\r\25\16\25\u014b\3\25"+
		"\3\25\3\26\3\26\5\26\u0152\n\26\3\26\3\26\3\26\3\26\3\27\5\27\u0159\n"+
		"\27\3\27\3\27\3\27\3\27\3\30\3\30\7\30\u0161\n\30\f\30\16\30\u0164\13"+
		"\30\3\30\3\30\3\30\3\30\3\31\3\31\7\31\u016c\n\31\f\31\16\31\u016f\13"+
		"\31\3\31\3\31\3\32\6\32\u0174\n\32\r\32\16\32\u0175\3\32\3\32\3\33\3\33"+
		"\5\33\u017c\n\33\3\33\3\33\3\33\3\33\3\34\5\34\u0183\n\34\3\34\3\34\3"+
		"\34\3\34\3\35\3\35\3\36\3\36\7\36\u018d\n\36\f\36\16\36\u0190\13\36\3"+
		"\36\3\36\3\37\3\37\3 \3 \5 \u0198\n \3 \3 \3!\3!\3!\3!\3\"\5\"\u01a1\n"+
		"\"\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\7#\u01ac\n#\f#\16#\u01af\13#\3#\3#"+
		"\3#\3#\3#\3$\3$\3$\3$\7$\u01ba\n$\f$\16$\u01bd\13$\3$\3$\3%\3%\3%\3%\3"+
		"&\6&\u01c6\n&\r&\16&\u01c7\3&\3&\3\'\6\'\u01cd\n\'\r\'\16\'\u01ce\3(\3"+
		"(\3(\3(\3(\3(\7(\u01d7\n(\f(\16(\u01da\13(\3(\3(\3)\3)\7)\u01e0\n)\f)"+
		"\16)\u01e3\13)\6j\u0091\u01ad\u01d8\2*\7\3\t\4\13\2\r\2\17\2\21\2\23\5"+
		"\25\6\27\7\31\b\33\t\35\n\37\13!\f#\r%\16\'\17)\20+\21-\22/\23\61\24\63"+
		"\25\65\26\67\279\30;\31=\32?\33A\34C\35E G\36I\2K\2M!O\2Q\37S\2U\2\7\2"+
		"\3\4\5\6\t\7\2\f\f\17\17$$\61\61bb\4\2\f\f\17\17\5\2\13\f\17\17\"\"\4"+
		"\2\13\13\"\"\6\2\f\f\17\17\61\61^^\5\2C\\aac|\7\2&&\62;C\\aac|\2\u0209"+
		"\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3"+
		"\2\2\2\3\23\3\2\2\2\3\25\3\2\2\2\3\27\3\2\2\2\3\31\3\2\2\2\3\33\3\2\2"+
		"\2\3\35\3\2\2\2\3\37\3\2\2\2\3!\3\2\2\2\3#\3\2\2\2\3%\3\2\2\2\3\'\3\2"+
		"\2\2\3)\3\2\2\2\3+\3\2\2\2\4-\3\2\2\2\4/\3\2\2\2\4\61\3\2\2\2\4\63\3\2"+
		"\2\2\4\65\3\2\2\2\5\67\3\2\2\2\59\3\2\2\2\5;\3\2\2\2\5=\3\2\2\2\5?\3\2"+
		"\2\2\5A\3\2\2\2\6C\3\2\2\2\6E\3\2\2\2\6G\3\2\2\2\6I\3\2\2\2\6K\3\2\2\2"+
		"\6M\3\2\2\2\6O\3\2\2\2\6Q\3\2\2\2\7W\3\2\2\2\t\\\3\2\2\2\13`\3\2\2\2\r"+
		"d\3\2\2\2\17r\3\2\2\2\21~\3\2\2\2\23\u0085\3\2\2\2\25\u008b\3\2\2\2\27"+
		"\u0099\3\2\2\2\31\u00a4\3\2\2\2\33\u00ad\3\2\2\2\35\u00b4\3\2\2\2\37\u00c2"+
		"\3\2\2\2!\u00d9\3\2\2\2#\u00f1\3\2\2\2%\u0102\3\2\2\2\'\u0116\3\2\2\2"+
		")\u012d\3\2\2\2+\u0144\3\2\2\2-\u0149\3\2\2\2/\u014f\3\2\2\2\61\u0158"+
		"\3\2\2\2\63\u015e\3\2\2\2\65\u0169\3\2\2\2\67\u0173\3\2\2\29\u0179\3\2"+
		"\2\2;\u0182\3\2\2\2=\u0188\3\2\2\2?\u018a\3\2\2\2A\u0193\3\2\2\2C\u0195"+
		"\3\2\2\2E\u019b\3\2\2\2G\u01a0\3\2\2\2I\u01a7\3\2\2\2K\u01b5\3\2\2\2M"+
		"\u01c0\3\2\2\2O\u01c5\3\2\2\2Q\u01cc\3\2\2\2S\u01d0\3\2\2\2U\u01dd\3\2"+
		"\2\2WX\7b\2\2XY\3\2\2\2YZ\b\2\2\2Z\b\3\2\2\2[]\n\2\2\2\\[\3\2\2\2]^\3"+
		"\2\2\2^\\\3\2\2\2^_\3\2\2\2_\n\3\2\2\2`a\5S(\2ab\3\2\2\2bc\b\4\3\2c\f"+
		"\3\2\2\2de\7\61\2\2ef\7,\2\2fj\3\2\2\2gi\13\2\2\2hg\3\2\2\2il\3\2\2\2"+
		"jk\3\2\2\2jh\3\2\2\2km\3\2\2\2lj\3\2\2\2mn\7,\2\2no\7\61\2\2op\3\2\2\2"+
		"pq\b\5\3\2q\16\3\2\2\2rs\7\61\2\2st\7\61\2\2tx\3\2\2\2uw\n\3\2\2vu\3\2"+
		"\2\2wz\3\2\2\2xv\3\2\2\2xy\3\2\2\2y{\3\2\2\2zx\3\2\2\2{|\b\6\3\2|\20\3"+
		"\2\2\2}\177\t\4\2\2~}\3\2\2\2\177\u0080\3\2\2\2\u0080~\3\2\2\2\u0080\u0081"+
		"\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0083\b\7\3\2\u0083\22\3\2\2\2\u0084"+
		"\u0086\t\5\2\2\u0085\u0084\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0085\3\2"+
		"\2\2\u0087\u0088\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u008a\b\b\4\2\u008a"+
		"\24\3\2\2\2\u008b\u008c\7\61\2\2\u008c\u008d\7,\2\2\u008d\u0091\3\2\2"+
		"\2\u008e\u0090\13\2\2\2\u008f\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091"+
		"\u0092\3\2\2\2\u0091\u008f\3\2\2\2\u0092\u0094\3\2\2\2\u0093\u0091\3\2"+
		"\2\2\u0094\u0095\7,\2\2\u0095\u0096\7\61\2\2\u0096\u0097\3\2\2\2\u0097"+
		"\u0098\b\t\4\2\u0098\26\3\2\2\2\u0099\u009a\7\61\2\2\u009a\u009b\7\61"+
		"\2\2\u009b\u009f\3\2\2\2\u009c\u009e\n\3\2\2\u009d\u009c\3\2\2\2\u009e"+
		"\u00a1\3\2\2\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a2\3\2"+
		"\2\2\u00a1\u009f\3\2\2\2\u00a2\u00a3\b\n\4\2\u00a3\30\3\2\2\2\u00a4\u00a6"+
		"\7^\2\2\u00a5\u00a7\7\17\2\2\u00a6\u00a5\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7"+
		"\u00a8\3\2\2\2\u00a8\u00a9\7\f\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ab\b\13"+
		"\4\2\u00ab\32\3\2\2\2\u00ac\u00ae\7\17\2\2\u00ad\u00ac\3\2\2\2\u00ad\u00ae"+
		"\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00b0\7\f\2\2\u00b0\u00b1\3\2\2\2\u00b1"+
		"\u00b2\b\f\4\2\u00b2\u00b3\b\f\5\2\u00b3\34\3\2\2\2\u00b4\u00b5\7f\2\2"+
		"\u00b5\u00b6\7g\2\2\u00b6\u00b7\7h\2\2\u00b7\u00b8\7k\2\2\u00b8\u00b9"+
		"\7p\2\2\u00b9\u00ba\7g\2\2\u00ba\u00bc\3\2\2\2\u00bb\u00bd\t\5\2\2\u00bc"+
		"\u00bb\3\2\2\2\u00bd\u00be\3\2\2\2\u00be\u00bc\3\2\2\2\u00be\u00bf\3\2"+
		"\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c1\b\r\6\2\u00c1\36\3\2\2\2\u00c2\u00c3"+
		"\7k\2\2\u00c3\u00c4\7h\2\2\u00c4\u00c5\7f\2\2\u00c5\u00c6\7g\2\2\u00c6"+
		"\u00c7\7h\2\2\u00c7\u00c9\3\2\2\2\u00c8\u00ca\t\5\2\2\u00c9\u00c8\3\2"+
		"\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00c9\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc"+
		"\u00cd\3\2\2\2\u00cd\u00d1\5U)\2\u00ce\u00d0\t\5\2\2\u00cf\u00ce\3\2\2"+
		"\2\u00d0\u00d3\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d5"+
		"\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d4\u00d6\t\3\2\2\u00d5\u00d4\3\2\2\2\u00d5"+
		"\u00d6\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00d8\b\16\5\2\u00d8 \3\2\2\2"+
		"\u00d9\u00da\7k\2\2\u00da\u00db\7h\2\2\u00db\u00dc\7p\2\2\u00dc\u00dd"+
		"\7f\2\2\u00dd\u00de\7g\2\2\u00de\u00df\7h\2\2\u00df\u00e1\3\2\2\2\u00e0"+
		"\u00e2\t\5\2\2\u00e1\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e1\3\2"+
		"\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5\u00e9\5U)\2\u00e6\u00e8"+
		"\t\5\2\2\u00e7\u00e6\3\2\2\2\u00e8\u00eb\3\2\2\2\u00e9\u00e7\3\2\2\2\u00e9"+
		"\u00ea\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00ec\u00ee\t\3"+
		"\2\2\u00ed\u00ec\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef"+
		"\u00f0\b\17\5\2\u00f0\"\3\2\2\2\u00f1\u00f2\7g\2\2\u00f2\u00f3\7p\2\2"+
		"\u00f3\u00f4\7f\2\2\u00f4\u00f5\7k\2\2\u00f5\u00f6\7h\2\2\u00f6\u00fa"+
		"\3\2\2\2\u00f7\u00f9\t\5\2\2\u00f8\u00f7\3\2\2\2\u00f9\u00fc\3\2\2\2\u00fa"+
		"\u00f8\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb\u00fe\3\2\2\2\u00fc\u00fa\3\2"+
		"\2\2\u00fd\u00ff\t\3\2\2\u00fe\u00fd\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff"+
		"\u0100\3\2\2\2\u0100\u0101\b\20\5\2\u0101$\3\2\2\2\u0102\u0103\7v\2\2"+
		"\u0103\u0104\7k\2\2\u0104\u0105\7o\2\2\u0105\u0106\7g\2\2\u0106\u0107"+
		"\7u\2\2\u0107\u0108\7e\2\2\u0108\u0109\7c\2\2\u0109\u010a\7n\2\2\u010a"+
		"\u010b\7g\2\2\u010b\u010d\3\2\2\2\u010c\u010e\n\3\2\2\u010d\u010c\3\2"+
		"\2\2\u010e\u010f\3\2\2\2\u010f\u010d\3\2\2\2\u010f\u0110\3\2\2\2\u0110"+
		"\u0112\3\2\2\2\u0111\u0113\t\3\2\2\u0112\u0111\3\2\2\2\u0112\u0113\3\2"+
		"\2\2\u0113\u0114\3\2\2\2\u0114\u0115\b\21\5\2\u0115&\3\2\2\2\u0116\u0117"+
		"\7w\2\2\u0117\u0118\7p\2\2\u0118\u0119\7f\2\2\u0119\u011a\7g\2\2\u011a"+
		"\u011b\7h\2\2\u011b\u011c\7k\2\2\u011c\u011d\7p\2\2\u011d\u011e\7g\2\2"+
		"\u011e\u011f\7c\2\2\u011f\u0120\7n\2\2\u0120\u0121\7n\2\2\u0121\u0125"+
		"\3\2\2\2\u0122\u0124\t\5\2\2\u0123\u0122\3\2\2\2\u0124\u0127\3\2\2\2\u0125"+
		"\u0123\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0129\3\2\2\2\u0127\u0125\3\2"+
		"\2\2\u0128\u012a\t\3\2\2\u0129\u0128\3\2\2\2\u0129\u012a\3\2\2\2\u012a"+
		"\u012b\3\2\2\2\u012b\u012c\b\22\5\2\u012c(\3\2\2\2\u012d\u012e\7w\2\2"+
		"\u012e\u012f\7p\2\2\u012f\u0130\7f\2\2\u0130\u0131\7g\2\2\u0131\u0132"+
		"\7h\2\2\u0132\u0134\3\2\2\2\u0133\u0135\t\5\2\2\u0134\u0133\3\2\2\2\u0135"+
		"\u0136\3\2\2\2\u0136\u0134\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u0138\3\2"+
		"\2\2\u0138\u013c\5U)\2\u0139\u013b\t\5\2\2\u013a\u0139\3\2\2\2\u013b\u013e"+
		"\3\2\2\2\u013c\u013a\3\2\2\2\u013c\u013d\3\2\2\2\u013d\u0140\3\2\2\2\u013e"+
		"\u013c\3\2\2\2\u013f\u0141\t\3\2\2\u0140\u013f\3\2\2\2\u0140\u0141\3\2"+
		"\2\2\u0141\u0142\3\2\2\2\u0142\u0143\b\23\5\2\u0143*\3\2\2\2\u0144\u0145"+
		"\5U)\2\u0145\u0146\3\2\2\2\u0146\u0147\b\24\5\2\u0147,\3\2\2\2\u0148\u014a"+
		"\t\5\2\2\u0149\u0148\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u0149\3\2\2\2\u014b"+
		"\u014c\3\2\2\2\u014c\u014d\3\2\2\2\u014d\u014e\b\25\4\2\u014e.\3\2\2\2"+
		"\u014f\u0151\7^\2\2\u0150\u0152\7\17\2\2\u0151\u0150\3\2\2\2\u0151\u0152"+
		"\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0154\7\f\2\2\u0154\u0155\3\2\2\2\u0155"+
		"\u0156\b\26\4\2\u0156\60\3\2\2\2\u0157\u0159\7\17\2\2\u0158\u0157\3\2"+
		"\2\2\u0158\u0159\3\2\2\2\u0159\u015a\3\2\2\2\u015a\u015b\7\f\2\2\u015b"+
		"\u015c\3\2\2\2\u015c\u015d\b\27\4\2\u015d\62\3\2\2\2\u015e\u0162\5U)\2"+
		"\u015f\u0161\t\5\2\2\u0160\u015f\3\2\2\2\u0161\u0164\3\2\2\2\u0162\u0160"+
		"\3\2\2\2\u0162\u0163\3\2\2\2\u0163\u0165\3\2\2\2\u0164\u0162\3\2\2\2\u0165"+
		"\u0166\7*\2\2\u0166\u0167\3\2\2\2\u0167\u0168\b\30\7\2\u0168\64\3\2\2"+
		"\2\u0169\u016d\5U)\2\u016a\u016c\t\5\2\2\u016b\u016a\3\2\2\2\u016c\u016f"+
		"\3\2\2\2\u016d\u016b\3\2\2\2\u016d\u016e\3\2\2\2\u016e\u0170\3\2\2\2\u016f"+
		"\u016d\3\2\2\2\u0170\u0171\b\31\b\2\u0171\66\3\2\2\2\u0172\u0174\t\5\2"+
		"\2\u0173\u0172\3\2\2\2\u0174\u0175\3\2\2\2\u0175\u0173\3\2\2\2\u0175\u0176"+
		"\3\2\2\2\u0176\u0177\3\2\2\2\u0177\u0178\b\32\4\2\u01788\3\2\2\2\u0179"+
		"\u017b\7^\2\2\u017a\u017c\7\17\2\2\u017b\u017a\3\2\2\2\u017b\u017c\3\2"+
		"\2\2\u017c\u017d\3\2\2\2\u017d\u017e\7\f\2\2\u017e\u017f\3\2\2\2\u017f"+
		"\u0180\b\33\4\2\u0180:\3\2\2\2\u0181\u0183\7\17\2\2\u0182\u0181\3\2\2"+
		"\2\u0182\u0183\3\2\2\2\u0183\u0184\3\2\2\2\u0184\u0185\7\f\2\2\u0185\u0186"+
		"\3\2\2\2\u0186\u0187\b\34\4\2\u0187<\3\2\2\2\u0188\u0189\7.\2\2\u0189"+
		">\3\2\2\2\u018a\u018e\7+\2\2\u018b\u018d\t\5\2\2\u018c\u018b\3\2\2\2\u018d"+
		"\u0190\3\2\2\2\u018e\u018c\3\2\2\2\u018e\u018f\3\2\2\2\u018f\u0191\3\2"+
		"\2\2\u0190\u018e\3\2\2\2\u0191\u0192\b\36\b\2\u0192@\3\2\2\2\u0193\u0194"+
		"\5U)\2\u0194B\3\2\2\2\u0195\u0197\7^\2\2\u0196\u0198\7\17\2\2\u0197\u0196"+
		"\3\2\2\2\u0197\u0198\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u019a\7\f\2\2\u019a"+
		"D\3\2\2\2\u019b\u019c\7^\2\2\u019c\u019d\3\2\2\2\u019d\u019e\b!\t\2\u019e"+
		"F\3\2\2\2\u019f\u01a1\7\17\2\2\u01a0\u019f\3\2\2\2\u01a0\u01a1\3\2\2\2"+
		"\u01a1\u01a2\3\2\2\2\u01a2\u01a3\7\f\2\2\u01a3\u01a4\3\2\2\2\u01a4\u01a5"+
		"\b\"\4\2\u01a5\u01a6\b\"\5\2\u01a6H\3\2\2\2\u01a7\u01a8\7\61\2\2\u01a8"+
		"\u01a9\7,\2\2\u01a9\u01ad\3\2\2\2\u01aa\u01ac\13\2\2\2\u01ab\u01aa\3\2"+
		"\2\2\u01ac\u01af\3\2\2\2\u01ad\u01ae\3\2\2\2\u01ad\u01ab\3\2\2\2\u01ae"+
		"\u01b0\3\2\2\2\u01af\u01ad\3\2\2\2\u01b0\u01b1\7,\2\2\u01b1\u01b2\7\61"+
		"\2\2\u01b2\u01b3\3\2\2\2\u01b3\u01b4\b#\t\2\u01b4J\3\2\2\2\u01b5\u01b6"+
		"\7\61\2\2\u01b6\u01b7\7\61\2\2\u01b7\u01bb\3\2\2\2\u01b8\u01ba\n\3\2\2"+
		"\u01b9\u01b8\3\2\2\2\u01ba\u01bd\3\2\2\2\u01bb\u01b9\3\2\2\2\u01bb\u01bc"+
		"\3\2\2\2\u01bc\u01be\3\2\2\2\u01bd\u01bb\3\2\2\2\u01be\u01bf\b$\t\2\u01bf"+
		"L\3\2\2\2\u01c0\u01c1\7\61\2\2\u01c1\u01c2\3\2\2\2\u01c2\u01c3\b%\t\2"+
		"\u01c3N\3\2\2\2\u01c4\u01c6\t\5\2\2\u01c5\u01c4\3\2\2\2\u01c6\u01c7\3"+
		"\2\2\2\u01c7\u01c5\3\2\2\2\u01c7\u01c8\3\2\2\2\u01c8\u01c9\3\2\2\2\u01c9"+
		"\u01ca\b&\t\2\u01caP\3\2\2\2\u01cb\u01cd\n\6\2\2\u01cc\u01cb\3\2\2\2\u01cd"+
		"\u01ce\3\2\2\2\u01ce\u01cc\3\2\2\2\u01ce\u01cf\3\2\2\2\u01cfR\3\2\2\2"+
		"\u01d0\u01d8\7$\2\2\u01d1\u01d2\7^\2\2\u01d2\u01d7\7$\2\2\u01d3\u01d4"+
		"\7^\2\2\u01d4\u01d7\7^\2\2\u01d5\u01d7\13\2\2\2\u01d6\u01d1\3\2\2\2\u01d6"+
		"\u01d3\3\2\2\2\u01d6\u01d5\3\2\2\2\u01d7\u01da\3\2\2\2\u01d8\u01d9\3\2"+
		"\2\2\u01d8\u01d6\3\2\2\2\u01d9\u01db\3\2\2\2\u01da\u01d8\3\2\2\2\u01db"+
		"\u01dc\7$\2\2\u01dcT\3\2\2\2\u01dd\u01e1\t\7\2\2\u01de\u01e0\t\b\2\2\u01df"+
		"\u01de\3\2\2\2\u01e0\u01e3\3\2\2\2\u01e1\u01df\3\2\2\2\u01e1\u01e2\3\2"+
		"\2\2\u01e2V\3\2\2\2\u01e3\u01e1\3\2\2\2\62\2\3\4\5\6^jx\u0080\u0087\u0091"+
		"\u009f\u00a6\u00ad\u00be\u00cb\u00d1\u00d5\u00e3\u00e9\u00ed\u00fa\u00fe"+
		"\u010f\u0112\u0125\u0129\u0136\u013c\u0140\u014b\u0151\u0158\u0162\u016d"+
		"\u0175\u017b\u0182\u018e\u0197\u01a0\u01ad\u01bb\u01c7\u01ce\u01d6\u01d8"+
		"\u01e1\n\4\3\2\t\4\2\2\3\2\4\2\2\4\4\2\4\5\2\4\6\2\t\37\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}