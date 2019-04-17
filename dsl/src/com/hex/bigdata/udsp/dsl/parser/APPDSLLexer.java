// Generated from B:/workspaces/IntellijIdeaGithubWorkspaces/github/boracay/dsl/resource\APPDSL.g4 by ANTLR 4.7.2

    package com.hex.bigdata.udsp.dsl.parser;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class APPDSLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, AS=6, COUNT=7, SELECT=8, FROM=9, 
		WHERE=10, AND=11, LIMIT=12, OFFSET=13, DESCRIBE=14, SHOW=15, SERVICES=16, 
		LIKE=17, ID=18, TEXT_STRING=19, DECIMAL_LITERAL=20, WS=21;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "AS", "COUNT", "SELECT", "FROM", 
			"WHERE", "AND", "LIMIT", "OFFSET", "DESCRIBE", "SHOW", "SERVICES", "LIKE", 
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", 
			"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "HEX_DIGIT", 
			"DEC_DIGIT", "LETTER", "ID", "TEXT_STRING", "DECIMAL_LITERAL", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'*'", "','", "'='", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, "AS", "COUNT", "SELECT", "FROM", 
			"WHERE", "AND", "LIMIT", "OFFSET", "DESCRIBE", "SHOW", "SERVICES", "LIKE", 
			"ID", "TEXT_STRING", "DECIMAL_LITERAL", "WS"
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


	public APPDSLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "APPDSL.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\27\u0115\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\3\2"+
		"\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30"+
		"\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37"+
		"\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)"+
		"\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\7\60\u00f6\n\60\f\60\16"+
		"\60\u00f9\13\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\7\61\u0103\n\61"+
		"\f\61\16\61\u0106\13\61\3\61\3\61\3\62\6\62\u010b\n\62\r\62\16\62\u010c"+
		"\3\63\6\63\u0110\n\63\r\63\16\63\u0111\3\63\3\63\2\2\64\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\2\'\2)\2+\2-\2/\2\61\2\63\2\65\2\67\29\2;\2=\2?\2A\2C\2E\2G\2I\2K\2"+
		"M\2O\2Q\2S\2U\2W\2Y\2[\2]\2_\24a\25c\26e\27\3\2\"\4\2CCcc\4\2DDdd\4\2"+
		"EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4"+
		"\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2VVv"+
		"v\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\4\2\62;CH\3\2\62;"+
		"\4\2C\\c|\6\2\62;C\\aac|\3\2))\5\2\13\f\17\17\"\"\2\u00fe\2\3\3\2\2\2"+
		"\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2"+
		"\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2"+
		"\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2_\3\2\2"+
		"\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\3g\3\2\2\2\5i\3\2\2\2\7k\3\2\2\2\t"+
		"m\3\2\2\2\13o\3\2\2\2\rq\3\2\2\2\17t\3\2\2\2\21z\3\2\2\2\23\u0081\3\2"+
		"\2\2\25\u0086\3\2\2\2\27\u008c\3\2\2\2\31\u0090\3\2\2\2\33\u0096\3\2\2"+
		"\2\35\u009d\3\2\2\2\37\u00a6\3\2\2\2!\u00ab\3\2\2\2#\u00b4\3\2\2\2%\u00b9"+
		"\3\2\2\2\'\u00bb\3\2\2\2)\u00bd\3\2\2\2+\u00bf\3\2\2\2-\u00c1\3\2\2\2"+
		"/\u00c3\3\2\2\2\61\u00c5\3\2\2\2\63\u00c7\3\2\2\2\65\u00c9\3\2\2\2\67"+
		"\u00cb\3\2\2\29\u00cd\3\2\2\2;\u00cf\3\2\2\2=\u00d1\3\2\2\2?\u00d3\3\2"+
		"\2\2A\u00d5\3\2\2\2C\u00d7\3\2\2\2E\u00d9\3\2\2\2G\u00db\3\2\2\2I\u00dd"+
		"\3\2\2\2K\u00df\3\2\2\2M\u00e1\3\2\2\2O\u00e3\3\2\2\2Q\u00e5\3\2\2\2S"+
		"\u00e7\3\2\2\2U\u00e9\3\2\2\2W\u00eb\3\2\2\2Y\u00ed\3\2\2\2[\u00ef\3\2"+
		"\2\2]\u00f1\3\2\2\2_\u00f3\3\2\2\2a\u00fa\3\2\2\2c\u010a\3\2\2\2e\u010f"+
		"\3\2\2\2gh\7,\2\2h\4\3\2\2\2ij\7.\2\2j\6\3\2\2\2kl\7?\2\2l\b\3\2\2\2m"+
		"n\7*\2\2n\n\3\2\2\2op\7+\2\2p\f\3\2\2\2qr\5%\23\2rs\5I%\2s\16\3\2\2\2"+
		"tu\5)\25\2uv\5A!\2vw\5M\'\2wx\5? \2xy\5K&\2y\20\3\2\2\2z{\5I%\2{|\5-\27"+
		"\2|}\5;\36\2}~\5-\27\2~\177\5)\25\2\177\u0080\5K&\2\u0080\22\3\2\2\2\u0081"+
		"\u0082\5/\30\2\u0082\u0083\5G$\2\u0083\u0084\5A!\2\u0084\u0085\5=\37\2"+
		"\u0085\24\3\2\2\2\u0086\u0087\5Q)\2\u0087\u0088\5\63\32\2\u0088\u0089"+
		"\5-\27\2\u0089\u008a\5G$\2\u008a\u008b\5-\27\2\u008b\26\3\2\2\2\u008c"+
		"\u008d\5%\23\2\u008d\u008e\5? \2\u008e\u008f\5+\26\2\u008f\30\3\2\2\2"+
		"\u0090\u0091\5;\36\2\u0091\u0092\5\65\33\2\u0092\u0093\5=\37\2\u0093\u0094"+
		"\5\65\33\2\u0094\u0095\5K&\2\u0095\32\3\2\2\2\u0096\u0097\5A!\2\u0097"+
		"\u0098\5/\30\2\u0098\u0099\5/\30\2\u0099\u009a\5I%\2\u009a\u009b\5-\27"+
		"\2\u009b\u009c\5K&\2\u009c\34\3\2\2\2\u009d\u009e\5+\26\2\u009e\u009f"+
		"\5-\27\2\u009f\u00a0\5I%\2\u00a0\u00a1\5)\25\2\u00a1\u00a2\5G$\2\u00a2"+
		"\u00a3\5\65\33\2\u00a3\u00a4\5\'\24\2\u00a4\u00a5\5-\27\2\u00a5\36\3\2"+
		"\2\2\u00a6\u00a7\5I%\2\u00a7\u00a8\5\63\32\2\u00a8\u00a9\5A!\2\u00a9\u00aa"+
		"\5Q)\2\u00aa \3\2\2\2\u00ab\u00ac\5I%\2\u00ac\u00ad\5-\27\2\u00ad\u00ae"+
		"\5G$\2\u00ae\u00af\5O(\2\u00af\u00b0\5\65\33\2\u00b0\u00b1\5)\25\2\u00b1"+
		"\u00b2\5-\27\2\u00b2\u00b3\5I%\2\u00b3\"\3\2\2\2\u00b4\u00b5\5;\36\2\u00b5"+
		"\u00b6\5\65\33\2\u00b6\u00b7\59\35\2\u00b7\u00b8\5-\27\2\u00b8$\3\2\2"+
		"\2\u00b9\u00ba\t\2\2\2\u00ba&\3\2\2\2\u00bb\u00bc\t\3\2\2\u00bc(\3\2\2"+
		"\2\u00bd\u00be\t\4\2\2\u00be*\3\2\2\2\u00bf\u00c0\t\5\2\2\u00c0,\3\2\2"+
		"\2\u00c1\u00c2\t\6\2\2\u00c2.\3\2\2\2\u00c3\u00c4\t\7\2\2\u00c4\60\3\2"+
		"\2\2\u00c5\u00c6\t\b\2\2\u00c6\62\3\2\2\2\u00c7\u00c8\t\t\2\2\u00c8\64"+
		"\3\2\2\2\u00c9\u00ca\t\n\2\2\u00ca\66\3\2\2\2\u00cb\u00cc\t\13\2\2\u00cc"+
		"8\3\2\2\2\u00cd\u00ce\t\f\2\2\u00ce:\3\2\2\2\u00cf\u00d0\t\r\2\2\u00d0"+
		"<\3\2\2\2\u00d1\u00d2\t\16\2\2\u00d2>\3\2\2\2\u00d3\u00d4\t\17\2\2\u00d4"+
		"@\3\2\2\2\u00d5\u00d6\t\20\2\2\u00d6B\3\2\2\2\u00d7\u00d8\t\21\2\2\u00d8"+
		"D\3\2\2\2\u00d9\u00da\t\22\2\2\u00daF\3\2\2\2\u00db\u00dc\t\23\2\2\u00dc"+
		"H\3\2\2\2\u00dd\u00de\t\24\2\2\u00deJ\3\2\2\2\u00df\u00e0\t\25\2\2\u00e0"+
		"L\3\2\2\2\u00e1\u00e2\t\26\2\2\u00e2N\3\2\2\2\u00e3\u00e4\t\27\2\2\u00e4"+
		"P\3\2\2\2\u00e5\u00e6\t\30\2\2\u00e6R\3\2\2\2\u00e7\u00e8\t\31\2\2\u00e8"+
		"T\3\2\2\2\u00e9\u00ea\t\32\2\2\u00eaV\3\2\2\2\u00eb\u00ec\t\33\2\2\u00ec"+
		"X\3\2\2\2\u00ed\u00ee\t\34\2\2\u00eeZ\3\2\2\2\u00ef\u00f0\t\35\2\2\u00f0"+
		"\\\3\2\2\2\u00f1\u00f2\t\36\2\2\u00f2^\3\2\2\2\u00f3\u00f7\t\36\2\2\u00f4"+
		"\u00f6\t\37\2\2\u00f5\u00f4\3\2\2\2\u00f6\u00f9\3\2\2\2\u00f7\u00f5\3"+
		"\2\2\2\u00f7\u00f8\3\2\2\2\u00f8`\3\2\2\2\u00f9\u00f7\3\2\2\2\u00fa\u0104"+
		"\7)\2\2\u00fb\u00fc\7^\2\2\u00fc\u0103\7^\2\2\u00fd\u00fe\7)\2\2\u00fe"+
		"\u0103\7)\2\2\u00ff\u0100\7^\2\2\u0100\u0103\7)\2\2\u0101\u0103\n \2\2"+
		"\u0102\u00fb\3\2\2\2\u0102\u00fd\3\2\2\2\u0102\u00ff\3\2\2\2\u0102\u0101"+
		"\3\2\2\2\u0103\u0106\3\2\2\2\u0104\u0102\3\2\2\2\u0104\u0105\3\2\2\2\u0105"+
		"\u0107\3\2\2\2\u0106\u0104\3\2\2\2\u0107\u0108\7)\2\2\u0108b\3\2\2\2\u0109"+
		"\u010b\5[.\2\u010a\u0109\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u010a\3\2\2"+
		"\2\u010c\u010d\3\2\2\2\u010dd\3\2\2\2\u010e\u0110\t!\2\2\u010f\u010e\3"+
		"\2\2\2\u0110\u0111\3\2\2\2\u0111\u010f\3\2\2\2\u0111\u0112\3\2\2\2\u0112"+
		"\u0113\3\2\2\2\u0113\u0114\b\63\2\2\u0114f\3\2\2\2\b\2\u00f7\u0102\u0104"+
		"\u010c\u0111\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}