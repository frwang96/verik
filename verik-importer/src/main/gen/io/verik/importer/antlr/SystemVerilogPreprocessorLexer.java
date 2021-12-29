// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/SystemVerilogPreprocessorLexer.g4 by ANTLR 4.9.2
package io.verik.importer.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SystemVerilogPreprocessorLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		BACKTICK=1, CODE=2, DIRECTIVE_WHITESPACE=3, DIRECTIVE_BLOCK_COMMENT=4, 
		DIRECTIVE_LINE_COMMENT=5, DIRECTIVE_LINE_CONTINUATION=6, DIRECTIVE_NEW_LINE=7, 
		DEFINE=8, IFNDEF=9, IFDEF=10, ENDIF=11, TIMESCALE=12, DEFINED_MACRO=13, 
		DEFINE_WHITESPACE=14, DEFINE_LINE_CONTINUATION=15, DEFINE_NEW_LINE=16, 
		DEFINE_MACRO=17, TEXT_LINE_CONTINUATION=18, TEXT_NEW_LINE=19, TEXT=20, 
		TEXT_LINE_BACK_SLASH=21, TEXT_SLASH=22;
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
			"DIRECTIVE_LINE_CONTINUATION", "DIRECTIVE_NEW_LINE", "DEFINE", "IFNDEF", 
			"IFDEF", "ENDIF", "TIMESCALE", "DEFINED_MACRO", "DEFINE_WHITESPACE", 
			"DEFINE_LINE_CONTINUATION", "DEFINE_NEW_LINE", "DEFINE_MACRO", "TEXT_LINE_CONTINUATION", 
			"TEXT_LINE_BACK_SLASH", "TEXT_NEW_LINE", "TEXT_BLOCK_COMMENT", "TEXT_LINE_COMMENT", 
			"TEXT_SLASH", "TEXT_WHITESPACE", "TEXT", "STRING", "IDENTIFIER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'`'", null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "'\\'", "'/'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BACKTICK", "CODE", "DIRECTIVE_WHITESPACE", "DIRECTIVE_BLOCK_COMMENT", 
			"DIRECTIVE_LINE_COMMENT", "DIRECTIVE_LINE_CONTINUATION", "DIRECTIVE_NEW_LINE", 
			"DEFINE", "IFNDEF", "IFDEF", "ENDIF", "TIMESCALE", "DEFINED_MACRO", "DEFINE_WHITESPACE", 
			"DEFINE_LINE_CONTINUATION", "DEFINE_NEW_LINE", "DEFINE_MACRO", "TEXT_LINE_CONTINUATION", 
			"TEXT_NEW_LINE", "TEXT", "TEXT_LINE_BACK_SLASH", "TEXT_SLASH"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\30\u0172\b\1\b\1"+
		"\b\1\b\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t"+
		"\t\4\n\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21"+
		"\t\21\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30"+
		"\t\30\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37"+
		"\t\37\4 \t \3\2\3\2\3\2\3\2\3\3\6\3J\n\3\r\3\16\3K\3\4\3\4\3\4\3\4\3\5"+
		"\3\5\3\5\3\5\7\5V\n\5\f\5\16\5Y\13\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3"+
		"\6\7\6d\n\6\f\6\16\6g\13\6\3\6\3\6\3\7\6\7l\n\7\r\7\16\7m\3\7\3\7\3\b"+
		"\6\bs\n\b\r\b\16\bt\3\b\3\b\3\t\3\t\3\t\3\t\7\t}\n\t\f\t\16\t\u0080\13"+
		"\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\7\n\u008b\n\n\f\n\16\n\u008e\13"+
		"\n\3\n\3\n\3\13\3\13\5\13\u0094\n\13\3\13\3\13\3\13\3\13\3\f\5\f\u009b"+
		"\n\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\6\r\u00aa\n\r"+
		"\r\r\16\r\u00ab\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\6\16\u00b8"+
		"\n\16\r\16\16\16\u00b9\3\16\3\16\7\16\u00be\n\16\f\16\16\16\u00c1\13\16"+
		"\3\16\5\16\u00c4\n\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\6\17"+
		"\u00cf\n\17\r\17\16\17\u00d0\3\17\3\17\7\17\u00d5\n\17\f\17\16\17\u00d8"+
		"\13\17\3\17\5\17\u00db\n\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3"+
		"\20\7\20\u00e6\n\20\f\20\16\20\u00e9\13\20\3\20\5\20\u00ec\n\20\3\20\3"+
		"\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\6\21\u00fb"+
		"\n\21\r\21\16\21\u00fc\3\21\5\21\u0100\n\21\3\21\3\21\3\22\3\22\3\22\3"+
		"\22\3\23\6\23\u0109\n\23\r\23\16\23\u010a\3\23\3\23\3\24\3\24\5\24\u0111"+
		"\n\24\3\24\3\24\3\24\3\24\3\25\5\25\u0118\n\25\3\25\3\25\3\25\3\25\3\26"+
		"\3\26\3\26\3\26\3\27\3\27\5\27\u0124\n\27\3\27\3\27\3\27\3\27\3\30\3\30"+
		"\3\30\3\30\3\31\5\31\u012f\n\31\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32"+
		"\3\32\7\32\u013a\n\32\f\32\16\32\u013d\13\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\33\3\33\3\33\3\33\7\33\u0148\n\33\f\33\16\33\u014b\13\33\3\33\3\33"+
		"\3\34\3\34\3\34\3\34\3\35\6\35\u0154\n\35\r\35\16\35\u0155\3\35\3\35\3"+
		"\36\6\36\u015b\n\36\r\36\16\36\u015c\3\37\3\37\3\37\3\37\3\37\3\37\7\37"+
		"\u0165\n\37\f\37\16\37\u0168\13\37\3\37\3\37\3 \3 \7 \u016e\n \f \16 "+
		"\u0171\13 \6W~\u013b\u0166\2!\6\3\b\4\n\2\f\2\16\2\20\2\22\5\24\6\26\7"+
		"\30\b\32\t\34\n\36\13 \f\"\r$\16&\17(\20*\21,\22.\23\60\24\62\27\64\25"+
		"\66\28\2:\30<\2>\26@\2B\2\6\2\3\4\5\t\7\2\f\f\17\17$$\61\61bb\4\2\f\f"+
		"\17\17\5\2\13\f\17\17\"\"\4\2\13\13\"\"\6\2\f\f\17\17\61\61^^\5\2C\\a"+
		"ac|\7\2&&\62;C\\aac|\2\u018d\2\6\3\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2\2\f\3"+
		"\2\2\2\2\16\3\2\2\2\2\20\3\2\2\2\3\22\3\2\2\2\3\24\3\2\2\2\3\26\3\2\2"+
		"\2\3\30\3\2\2\2\3\32\3\2\2\2\3\34\3\2\2\2\3\36\3\2\2\2\3 \3\2\2\2\3\""+
		"\3\2\2\2\3$\3\2\2\2\3&\3\2\2\2\4(\3\2\2\2\4*\3\2\2\2\4,\3\2\2\2\4.\3\2"+
		"\2\2\5\60\3\2\2\2\5\62\3\2\2\2\5\64\3\2\2\2\5\66\3\2\2\2\58\3\2\2\2\5"+
		":\3\2\2\2\5<\3\2\2\2\5>\3\2\2\2\6D\3\2\2\2\bI\3\2\2\2\nM\3\2\2\2\fQ\3"+
		"\2\2\2\16_\3\2\2\2\20k\3\2\2\2\22r\3\2\2\2\24x\3\2\2\2\26\u0086\3\2\2"+
		"\2\30\u0091\3\2\2\2\32\u009a\3\2\2\2\34\u00a1\3\2\2\2\36\u00af\3\2\2\2"+
		" \u00c7\3\2\2\2\"\u00de\3\2\2\2$\u00ef\3\2\2\2&\u0103\3\2\2\2(\u0108\3"+
		"\2\2\2*\u010e\3\2\2\2,\u0117\3\2\2\2.\u011d\3\2\2\2\60\u0121\3\2\2\2\62"+
		"\u0129\3\2\2\2\64\u012e\3\2\2\2\66\u0135\3\2\2\28\u0143\3\2\2\2:\u014e"+
		"\3\2\2\2<\u0153\3\2\2\2>\u015a\3\2\2\2@\u015e\3\2\2\2B\u016b\3\2\2\2D"+
		"E\7b\2\2EF\3\2\2\2FG\b\2\2\2G\7\3\2\2\2HJ\n\2\2\2IH\3\2\2\2JK\3\2\2\2"+
		"KI\3\2\2\2KL\3\2\2\2L\t\3\2\2\2MN\5@\37\2NO\3\2\2\2OP\b\4\3\2P\13\3\2"+
		"\2\2QR\7\61\2\2RS\7,\2\2SW\3\2\2\2TV\13\2\2\2UT\3\2\2\2VY\3\2\2\2WX\3"+
		"\2\2\2WU\3\2\2\2XZ\3\2\2\2YW\3\2\2\2Z[\7,\2\2[\\\7\61\2\2\\]\3\2\2\2]"+
		"^\b\5\3\2^\r\3\2\2\2_`\7\61\2\2`a\7\61\2\2ae\3\2\2\2bd\n\3\2\2cb\3\2\2"+
		"\2dg\3\2\2\2ec\3\2\2\2ef\3\2\2\2fh\3\2\2\2ge\3\2\2\2hi\b\6\3\2i\17\3\2"+
		"\2\2jl\t\4\2\2kj\3\2\2\2lm\3\2\2\2mk\3\2\2\2mn\3\2\2\2no\3\2\2\2op\b\7"+
		"\3\2p\21\3\2\2\2qs\t\5\2\2rq\3\2\2\2st\3\2\2\2tr\3\2\2\2tu\3\2\2\2uv\3"+
		"\2\2\2vw\b\b\4\2w\23\3\2\2\2xy\7\61\2\2yz\7,\2\2z~\3\2\2\2{}\13\2\2\2"+
		"|{\3\2\2\2}\u0080\3\2\2\2~\177\3\2\2\2~|\3\2\2\2\177\u0081\3\2\2\2\u0080"+
		"~\3\2\2\2\u0081\u0082\7,\2\2\u0082\u0083\7\61\2\2\u0083\u0084\3\2\2\2"+
		"\u0084\u0085\b\t\4\2\u0085\25\3\2\2\2\u0086\u0087\7\61\2\2\u0087\u0088"+
		"\7\61\2\2\u0088\u008c\3\2\2\2\u0089\u008b\n\3\2\2\u008a\u0089\3\2\2\2"+
		"\u008b\u008e\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008f"+
		"\3\2\2\2\u008e\u008c\3\2\2\2\u008f\u0090\b\n\4\2\u0090\27\3\2\2\2\u0091"+
		"\u0093\7^\2\2\u0092\u0094\7\17\2\2\u0093\u0092\3\2\2\2\u0093\u0094\3\2"+
		"\2\2\u0094\u0095\3\2\2\2\u0095\u0096\7\f\2\2\u0096\u0097\3\2\2\2\u0097"+
		"\u0098\b\13\4\2\u0098\31\3\2\2\2\u0099\u009b\7\17\2\2\u009a\u0099\3\2"+
		"\2\2\u009a\u009b\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009d\7\f\2\2\u009d"+
		"\u009e\3\2\2\2\u009e\u009f\b\f\4\2\u009f\u00a0\b\f\5\2\u00a0\33\3\2\2"+
		"\2\u00a1\u00a2\7f\2\2\u00a2\u00a3\7g\2\2\u00a3\u00a4\7h\2\2\u00a4\u00a5"+
		"\7k\2\2\u00a5\u00a6\7p\2\2\u00a6\u00a7\7g\2\2\u00a7\u00a9\3\2\2\2\u00a8"+
		"\u00aa\t\5\2\2\u00a9\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00a9\3\2"+
		"\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00ae\b\r\6\2\u00ae"+
		"\35\3\2\2\2\u00af\u00b0\7k\2\2\u00b0\u00b1\7h\2\2\u00b1\u00b2\7p\2\2\u00b2"+
		"\u00b3\7f\2\2\u00b3\u00b4\7g\2\2\u00b4\u00b5\7h\2\2\u00b5\u00b7\3\2\2"+
		"\2\u00b6\u00b8\t\5\2\2\u00b7\u00b6\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00b7"+
		"\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bf\5B \2\u00bc"+
		"\u00be\t\5\2\2\u00bd\u00bc\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf\u00bd\3\2"+
		"\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c3\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c2"+
		"\u00c4\t\3\2\2\u00c3\u00c2\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c5\3\2"+
		"\2\2\u00c5\u00c6\b\16\5\2\u00c6\37\3\2\2\2\u00c7\u00c8\7k\2\2\u00c8\u00c9"+
		"\7h\2\2\u00c9\u00ca\7f\2\2\u00ca\u00cb\7g\2\2\u00cb\u00cc\7h\2\2\u00cc"+
		"\u00ce\3\2\2\2\u00cd\u00cf\t\5\2\2\u00ce\u00cd\3\2\2\2\u00cf\u00d0\3\2"+
		"\2\2\u00d0\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2"+
		"\u00d6\5B \2\u00d3\u00d5\t\5\2\2\u00d4\u00d3\3\2\2\2\u00d5\u00d8\3\2\2"+
		"\2\u00d6\u00d4\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00da\3\2\2\2\u00d8\u00d6"+
		"\3\2\2\2\u00d9\u00db\t\3\2\2\u00da\u00d9\3\2\2\2\u00da\u00db\3\2\2\2\u00db"+
		"\u00dc\3\2\2\2\u00dc\u00dd\b\17\5\2\u00dd!\3\2\2\2\u00de\u00df\7g\2\2"+
		"\u00df\u00e0\7p\2\2\u00e0\u00e1\7f\2\2\u00e1\u00e2\7k\2\2\u00e2\u00e3"+
		"\7h\2\2\u00e3\u00e7\3\2\2\2\u00e4\u00e6\t\5\2\2\u00e5\u00e4\3\2\2\2\u00e6"+
		"\u00e9\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00eb\3\2"+
		"\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00ec\t\3\2\2\u00eb\u00ea\3\2\2\2\u00eb"+
		"\u00ec\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00ee\b\20\5\2\u00ee#\3\2\2\2"+
		"\u00ef\u00f0\7v\2\2\u00f0\u00f1\7k\2\2\u00f1\u00f2\7o\2\2\u00f2\u00f3"+
		"\7g\2\2\u00f3\u00f4\7u\2\2\u00f4\u00f5\7e\2\2\u00f5\u00f6\7c\2\2\u00f6"+
		"\u00f7\7n\2\2\u00f7\u00f8\7g\2\2\u00f8\u00fa\3\2\2\2\u00f9\u00fb\n\3\2"+
		"\2\u00fa\u00f9\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc\u00fa\3\2\2\2\u00fc\u00fd"+
		"\3\2\2\2\u00fd\u00ff\3\2\2\2\u00fe\u0100\t\3\2\2\u00ff\u00fe\3\2\2\2\u00ff"+
		"\u0100\3\2\2\2\u0100\u0101\3\2\2\2\u0101\u0102\b\21\5\2\u0102%\3\2\2\2"+
		"\u0103\u0104\5B \2\u0104\u0105\3\2\2\2\u0105\u0106\b\22\5\2\u0106\'\3"+
		"\2\2\2\u0107\u0109\t\5\2\2\u0108\u0107\3\2\2\2\u0109\u010a\3\2\2\2\u010a"+
		"\u0108\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u010d\b\23"+
		"\4\2\u010d)\3\2\2\2\u010e\u0110\7^\2\2\u010f\u0111\7\17\2\2\u0110\u010f"+
		"\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u0113\7\f\2\2\u0113"+
		"\u0114\3\2\2\2\u0114\u0115\b\24\4\2\u0115+\3\2\2\2\u0116\u0118\7\17\2"+
		"\2\u0117\u0116\3\2\2\2\u0117\u0118\3\2\2\2\u0118\u0119\3\2\2\2\u0119\u011a"+
		"\7\f\2\2\u011a\u011b\3\2\2\2\u011b\u011c\b\25\4\2\u011c-\3\2\2\2\u011d"+
		"\u011e\5B \2\u011e\u011f\3\2\2\2\u011f\u0120\b\26\7\2\u0120/\3\2\2\2\u0121"+
		"\u0123\7^\2\2\u0122\u0124\7\17\2\2\u0123\u0122\3\2\2\2\u0123\u0124\3\2"+
		"\2\2\u0124\u0125\3\2\2\2\u0125\u0126\7\f\2\2\u0126\u0127\3\2\2\2\u0127"+
		"\u0128\b\27\4\2\u0128\61\3\2\2\2\u0129\u012a\7^\2\2\u012a\u012b\3\2\2"+
		"\2\u012b\u012c\b\30\b\2\u012c\63\3\2\2\2\u012d\u012f\7\17\2\2\u012e\u012d"+
		"\3\2\2\2\u012e\u012f\3\2\2\2\u012f\u0130\3\2\2\2\u0130\u0131\7\f\2\2\u0131"+
		"\u0132\3\2\2\2\u0132\u0133\b\31\4\2\u0133\u0134\b\31\5\2\u0134\65\3\2"+
		"\2\2\u0135\u0136\7\61\2\2\u0136\u0137\7,\2\2\u0137\u013b\3\2\2\2\u0138"+
		"\u013a\13\2\2\2\u0139\u0138\3\2\2\2\u013a\u013d\3\2\2\2\u013b\u013c\3"+
		"\2\2\2\u013b\u0139\3\2\2\2\u013c\u013e\3\2\2\2\u013d\u013b\3\2\2\2\u013e"+
		"\u013f\7,\2\2\u013f\u0140\7\61\2\2\u0140\u0141\3\2\2\2\u0141\u0142\b\32"+
		"\b\2\u0142\67\3\2\2\2\u0143\u0144\7\61\2\2\u0144\u0145\7\61\2\2\u0145"+
		"\u0149\3\2\2\2\u0146\u0148\n\3\2\2\u0147\u0146\3\2\2\2\u0148\u014b\3\2"+
		"\2\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014c\3\2\2\2\u014b"+
		"\u0149\3\2\2\2\u014c\u014d\b\33\b\2\u014d9\3\2\2\2\u014e\u014f\7\61\2"+
		"\2\u014f\u0150\3\2\2\2\u0150\u0151\b\34\b\2\u0151;\3\2\2\2\u0152\u0154"+
		"\t\5\2\2\u0153\u0152\3\2\2\2\u0154\u0155\3\2\2\2\u0155\u0153\3\2\2\2\u0155"+
		"\u0156\3\2\2\2\u0156\u0157\3\2\2\2\u0157\u0158\b\35\b\2\u0158=\3\2\2\2"+
		"\u0159\u015b\n\6\2\2\u015a\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c\u015a"+
		"\3\2\2\2\u015c\u015d\3\2\2\2\u015d?\3\2\2\2\u015e\u0166\7$\2\2\u015f\u0160"+
		"\7^\2\2\u0160\u0165\7$\2\2\u0161\u0162\7^\2\2\u0162\u0165\7^\2\2\u0163"+
		"\u0165\13\2\2\2\u0164\u015f\3\2\2\2\u0164\u0161\3\2\2\2\u0164\u0163\3"+
		"\2\2\2\u0165\u0168\3\2\2\2\u0166\u0167\3\2\2\2\u0166\u0164\3\2\2\2\u0167"+
		"\u0169\3\2\2\2\u0168\u0166\3\2\2\2\u0169\u016a\7$\2\2\u016aA\3\2\2\2\u016b"+
		"\u016f\t\7\2\2\u016c\u016e\t\b\2\2\u016d\u016c\3\2\2\2\u016e\u0171\3\2"+
		"\2\2\u016f\u016d\3\2\2\2\u016f\u0170\3\2\2\2\u0170C\3\2\2\2\u0171\u016f"+
		"\3\2\2\2&\2\3\4\5KWemt~\u008c\u0093\u009a\u00ab\u00b9\u00bf\u00c3\u00d0"+
		"\u00d6\u00da\u00e7\u00eb\u00fc\u00ff\u010a\u0110\u0117\u0123\u012e\u013b"+
		"\u0149\u0155\u015c\u0164\u0166\u016f\t\4\3\2\t\4\2\2\3\2\4\2\2\4\4\2\4"+
		"\5\2\t\26\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}