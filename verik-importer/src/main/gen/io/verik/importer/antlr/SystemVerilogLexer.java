// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/SystemVerilogLexer.g4 by ANTLR 4.9.2
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
public class SystemVerilogLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		EQ=1, COMMA=2, COLON=3, SEMICOLON=4, UNDERSCORE=5, LBRACK=6, RBRACK=7, 
		LPAREN=8, RPAREN=9, LPAREN_STAR=10, RPAREN_STAR=11, ENDMODULE=12, INPUT=13, 
		LOGIC=14, MODULE=15, OUTPUT=16, SIGNED=17, UNSIGNED=18, WIRE=19, UNSIGNED_NUMBER=20, 
		STRING_LITERAL=21, SIMPLE_IDENTIFIER=22, WS=23;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"EQ", "COMMA", "COLON", "SEMICOLON", "UNDERSCORE", "LBRACK", "RBRACK", 
			"LPAREN", "RPAREN", "LPAREN_STAR", "RPAREN_STAR", "ENDMODULE", "INPUT", 
			"LOGIC", "MODULE", "OUTPUT", "SIGNED", "UNSIGNED", "WIRE", "UNSIGNED_NUMBER", 
			"NON_ZERO_DECIMAL_DIGIT", "DECIMAL_DIGIT", "STRING_LITERAL", "SIMPLE_IDENTIFIER", 
			"WS"
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


	public SystemVerilogLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SystemVerilogLexer.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\31\u00a9\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3"+
		"\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\7\25\u008a\n\25"+
		"\f\25\16\25\u008d\13\25\3\26\3\26\3\27\3\27\3\30\3\30\7\30\u0095\n\30"+
		"\f\30\16\30\u0098\13\30\3\30\3\30\3\31\3\31\7\31\u009e\n\31\f\31\16\31"+
		"\u00a1\13\31\3\32\6\32\u00a4\n\32\r\32\16\32\u00a5\3\32\3\32\2\2\33\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
		"\21!\22#\23%\24\'\25)\26+\2-\2/\27\61\30\63\31\3\2\b\3\2\63;\3\2\62;\5"+
		"\2\f\f\17\17$$\5\2C\\aac|\7\2&&\62;C\\aac|\5\2\13\f\17\17\"\"\2\u00ab"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3"+
		"\2\2\2\3\65\3\2\2\2\5\67\3\2\2\2\79\3\2\2\2\t;\3\2\2\2\13=\3\2\2\2\r?"+
		"\3\2\2\2\17A\3\2\2\2\21C\3\2\2\2\23E\3\2\2\2\25G\3\2\2\2\27J\3\2\2\2\31"+
		"M\3\2\2\2\33W\3\2\2\2\35]\3\2\2\2\37c\3\2\2\2!j\3\2\2\2#q\3\2\2\2%x\3"+
		"\2\2\2\'\u0081\3\2\2\2)\u0086\3\2\2\2+\u008e\3\2\2\2-\u0090\3\2\2\2/\u0092"+
		"\3\2\2\2\61\u009b\3\2\2\2\63\u00a3\3\2\2\2\65\66\7?\2\2\66\4\3\2\2\2\67"+
		"8\7.\2\28\6\3\2\2\29:\7<\2\2:\b\3\2\2\2;<\7=\2\2<\n\3\2\2\2=>\7a\2\2>"+
		"\f\3\2\2\2?@\7]\2\2@\16\3\2\2\2AB\7_\2\2B\20\3\2\2\2CD\7*\2\2D\22\3\2"+
		"\2\2EF\7+\2\2F\24\3\2\2\2GH\7*\2\2HI\7,\2\2I\26\3\2\2\2JK\7,\2\2KL\7+"+
		"\2\2L\30\3\2\2\2MN\7g\2\2NO\7p\2\2OP\7f\2\2PQ\7o\2\2QR\7q\2\2RS\7f\2\2"+
		"ST\7w\2\2TU\7n\2\2UV\7g\2\2V\32\3\2\2\2WX\7k\2\2XY\7p\2\2YZ\7r\2\2Z[\7"+
		"w\2\2[\\\7v\2\2\\\34\3\2\2\2]^\7n\2\2^_\7q\2\2_`\7i\2\2`a\7k\2\2ab\7e"+
		"\2\2b\36\3\2\2\2cd\7o\2\2de\7q\2\2ef\7f\2\2fg\7w\2\2gh\7n\2\2hi\7g\2\2"+
		"i \3\2\2\2jk\7q\2\2kl\7w\2\2lm\7v\2\2mn\7r\2\2no\7w\2\2op\7v\2\2p\"\3"+
		"\2\2\2qr\7u\2\2rs\7k\2\2st\7i\2\2tu\7p\2\2uv\7g\2\2vw\7f\2\2w$\3\2\2\2"+
		"xy\7w\2\2yz\7p\2\2z{\7u\2\2{|\7k\2\2|}\7i\2\2}~\7p\2\2~\177\7g\2\2\177"+
		"\u0080\7f\2\2\u0080&\3\2\2\2\u0081\u0082\7y\2\2\u0082\u0083\7k\2\2\u0083"+
		"\u0084\7t\2\2\u0084\u0085\7g\2\2\u0085(\3\2\2\2\u0086\u008b\5-\27\2\u0087"+
		"\u008a\5\13\6\2\u0088\u008a\5-\27\2\u0089\u0087\3\2\2\2\u0089\u0088\3"+
		"\2\2\2\u008a\u008d\3\2\2\2\u008b\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c"+
		"*\3\2\2\2\u008d\u008b\3\2\2\2\u008e\u008f\t\2\2\2\u008f,\3\2\2\2\u0090"+
		"\u0091\t\3\2\2\u0091.\3\2\2\2\u0092\u0096\7$\2\2\u0093\u0095\n\4\2\2\u0094"+
		"\u0093\3\2\2\2\u0095\u0098\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2"+
		"\2\2\u0097\u0099\3\2\2\2\u0098\u0096\3\2\2\2\u0099\u009a\7$\2\2\u009a"+
		"\60\3\2\2\2\u009b\u009f\t\5\2\2\u009c\u009e\t\6\2\2\u009d\u009c\3\2\2"+
		"\2\u009e\u00a1\3\2\2\2\u009f\u009d\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\62"+
		"\3\2\2\2\u00a1\u009f\3\2\2\2\u00a2\u00a4\t\7\2\2\u00a3\u00a2\3\2\2\2\u00a4"+
		"\u00a5\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a7\3\2"+
		"\2\2\u00a7\u00a8\b\32\2\2\u00a8\64\3\2\2\2\b\2\u0089\u008b\u0096\u009f"+
		"\u00a5\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}