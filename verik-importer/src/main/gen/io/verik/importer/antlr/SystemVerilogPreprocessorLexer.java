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
		BACKTICK=1, CODE=2, DIRECTIVE_WS=3, IFNDEF=4, IFDEF=5, ENDIF=6, TIMESCALE=7;
	public static final int
		DIRECTIVE_MODE=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "DIRECTIVE_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"BACKTICK", "CODE", "STRING_LITERAL", "BLOCK_COMMENT", "LINE_COMMENT", 
			"WS", "DIRECTIVE_WS", "IFNDEF", "IFDEF", "ENDIF", "TIMESCALE", "STRING", 
			"IDENTIFIER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'`'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "BACKTICK", "CODE", "DIRECTIVE_WS", "IFNDEF", "IFDEF", "ENDIF", 
			"TIMESCALE"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\t\u00ba\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\2\3\2\3\3\6\3$\n\3\r\3"+
		"\16\3%\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\7\5\60\n\5\f\5\16\5\63\13\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\7\6>\n\6\f\6\16\6A\13\6\3\6\3\6\3\7\6"+
		"\7F\n\7\r\7\16\7G\3\7\3\7\3\b\6\bM\n\b\r\b\16\bN\3\b\3\b\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\t\6\t[\n\t\r\t\16\t\\\3\t\3\t\7\ta\n\t\f\t\16\td\13\t"+
		"\3\t\5\tg\n\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\6\nr\n\n\r\n\16\ns\3"+
		"\n\3\n\7\nx\n\n\f\n\16\n{\13\n\3\n\5\n~\n\n\3\n\3\n\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\7\13\u0089\n\13\f\13\16\13\u008c\13\13\3\13\5\13\u008f"+
		"\n\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\6\f\u009e"+
		"\n\f\r\f\16\f\u009f\3\f\5\f\u00a3\n\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\7\r\u00ad\n\r\f\r\16\r\u00b0\13\r\3\r\3\r\3\16\3\16\7\16\u00b6\n\16\f"+
		"\16\16\16\u00b9\13\16\4\61\u00ae\2\17\4\3\6\4\b\2\n\2\f\2\16\2\20\5\22"+
		"\6\24\7\26\b\30\t\32\2\34\2\4\2\3\b\7\2\f\f\17\17$$\61\61bb\4\2\f\f\17"+
		"\17\5\2\13\f\17\17\"\"\4\2\13\13\"\"\5\2C\\aac|\7\2&&\62;C\\aac|\2\u00c9"+
		"\2\4\3\2\2\2\2\6\3\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2\2\f\3\2\2\2\2\16\3\2"+
		"\2\2\3\20\3\2\2\2\3\22\3\2\2\2\3\24\3\2\2\2\3\26\3\2\2\2\3\30\3\2\2\2"+
		"\4\36\3\2\2\2\6#\3\2\2\2\b\'\3\2\2\2\n+\3\2\2\2\f9\3\2\2\2\16E\3\2\2\2"+
		"\20L\3\2\2\2\22R\3\2\2\2\24j\3\2\2\2\26\u0081\3\2\2\2\30\u0092\3\2\2\2"+
		"\32\u00a6\3\2\2\2\34\u00b3\3\2\2\2\36\37\7b\2\2\37 \3\2\2\2 !\b\2\2\2"+
		"!\5\3\2\2\2\"$\n\2\2\2#\"\3\2\2\2$%\3\2\2\2%#\3\2\2\2%&\3\2\2\2&\7\3\2"+
		"\2\2\'(\5\32\r\2()\3\2\2\2)*\b\4\3\2*\t\3\2\2\2+,\7\61\2\2,-\7,\2\2-\61"+
		"\3\2\2\2.\60\13\2\2\2/.\3\2\2\2\60\63\3\2\2\2\61\62\3\2\2\2\61/\3\2\2"+
		"\2\62\64\3\2\2\2\63\61\3\2\2\2\64\65\7,\2\2\65\66\7\61\2\2\66\67\3\2\2"+
		"\2\678\b\5\3\28\13\3\2\2\29:\7\61\2\2:;\7\61\2\2;?\3\2\2\2<>\n\3\2\2="+
		"<\3\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@B\3\2\2\2A?\3\2\2\2BC\b\6\3\2"+
		"C\r\3\2\2\2DF\t\4\2\2ED\3\2\2\2FG\3\2\2\2GE\3\2\2\2GH\3\2\2\2HI\3\2\2"+
		"\2IJ\b\7\3\2J\17\3\2\2\2KM\t\5\2\2LK\3\2\2\2MN\3\2\2\2NL\3\2\2\2NO\3\2"+
		"\2\2OP\3\2\2\2PQ\b\b\4\2Q\21\3\2\2\2RS\7k\2\2ST\7h\2\2TU\7p\2\2UV\7f\2"+
		"\2VW\7g\2\2WX\7h\2\2XZ\3\2\2\2Y[\t\5\2\2ZY\3\2\2\2[\\\3\2\2\2\\Z\3\2\2"+
		"\2\\]\3\2\2\2]^\3\2\2\2^b\5\34\16\2_a\t\5\2\2`_\3\2\2\2ad\3\2\2\2b`\3"+
		"\2\2\2bc\3\2\2\2cf\3\2\2\2db\3\2\2\2eg\t\3\2\2fe\3\2\2\2fg\3\2\2\2gh\3"+
		"\2\2\2hi\b\t\5\2i\23\3\2\2\2jk\7k\2\2kl\7h\2\2lm\7f\2\2mn\7g\2\2no\7h"+
		"\2\2oq\3\2\2\2pr\t\5\2\2qp\3\2\2\2rs\3\2\2\2sq\3\2\2\2st\3\2\2\2tu\3\2"+
		"\2\2uy\5\34\16\2vx\t\5\2\2wv\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z}\3"+
		"\2\2\2{y\3\2\2\2|~\t\3\2\2}|\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080"+
		"\b\n\5\2\u0080\25\3\2\2\2\u0081\u0082\7g\2\2\u0082\u0083\7p\2\2\u0083"+
		"\u0084\7f\2\2\u0084\u0085\7k\2\2\u0085\u0086\7h\2\2\u0086\u008a\3\2\2"+
		"\2\u0087\u0089\t\5\2\2\u0088\u0087\3\2\2\2\u0089\u008c\3\2\2\2\u008a\u0088"+
		"\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2\2\2\u008d"+
		"\u008f\t\3\2\2\u008e\u008d\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0090\3\2"+
		"\2\2\u0090\u0091\b\13\5\2\u0091\27\3\2\2\2\u0092\u0093\7v\2\2\u0093\u0094"+
		"\7k\2\2\u0094\u0095\7o\2\2\u0095\u0096\7g\2\2\u0096\u0097\7u\2\2\u0097"+
		"\u0098\7e\2\2\u0098\u0099\7c\2\2\u0099\u009a\7n\2\2\u009a\u009b\7g\2\2"+
		"\u009b\u009d\3\2\2\2\u009c\u009e\n\3\2\2\u009d\u009c\3\2\2\2\u009e\u009f"+
		"\3\2\2\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a2\3\2\2\2\u00a1"+
		"\u00a3\t\3\2\2\u00a2\u00a1\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\3\2"+
		"\2\2\u00a4\u00a5\b\f\5\2\u00a5\31\3\2\2\2\u00a6\u00ae\7$\2\2\u00a7\u00a8"+
		"\7^\2\2\u00a8\u00ad\7$\2\2\u00a9\u00aa\7^\2\2\u00aa\u00ad\7^\2\2\u00ab"+
		"\u00ad\13\2\2\2\u00ac\u00a7\3\2\2\2\u00ac\u00a9\3\2\2\2\u00ac\u00ab\3"+
		"\2\2\2\u00ad\u00b0\3\2\2\2\u00ae\u00af\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af"+
		"\u00b1\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u00b2\7$\2\2\u00b2\33\3\2\2\2"+
		"\u00b3\u00b7\t\6\2\2\u00b4\u00b6\t\7\2\2\u00b5\u00b4\3\2\2\2\u00b6\u00b9"+
		"\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\35\3\2\2\2\u00b9"+
		"\u00b7\3\2\2\2\26\2\3%\61?GN\\bfsy}\u008a\u008e\u009f\u00a2\u00ac\u00ae"+
		"\u00b7\6\4\3\2\t\4\2\2\3\2\4\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}