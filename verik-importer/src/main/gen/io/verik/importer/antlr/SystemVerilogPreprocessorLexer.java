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
		BACKTICK=1, CODE=2, BLOCK_COMMENT=3, LINE_COMMENT=4, WS=5, DIRECTIVE_WS=6, 
		TIMESCALE=7;
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
			"WS", "DIRECTIVE_WS", "TIMESCALE", "STRING"
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
			null, "BACKTICK", "CODE", "BLOCK_COMMENT", "LINE_COMMENT", "WS", "DIRECTIVE_WS", 
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\th\b\1\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2"+
		"\3\2\3\2\3\2\3\3\6\3\34\n\3\r\3\16\3\35\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3"+
		"\5\7\5(\n\5\f\5\16\5+\13\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\7\6\66"+
		"\n\6\f\6\16\69\13\6\3\6\3\6\3\7\6\7>\n\7\r\7\16\7?\3\7\3\7\3\b\6\bE\n"+
		"\b\r\b\16\bF\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\6\tV"+
		"\n\t\r\t\16\tW\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\7\nb\n\n\f\n\16\ne\13\n"+
		"\3\n\3\n\4)c\2\13\4\3\6\4\b\2\n\5\f\6\16\7\20\b\22\t\24\2\4\2\3\6\7\2"+
		"\f\f\17\17$$\61\61bb\4\2\f\f\17\17\5\2\13\f\17\17\"\"\4\2\13\13\"\"\2"+
		"n\2\4\3\2\2\2\2\6\3\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2\2\f\3\2\2\2\2\16\3\2"+
		"\2\2\3\20\3\2\2\2\3\22\3\2\2\2\4\26\3\2\2\2\6\33\3\2\2\2\b\37\3\2\2\2"+
		"\n#\3\2\2\2\f\61\3\2\2\2\16=\3\2\2\2\20D\3\2\2\2\22J\3\2\2\2\24[\3\2\2"+
		"\2\26\27\7b\2\2\27\30\3\2\2\2\30\31\b\2\2\2\31\5\3\2\2\2\32\34\n\2\2\2"+
		"\33\32\3\2\2\2\34\35\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36\7\3\2\2\2"+
		"\37 \5\24\n\2 !\3\2\2\2!\"\b\4\3\2\"\t\3\2\2\2#$\7\61\2\2$%\7,\2\2%)\3"+
		"\2\2\2&(\13\2\2\2\'&\3\2\2\2(+\3\2\2\2)*\3\2\2\2)\'\3\2\2\2*,\3\2\2\2"+
		"+)\3\2\2\2,-\7,\2\2-.\7\61\2\2./\3\2\2\2/\60\b\5\4\2\60\13\3\2\2\2\61"+
		"\62\7\61\2\2\62\63\7\61\2\2\63\67\3\2\2\2\64\66\n\3\2\2\65\64\3\2\2\2"+
		"\669\3\2\2\2\67\65\3\2\2\2\678\3\2\2\28:\3\2\2\29\67\3\2\2\2:;\b\6\4\2"+
		";\r\3\2\2\2<>\t\4\2\2=<\3\2\2\2>?\3\2\2\2?=\3\2\2\2?@\3\2\2\2@A\3\2\2"+
		"\2AB\b\7\4\2B\17\3\2\2\2CE\t\5\2\2DC\3\2\2\2EF\3\2\2\2FD\3\2\2\2FG\3\2"+
		"\2\2GH\3\2\2\2HI\b\b\4\2I\21\3\2\2\2JK\7v\2\2KL\7k\2\2LM\7o\2\2MN\7g\2"+
		"\2NO\7u\2\2OP\7e\2\2PQ\7c\2\2QR\7n\2\2RS\7g\2\2SU\3\2\2\2TV\n\3\2\2UT"+
		"\3\2\2\2VW\3\2\2\2WU\3\2\2\2WX\3\2\2\2XY\3\2\2\2YZ\b\t\5\2Z\23\3\2\2\2"+
		"[c\7$\2\2\\]\7^\2\2]b\7$\2\2^_\7^\2\2_b\7^\2\2`b\13\2\2\2a\\\3\2\2\2a"+
		"^\3\2\2\2a`\3\2\2\2be\3\2\2\2cd\3\2\2\2ca\3\2\2\2df\3\2\2\2ec\3\2\2\2"+
		"fg\7$\2\2g\25\3\2\2\2\f\2\3\35)\67?FWac\6\4\3\2\t\4\2\2\3\2\4\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}