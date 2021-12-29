// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/SystemVerilogPreprocessorLexer.g4 by ANTLR 4.9.2
package io.verik.importer.antlr;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SystemVerilogPreprocessorLexer extends Lexer {
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
		DIRECTIVE_MODE=1, DEFINE_MODE=2, TEXT_MODE=3;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "DIRECTIVE_MODE", "DEFINE_MODE", "TEXT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"BACKTICK", "CODE", "STRING_LITERAL", "BLOCK_COMMENT", "LINE_COMMENT", 
			"WHITESPACE", "DIRECTIVE_WHITESPACE", "DIRECTIVE_BLOCK_COMMENT", "DIRECTIVE_LINE_COMMENT", 
			"DIRECTIVE_LINE_CONTINUATION", "DIRECTIVE_NEW_LINE", "DEFINE", "IFDEF", 
			"IFNDEF", "ENDIF", "TIMESCALE", "UNDEF_ALL", "UNDEF", "DEFINED_MACRO", 
			"DEFINE_WHITESPACE", "DEFINE_LINE_CONTINUATION", "DEFINE_NEW_LINE", "DEFINE_MACRO", 
			"TEXT_LINE_CONTINUATION", "TEXT_LINE_BACK_SLASH", "TEXT_NEW_LINE", "TEXT_BLOCK_COMMENT", 
			"TEXT_LINE_COMMENT", "TEXT_SLASH", "TEXT_WHITESPACE", "TEXT", "STRING", 
			"IDENTIFIER"
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


	public SystemVerilogPreprocessorLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SystemVerilogPreprocessorLexer.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\32\u01a4\b\1\b\1"+
		"\b\1\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t"+
		"\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21"+
		"\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30"+
		"\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37"+
		"\t\37\4 \t \4!\t!\4\"\t\"\3\2\3\2\3\2\3\2\3\3\6\3N\n\3\r\3\16\3O\3\4\3"+
		"\4\3\4\3\4\3\5\3\5\3\5\3\5\7\5Z\n\5\f\5\16\5]\13\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\6\3\6\3\6\3\6\7\6h\n\6\f\6\16\6k\13\6\3\6\3\6\3\7\6\7p\n\7\r\7\16\7"+
		"q\3\7\3\7\3\b\6\bw\n\b\r\b\16\bx\3\b\3\b\3\t\3\t\3\t\3\t\7\t\u0081\n\t"+
		"\f\t\16\t\u0084\13\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\7\n\u008f\n\n"+
		"\f\n\16\n\u0092\13\n\3\n\3\n\3\13\3\13\5\13\u0098\n\13\3\13\3\13\3\13"+
		"\3\13\3\f\5\f\u009f\n\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\6\r\u00ae\n\r\r\r\16\r\u00af\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\6\16\u00bb\n\16\r\16\16\16\u00bc\3\16\3\16\7\16\u00c1\n\16\f"+
		"\16\16\16\u00c4\13\16\3\16\5\16\u00c7\n\16\3\16\3\16\3\17\3\17\3\17\3"+
		"\17\3\17\3\17\3\17\3\17\6\17\u00d3\n\17\r\17\16\17\u00d4\3\17\3\17\7\17"+
		"\u00d9\n\17\f\17\16\17\u00dc\13\17\3\17\5\17\u00df\n\17\3\17\3\17\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\7\20\u00ea\n\20\f\20\16\20\u00ed\13\20"+
		"\3\20\5\20\u00f0\n\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\6\21\u00ff\n\21\r\21\16\21\u0100\3\21\5\21\u0104\n\21"+
		"\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\7\22\u0115\n\22\f\22\16\22\u0118\13\22\3\22\5\22\u011b\n\22\3\22"+
		"\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\6\23\u0126\n\23\r\23\16\23\u0127"+
		"\3\23\3\23\7\23\u012c\n\23\f\23\16\23\u012f\13\23\3\23\5\23\u0132\n\23"+
		"\3\23\3\23\3\24\3\24\3\24\3\24\3\25\6\25\u013b\n\25\r\25\16\25\u013c\3"+
		"\25\3\25\3\26\3\26\5\26\u0143\n\26\3\26\3\26\3\26\3\26\3\27\5\27\u014a"+
		"\n\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\31\3\31\5\31\u0156\n\31"+
		"\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\33\5\33\u0161\n\33\3\33\3\33"+
		"\3\33\3\33\3\33\3\34\3\34\3\34\3\34\7\34\u016c\n\34\f\34\16\34\u016f\13"+
		"\34\3\34\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3\35\7\35\u017a\n\35\f\35"+
		"\16\35\u017d\13\35\3\35\3\35\3\36\3\36\3\36\3\36\3\37\6\37\u0186\n\37"+
		"\r\37\16\37\u0187\3\37\3\37\3 \6 \u018d\n \r \16 \u018e\3!\3!\3!\3!\3"+
		"!\3!\7!\u0197\n!\f!\16!\u019a\13!\3!\3!\3\"\3\"\7\"\u01a0\n\"\f\"\16\""+
		"\u01a3\13\"\6[\u0082\u016d\u0198\2#\6\3\b\4\n\2\f\2\16\2\20\2\22\5\24"+
		"\6\26\7\30\b\32\t\34\n\36\13 \f\"\r$\16&\17(\20*\21,\22.\23\60\24\62\25"+
		"\64\26\66\318\27:\2<\2>\32@\2B\30D\2F\2\6\2\3\4\5\t\7\2\f\f\17\17$$\61"+
		"\61bb\4\2\f\f\17\17\5\2\13\f\17\17\"\"\4\2\13\13\"\"\6\2\f\f\17\17\61"+
		"\61^^\5\2C\\aac|\7\2&&\62;C\\aac|\2\u01c4\2\6\3\2\2\2\2\b\3\2\2\2\2\n"+
		"\3\2\2\2\2\f\3\2\2\2\2\16\3\2\2\2\2\20\3\2\2\2\3\22\3\2\2\2\3\24\3\2\2"+
		"\2\3\26\3\2\2\2\3\30\3\2\2\2\3\32\3\2\2\2\3\34\3\2\2\2\3\36\3\2\2\2\3"+
		" \3\2\2\2\3\"\3\2\2\2\3$\3\2\2\2\3&\3\2\2\2\3(\3\2\2\2\3*\3\2\2\2\4,\3"+
		"\2\2\2\4.\3\2\2\2\4\60\3\2\2\2\4\62\3\2\2\2\5\64\3\2\2\2\5\66\3\2\2\2"+
		"\58\3\2\2\2\5:\3\2\2\2\5<\3\2\2\2\5>\3\2\2\2\5@\3\2\2\2\5B\3\2\2\2\6H"+
		"\3\2\2\2\bM\3\2\2\2\nQ\3\2\2\2\fU\3\2\2\2\16c\3\2\2\2\20o\3\2\2\2\22v"+
		"\3\2\2\2\24|\3\2\2\2\26\u008a\3\2\2\2\30\u0095\3\2\2\2\32\u009e\3\2\2"+
		"\2\34\u00a5\3\2\2\2\36\u00b3\3\2\2\2 \u00ca\3\2\2\2\"\u00e2\3\2\2\2$\u00f3"+
		"\3\2\2\2&\u0107\3\2\2\2(\u011e\3\2\2\2*\u0135\3\2\2\2,\u013a\3\2\2\2."+
		"\u0140\3\2\2\2\60\u0149\3\2\2\2\62\u014f\3\2\2\2\64\u0153\3\2\2\2\66\u015b"+
		"\3\2\2\28\u0160\3\2\2\2:\u0167\3\2\2\2<\u0175\3\2\2\2>\u0180\3\2\2\2@"+
		"\u0185\3\2\2\2B\u018c\3\2\2\2D\u0190\3\2\2\2F\u019d\3\2\2\2HI\7b\2\2I"+
		"J\3\2\2\2JK\b\2\2\2K\7\3\2\2\2LN\n\2\2\2ML\3\2\2\2NO\3\2\2\2OM\3\2\2\2"+
		"OP\3\2\2\2P\t\3\2\2\2QR\5D!\2RS\3\2\2\2ST\b\4\3\2T\13\3\2\2\2UV\7\61\2"+
		"\2VW\7,\2\2W[\3\2\2\2XZ\13\2\2\2YX\3\2\2\2Z]\3\2\2\2[\\\3\2\2\2[Y\3\2"+
		"\2\2\\^\3\2\2\2][\3\2\2\2^_\7,\2\2_`\7\61\2\2`a\3\2\2\2ab\b\5\3\2b\r\3"+
		"\2\2\2cd\7\61\2\2de\7\61\2\2ei\3\2\2\2fh\n\3\2\2gf\3\2\2\2hk\3\2\2\2i"+
		"g\3\2\2\2ij\3\2\2\2jl\3\2\2\2ki\3\2\2\2lm\b\6\3\2m\17\3\2\2\2np\t\4\2"+
		"\2on\3\2\2\2pq\3\2\2\2qo\3\2\2\2qr\3\2\2\2rs\3\2\2\2st\b\7\3\2t\21\3\2"+
		"\2\2uw\t\5\2\2vu\3\2\2\2wx\3\2\2\2xv\3\2\2\2xy\3\2\2\2yz\3\2\2\2z{\b\b"+
		"\4\2{\23\3\2\2\2|}\7\61\2\2}~\7,\2\2~\u0082\3\2\2\2\177\u0081\13\2\2\2"+
		"\u0080\177\3\2\2\2\u0081\u0084\3\2\2\2\u0082\u0083\3\2\2\2\u0082\u0080"+
		"\3\2\2\2\u0083\u0085\3\2\2\2\u0084\u0082\3\2\2\2\u0085\u0086\7,\2\2\u0086"+
		"\u0087\7\61\2\2\u0087\u0088\3\2\2\2\u0088\u0089\b\t\4\2\u0089\25\3\2\2"+
		"\2\u008a\u008b\7\61\2\2\u008b\u008c\7\61\2\2\u008c\u0090\3\2\2\2\u008d"+
		"\u008f\n\3\2\2\u008e\u008d\3\2\2\2\u008f\u0092\3\2\2\2\u0090\u008e\3\2"+
		"\2\2\u0090\u0091\3\2\2\2\u0091\u0093\3\2\2\2\u0092\u0090\3\2\2\2\u0093"+
		"\u0094\b\n\4\2\u0094\27\3\2\2\2\u0095\u0097\7^\2\2\u0096\u0098\7\17\2"+
		"\2\u0097\u0096\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009a"+
		"\7\f\2\2\u009a\u009b\3\2\2\2\u009b\u009c\b\13\4\2\u009c\31\3\2\2\2\u009d"+
		"\u009f\7\17\2\2\u009e\u009d\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a0\3"+
		"\2\2\2\u00a0\u00a1\7\f\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a3\b\f\4\2\u00a3"+
		"\u00a4\b\f\5\2\u00a4\33\3\2\2\2\u00a5\u00a6\7f\2\2\u00a6\u00a7\7g\2\2"+
		"\u00a7\u00a8\7h\2\2\u00a8\u00a9\7k\2\2\u00a9\u00aa\7p\2\2\u00aa\u00ab"+
		"\7g\2\2\u00ab\u00ad\3\2\2\2\u00ac\u00ae\t\5\2\2\u00ad\u00ac\3\2\2\2\u00ae"+
		"\u00af\3\2\2\2\u00af\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b1\3\2"+
		"\2\2\u00b1\u00b2\b\r\6\2\u00b2\35\3\2\2\2\u00b3\u00b4\7k\2\2\u00b4\u00b5"+
		"\7h\2\2\u00b5\u00b6\7f\2\2\u00b6\u00b7\7g\2\2\u00b7\u00b8\7h\2\2\u00b8"+
		"\u00ba\3\2\2\2\u00b9\u00bb\t\5\2\2\u00ba\u00b9\3\2\2\2\u00bb\u00bc\3\2"+
		"\2\2\u00bc\u00ba\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00be\3\2\2\2\u00be"+
		"\u00c2\5F\"\2\u00bf\u00c1\t\5\2\2\u00c0\u00bf\3\2\2\2\u00c1\u00c4\3\2"+
		"\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4"+
		"\u00c2\3\2\2\2\u00c5\u00c7\t\3\2\2\u00c6\u00c5\3\2\2\2\u00c6\u00c7\3\2"+
		"\2\2\u00c7\u00c8\3\2\2\2\u00c8\u00c9\b\16\5\2\u00c9\37\3\2\2\2\u00ca\u00cb"+
		"\7k\2\2\u00cb\u00cc\7h\2\2\u00cc\u00cd\7p\2\2\u00cd\u00ce\7f\2\2\u00ce"+
		"\u00cf\7g\2\2\u00cf\u00d0\7h\2\2\u00d0\u00d2\3\2\2\2\u00d1\u00d3\t\5\2"+
		"\2\u00d2\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d2\3\2\2\2\u00d4\u00d5"+
		"\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6\u00da\5F\"\2\u00d7\u00d9\t\5\2\2\u00d8"+
		"\u00d7\3\2\2\2\u00d9\u00dc\3\2\2\2\u00da\u00d8\3\2\2\2\u00da\u00db\3\2"+
		"\2\2\u00db\u00de\3\2\2\2\u00dc\u00da\3\2\2\2\u00dd\u00df\t\3\2\2\u00de"+
		"\u00dd\3\2\2\2\u00de\u00df\3\2\2\2\u00df\u00e0\3\2\2\2\u00e0\u00e1\b\17"+
		"\5\2\u00e1!\3\2\2\2\u00e2\u00e3\7g\2\2\u00e3\u00e4\7p\2\2\u00e4\u00e5"+
		"\7f\2\2\u00e5\u00e6\7k\2\2\u00e6\u00e7\7h\2\2\u00e7\u00eb\3\2\2\2\u00e8"+
		"\u00ea\t\5\2\2\u00e9\u00e8\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2"+
		"\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee"+
		"\u00f0\t\3\2\2\u00ef\u00ee\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f1\3\2"+
		"\2\2\u00f1\u00f2\b\20\5\2\u00f2#\3\2\2\2\u00f3\u00f4\7v\2\2\u00f4\u00f5"+
		"\7k\2\2\u00f5\u00f6\7o\2\2\u00f6\u00f7\7g\2\2\u00f7\u00f8\7u\2\2\u00f8"+
		"\u00f9\7e\2\2\u00f9\u00fa\7c\2\2\u00fa\u00fb\7n\2\2\u00fb\u00fc\7g\2\2"+
		"\u00fc\u00fe\3\2\2\2\u00fd\u00ff\n\3\2\2\u00fe\u00fd\3\2\2\2\u00ff\u0100"+
		"\3\2\2\2\u0100\u00fe\3\2\2\2\u0100\u0101\3\2\2\2\u0101\u0103\3\2\2\2\u0102"+
		"\u0104\t\3\2\2\u0103\u0102\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0105\3\2"+
		"\2\2\u0105\u0106\b\21\5\2\u0106%\3\2\2\2\u0107\u0108\7w\2\2\u0108\u0109"+
		"\7p\2\2\u0109\u010a\7f\2\2\u010a\u010b\7g\2\2\u010b\u010c\7h\2\2\u010c"+
		"\u010d\7k\2\2\u010d\u010e\7p\2\2\u010e\u010f\7g\2\2\u010f\u0110\7c\2\2"+
		"\u0110\u0111\7n\2\2\u0111\u0112\7n\2\2\u0112\u0116\3\2\2\2\u0113\u0115"+
		"\t\5\2\2\u0114\u0113\3\2\2\2\u0115\u0118\3\2\2\2\u0116\u0114\3\2\2\2\u0116"+
		"\u0117\3\2\2\2\u0117\u011a\3\2\2\2\u0118\u0116\3\2\2\2\u0119\u011b\t\3"+
		"\2\2\u011a\u0119\3\2\2\2\u011a\u011b\3\2\2\2\u011b\u011c\3\2\2\2\u011c"+
		"\u011d\b\22\5\2\u011d\'\3\2\2\2\u011e\u011f\7w\2\2\u011f\u0120\7p\2\2"+
		"\u0120\u0121\7f\2\2\u0121\u0122\7g\2\2\u0122\u0123\7h\2\2\u0123\u0125"+
		"\3\2\2\2\u0124\u0126\t\5\2\2\u0125\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127"+
		"\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128\u0129\3\2\2\2\u0129\u012d\5F"+
		"\"\2\u012a\u012c\t\5\2\2\u012b\u012a\3\2\2\2\u012c\u012f\3\2\2\2\u012d"+
		"\u012b\3\2\2\2\u012d\u012e\3\2\2\2\u012e\u0131\3\2\2\2\u012f\u012d\3\2"+
		"\2\2\u0130\u0132\t\3\2\2\u0131\u0130\3\2\2\2\u0131\u0132\3\2\2\2\u0132"+
		"\u0133\3\2\2\2\u0133\u0134\b\23\5\2\u0134)\3\2\2\2\u0135\u0136\5F\"\2"+
		"\u0136\u0137\3\2\2\2\u0137\u0138\b\24\5\2\u0138+\3\2\2\2\u0139\u013b\t"+
		"\5\2\2\u013a\u0139\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013a\3\2\2\2\u013c"+
		"\u013d\3\2\2\2\u013d\u013e\3\2\2\2\u013e\u013f\b\25\4\2\u013f-\3\2\2\2"+
		"\u0140\u0142\7^\2\2\u0141\u0143\7\17\2\2\u0142\u0141\3\2\2\2\u0142\u0143"+
		"\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145\7\f\2\2\u0145\u0146\3\2\2\2\u0146"+
		"\u0147\b\26\4\2\u0147/\3\2\2\2\u0148\u014a\7\17\2\2\u0149\u0148\3\2\2"+
		"\2\u0149\u014a\3\2\2\2\u014a\u014b\3\2\2\2\u014b\u014c\7\f\2\2\u014c\u014d"+
		"\3\2\2\2\u014d\u014e\b\27\4\2\u014e\61\3\2\2\2\u014f\u0150\5F\"\2\u0150"+
		"\u0151\3\2\2\2\u0151\u0152\b\30\7\2\u0152\63\3\2\2\2\u0153\u0155\7^\2"+
		"\2\u0154\u0156\7\17\2\2\u0155\u0154\3\2\2\2\u0155\u0156\3\2\2\2\u0156"+
		"\u0157\3\2\2\2\u0157\u0158\7\f\2\2\u0158\u0159\3\2\2\2\u0159\u015a\b\31"+
		"\4\2\u015a\65\3\2\2\2\u015b\u015c\7^\2\2\u015c\u015d\3\2\2\2\u015d\u015e"+
		"\b\32\b\2\u015e\67\3\2\2\2\u015f\u0161\7\17\2\2\u0160\u015f\3\2\2\2\u0160"+
		"\u0161\3\2\2\2\u0161\u0162\3\2\2\2\u0162\u0163\7\f\2\2\u0163\u0164\3\2"+
		"\2\2\u0164\u0165\b\33\4\2\u0165\u0166\b\33\5\2\u01669\3\2\2\2\u0167\u0168"+
		"\7\61\2\2\u0168\u0169\7,\2\2\u0169\u016d\3\2\2\2\u016a\u016c\13\2\2\2"+
		"\u016b\u016a\3\2\2\2\u016c\u016f\3\2\2\2\u016d\u016e\3\2\2\2\u016d\u016b"+
		"\3\2\2\2\u016e\u0170\3\2\2\2\u016f\u016d\3\2\2\2\u0170\u0171\7,\2\2\u0171"+
		"\u0172\7\61\2\2\u0172\u0173\3\2\2\2\u0173\u0174\b\34\b\2\u0174;\3\2\2"+
		"\2\u0175\u0176\7\61\2\2\u0176\u0177\7\61\2\2\u0177\u017b\3\2\2\2\u0178"+
		"\u017a\n\3\2\2\u0179\u0178\3\2\2\2\u017a\u017d\3\2\2\2\u017b\u0179\3\2"+
		"\2\2\u017b\u017c\3\2\2\2\u017c\u017e\3\2\2\2\u017d\u017b\3\2\2\2\u017e"+
		"\u017f\b\35\b\2\u017f=\3\2\2\2\u0180\u0181\7\61\2\2\u0181\u0182\3\2\2"+
		"\2\u0182\u0183\b\36\b\2\u0183?\3\2\2\2\u0184\u0186\t\5\2\2\u0185\u0184"+
		"\3\2\2\2\u0186\u0187\3\2\2\2\u0187\u0185\3\2\2\2\u0187\u0188\3\2\2\2\u0188"+
		"\u0189\3\2\2\2\u0189\u018a\b\37\b\2\u018aA\3\2\2\2\u018b\u018d\n\6\2\2"+
		"\u018c\u018b\3\2\2\2\u018d\u018e\3\2\2\2\u018e\u018c\3\2\2\2\u018e\u018f"+
		"\3\2\2\2\u018fC\3\2\2\2\u0190\u0198\7$\2\2\u0191\u0192\7^\2\2\u0192\u0197"+
		"\7$\2\2\u0193\u0194\7^\2\2\u0194\u0197\7^\2\2\u0195\u0197\13\2\2\2\u0196"+
		"\u0191\3\2\2\2\u0196\u0193\3\2\2\2\u0196\u0195\3\2\2\2\u0197\u019a\3\2"+
		"\2\2\u0198\u0199\3\2\2\2\u0198\u0196\3\2\2\2\u0199\u019b\3\2\2\2\u019a"+
		"\u0198\3\2\2\2\u019b\u019c\7$\2\2\u019cE\3\2\2\2\u019d\u01a1\t\7\2\2\u019e"+
		"\u01a0\t\b\2\2\u019f\u019e\3\2\2\2\u01a0\u01a3\3\2\2\2\u01a1\u019f\3\2"+
		"\2\2\u01a1\u01a2\3\2\2\2\u01a2G\3\2\2\2\u01a3\u01a1\3\2\2\2+\2\3\4\5O"+
		"[iqx\u0082\u0090\u0097\u009e\u00af\u00bc\u00c2\u00c6\u00d4\u00da\u00de"+
		"\u00eb\u00ef\u0100\u0103\u0116\u011a\u0127\u012d\u0131\u013c\u0142\u0149"+
		"\u0155\u0160\u016d\u017b\u0187\u018e\u0196\u0198\u01a1\t\4\3\2\t\4\2\2"+
		"\3\2\4\2\2\4\4\2\4\5\2\t\30\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}