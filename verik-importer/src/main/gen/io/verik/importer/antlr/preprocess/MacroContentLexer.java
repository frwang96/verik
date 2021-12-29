// Generated from /Users/francis/Documents/Work/Verik/git.nosync/verik/verik-importer/src/main/gen/io/verik/importer/antlr/preprocess/MacroContentLexer.g4 by ANTLR 4.9.2
package io.verik.importer.antlr.preprocess;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MacroContentLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CONCAT=1, ESCAPE_DQ=2, ESCAPE_SLASH_DQ=3, IDENTIFIER=4, TEXT=5, BACK_TICK=6;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"STRING_LITERAL", "CONCAT", "ESCAPE_DQ", "ESCAPE_SLASH_DQ", "BACK_TICK", 
			"IDENTIFIER", "TEXT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'``'", "'`\"'", "'`\\`\"'", null, null, "'`'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CONCAT", "ESCAPE_DQ", "ESCAPE_SLASH_DQ", "IDENTIFIER", "TEXT", 
			"BACK_TICK"
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


	public MacroContentLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MacroContentLexer.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\b;\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\7\2\30\n\2\f\2\16\2\33\13\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3"+
		"\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\7\3\7\7\7\62\n\7\f\7\16\7\65\13\7"+
		"\3\b\6\b8\n\b\r\b\16\b9\3\31\2\t\3\2\5\3\7\4\t\5\13\b\r\6\17\7\3\2\5\5"+
		"\2C\\aac|\7\2&&\62;C\\aac|\5\2$$C\\a|\2?\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
		"\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\3\21\3\2\2\2"+
		"\5 \3\2\2\2\7#\3\2\2\2\t&\3\2\2\2\13+\3\2\2\2\r/\3\2\2\2\17\67\3\2\2\2"+
		"\21\31\7$\2\2\22\23\7^\2\2\23\30\7$\2\2\24\25\7^\2\2\25\30\7^\2\2\26\30"+
		"\13\2\2\2\27\22\3\2\2\2\27\24\3\2\2\2\27\26\3\2\2\2\30\33\3\2\2\2\31\32"+
		"\3\2\2\2\31\27\3\2\2\2\32\34\3\2\2\2\33\31\3\2\2\2\34\35\7$\2\2\35\36"+
		"\3\2\2\2\36\37\b\2\2\2\37\4\3\2\2\2 !\7b\2\2!\"\7b\2\2\"\6\3\2\2\2#$\7"+
		"b\2\2$%\7$\2\2%\b\3\2\2\2&\'\7b\2\2\'(\7^\2\2()\7b\2\2)*\7$\2\2*\n\3\2"+
		"\2\2+,\7b\2\2,-\3\2\2\2-.\b\6\2\2.\f\3\2\2\2/\63\t\2\2\2\60\62\t\3\2\2"+
		"\61\60\3\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64\16\3\2\2\2"+
		"\65\63\3\2\2\2\668\n\4\2\2\67\66\3\2\2\289\3\2\2\29\67\3\2\2\29:\3\2\2"+
		"\2:\20\3\2\2\2\7\2\27\31\639\3\t\7\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}