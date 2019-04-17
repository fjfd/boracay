// Generated from B:/workspaces/IntellijIdeaGithubWorkspaces/github/boracay/dsl/resource\APPDSL.g4 by ANTLR 4.7.2

    package com.hex.bigdata.udsp.dsl.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class APPDSLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, AS=6, COUNT=7, SELECT=8, FROM=9, 
		WHERE=10, AND=11, LIMIT=12, OFFSET=13, DESCRIBE=14, SHOW=15, SERVICES=16, 
		LIKE=17, ID=18, TEXT_STRING=19, DECIMAL_LITERAL=20, WS=21;
	public static final int
		RULE_statement = 0, RULE_describeServiceStatement = 1, RULE_showServicesStatement = 2, 
		RULE_selectStatement = 3, RULE_selectElements = 4, RULE_whereClause = 5, 
		RULE_logicExpressions = 6, RULE_logicExpression = 7, RULE_limitClause = 8, 
		RULE_logicalOperator = 9, RULE_comparisonOperator = 10, RULE_value = 11, 
		RULE_decimalLiteral = 12, RULE_textLiteral = 13, RULE_selectElement = 14, 
		RULE_aggregateWindowedFunction = 15, RULE_functionArg = 16, RULE_fullColumnName = 17, 
		RULE_functionCall = 18, RULE_serviceName = 19, RULE_columnName = 20, RULE_uid = 21;
	private static String[] makeRuleNames() {
		return new String[] {
			"statement", "describeServiceStatement", "showServicesStatement", "selectStatement", 
			"selectElements", "whereClause", "logicExpressions", "logicExpression", 
			"limitClause", "logicalOperator", "comparisonOperator", "value", "decimalLiteral", 
			"textLiteral", "selectElement", "aggregateWindowedFunction", "functionArg", 
			"fullColumnName", "functionCall", "serviceName", "columnName", "uid"
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

	@Override
	public String getGrammarFileName() { return "APPDSL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public APPDSLParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class StatementContext extends ParserRuleContext {
		public SelectStatementContext selectStatement() {
			return getRuleContext(SelectStatementContext.class,0);
		}
		public DescribeServiceStatementContext describeServiceStatement() {
			return getRuleContext(DescribeServiceStatementContext.class,0);
		}
		public ShowServicesStatementContext showServicesStatement() {
			return getRuleContext(ShowServicesStatementContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_statement);
		try {
			setState(47);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT:
				enterOuterAlt(_localctx, 1);
				{
				setState(44);
				selectStatement();
				}
				break;
			case DESCRIBE:
				enterOuterAlt(_localctx, 2);
				{
				setState(45);
				describeServiceStatement();
				}
				break;
			case SHOW:
				enterOuterAlt(_localctx, 3);
				{
				setState(46);
				showServicesStatement();
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

	public static class DescribeServiceStatementContext extends ParserRuleContext {
		public TerminalNode DESCRIBE() { return getToken(APPDSLParser.DESCRIBE, 0); }
		public ServiceNameContext serviceName() {
			return getRuleContext(ServiceNameContext.class,0);
		}
		public DescribeServiceStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_describeServiceStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterDescribeServiceStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitDescribeServiceStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitDescribeServiceStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescribeServiceStatementContext describeServiceStatement() throws RecognitionException {
		DescribeServiceStatementContext _localctx = new DescribeServiceStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_describeServiceStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			match(DESCRIBE);
			setState(50);
			serviceName();
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

	public static class ShowServicesStatementContext extends ParserRuleContext {
		public TerminalNode SHOW() { return getToken(APPDSLParser.SHOW, 0); }
		public TerminalNode SERVICES() { return getToken(APPDSLParser.SERVICES, 0); }
		public TerminalNode LIKE() { return getToken(APPDSLParser.LIKE, 0); }
		public TextLiteralContext textLiteral() {
			return getRuleContext(TextLiteralContext.class,0);
		}
		public ShowServicesStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_showServicesStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterShowServicesStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitShowServicesStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitShowServicesStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ShowServicesStatementContext showServicesStatement() throws RecognitionException {
		ShowServicesStatementContext _localctx = new ShowServicesStatementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_showServicesStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(SHOW);
			setState(53);
			match(SERVICES);
			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIKE) {
				{
				setState(54);
				match(LIKE);
				setState(55);
				textLiteral();
				}
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

	public static class SelectStatementContext extends ParserRuleContext {
		public TerminalNode SELECT() { return getToken(APPDSLParser.SELECT, 0); }
		public SelectElementsContext selectElements() {
			return getRuleContext(SelectElementsContext.class,0);
		}
		public TerminalNode FROM() { return getToken(APPDSLParser.FROM, 0); }
		public ServiceNameContext serviceName() {
			return getRuleContext(ServiceNameContext.class,0);
		}
		public WhereClauseContext whereClause() {
			return getRuleContext(WhereClauseContext.class,0);
		}
		public LimitClauseContext limitClause() {
			return getRuleContext(LimitClauseContext.class,0);
		}
		public SelectStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterSelectStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitSelectStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitSelectStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectStatementContext selectStatement() throws RecognitionException {
		SelectStatementContext _localctx = new SelectStatementContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_selectStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(SELECT);
			setState(59);
			selectElements();
			setState(60);
			match(FROM);
			setState(61);
			serviceName();
			setState(63);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(62);
				whereClause();
				}
			}

			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIMIT) {
				{
				setState(65);
				limitClause();
				}
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

	public static class SelectElementsContext extends ParserRuleContext {
		public Token star;
		public SelectElementContext selectElement() {
			return getRuleContext(SelectElementContext.class,0);
		}
		public SelectElementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectElements; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterSelectElements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitSelectElements(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitSelectElements(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectElementsContext selectElements() throws RecognitionException {
		SelectElementsContext _localctx = new SelectElementsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_selectElements);
		try {
			setState(70);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(68);
				((SelectElementsContext)_localctx).star = match(T__0);
				}
				break;
			case COUNT:
				enterOuterAlt(_localctx, 2);
				{
				setState(69);
				selectElement();
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

	public static class WhereClauseContext extends ParserRuleContext {
		public TerminalNode WHERE() { return getToken(APPDSLParser.WHERE, 0); }
		public LogicExpressionsContext logicExpressions() {
			return getRuleContext(LogicExpressionsContext.class,0);
		}
		public WhereClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterWhereClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitWhereClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitWhereClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereClauseContext whereClause() throws RecognitionException {
		WhereClauseContext _localctx = new WhereClauseContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_whereClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(WHERE);
			setState(73);
			logicExpressions();
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

	public static class LogicExpressionsContext extends ParserRuleContext {
		public List<LogicExpressionContext> logicExpression() {
			return getRuleContexts(LogicExpressionContext.class);
		}
		public LogicExpressionContext logicExpression(int i) {
			return getRuleContext(LogicExpressionContext.class,i);
		}
		public List<LogicalOperatorContext> logicalOperator() {
			return getRuleContexts(LogicalOperatorContext.class);
		}
		public LogicalOperatorContext logicalOperator(int i) {
			return getRuleContext(LogicalOperatorContext.class,i);
		}
		public LogicExpressionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logicExpressions; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterLogicExpressions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitLogicExpressions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitLogicExpressions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicExpressionsContext logicExpressions() throws RecognitionException {
		LogicExpressionsContext _localctx = new LogicExpressionsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_logicExpressions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			logicExpression();
			setState(81);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(76);
				logicalOperator();
				setState(77);
				logicExpression();
				}
				}
				setState(83);
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

	public static class LogicExpressionContext extends ParserRuleContext {
		public FullColumnNameContext fullColumnName() {
			return getRuleContext(FullColumnNameContext.class,0);
		}
		public ComparisonOperatorContext comparisonOperator() {
			return getRuleContext(ComparisonOperatorContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public LogicExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logicExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterLogicExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitLogicExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitLogicExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicExpressionContext logicExpression() throws RecognitionException {
		LogicExpressionContext _localctx = new LogicExpressionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_logicExpression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			fullColumnName();
			setState(85);
			comparisonOperator();
			setState(86);
			value();
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

	public static class LimitClauseContext extends ParserRuleContext {
		public DecimalLiteralContext offset;
		public DecimalLiteralContext limit;
		public TerminalNode LIMIT() { return getToken(APPDSLParser.LIMIT, 0); }
		public TerminalNode OFFSET() { return getToken(APPDSLParser.OFFSET, 0); }
		public List<DecimalLiteralContext> decimalLiteral() {
			return getRuleContexts(DecimalLiteralContext.class);
		}
		public DecimalLiteralContext decimalLiteral(int i) {
			return getRuleContext(DecimalLiteralContext.class,i);
		}
		public LimitClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_limitClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterLimitClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitLimitClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitLimitClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LimitClauseContext limitClause() throws RecognitionException {
		LimitClauseContext _localctx = new LimitClauseContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_limitClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(LIMIT);
			setState(99);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				{
				setState(92);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
				case 1:
					{
					setState(89);
					((LimitClauseContext)_localctx).offset = decimalLiteral();
					setState(90);
					match(T__1);
					}
					break;
				}
				setState(94);
				((LimitClauseContext)_localctx).limit = decimalLiteral();
				}
				break;
			case 2:
				{
				setState(95);
				((LimitClauseContext)_localctx).limit = decimalLiteral();
				setState(96);
				match(OFFSET);
				setState(97);
				((LimitClauseContext)_localctx).offset = decimalLiteral();
				}
				break;
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

	public static class LogicalOperatorContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(APPDSLParser.AND, 0); }
		public LogicalOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logicalOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterLogicalOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitLogicalOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitLogicalOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicalOperatorContext logicalOperator() throws RecognitionException {
		LogicalOperatorContext _localctx = new LogicalOperatorContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_logicalOperator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(AND);
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

	public static class ComparisonOperatorContext extends ParserRuleContext {
		public ComparisonOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterComparisonOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitComparisonOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitComparisonOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonOperatorContext comparisonOperator() throws RecognitionException {
		ComparisonOperatorContext _localctx = new ComparisonOperatorContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_comparisonOperator);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			match(T__2);
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

	public static class ValueContext extends ParserRuleContext {
		public TextLiteralContext textLiteral() {
			return getRuleContext(TextLiteralContext.class,0);
		}
		public DecimalLiteralContext decimalLiteral() {
			return getRuleContext(DecimalLiteralContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_value);
		try {
			setState(107);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXT_STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				textLiteral();
				}
				break;
			case DECIMAL_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(106);
				decimalLiteral();
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

	public static class DecimalLiteralContext extends ParserRuleContext {
		public TerminalNode DECIMAL_LITERAL() { return getToken(APPDSLParser.DECIMAL_LITERAL, 0); }
		public DecimalLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decimalLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterDecimalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitDecimalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitDecimalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DecimalLiteralContext decimalLiteral() throws RecognitionException {
		DecimalLiteralContext _localctx = new DecimalLiteralContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_decimalLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			match(DECIMAL_LITERAL);
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

	public static class TextLiteralContext extends ParserRuleContext {
		public TerminalNode TEXT_STRING() { return getToken(APPDSLParser.TEXT_STRING, 0); }
		public TextLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_textLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterTextLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitTextLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitTextLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TextLiteralContext textLiteral() throws RecognitionException {
		TextLiteralContext _localctx = new TextLiteralContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_textLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(111);
			match(TEXT_STRING);
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

	public static class SelectElementContext extends ParserRuleContext {
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public TerminalNode AS() { return getToken(APPDSLParser.AS, 0); }
		public SelectElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterSelectElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitSelectElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitSelectElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectElementContext selectElement() throws RecognitionException {
		SelectElementContext _localctx = new SelectElementContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_selectElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			functionCall();
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==AS || _la==ID) {
				{
				setState(115);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS) {
					{
					setState(114);
					match(AS);
					}
				}

				setState(117);
				uid();
				}
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

	public static class AggregateWindowedFunctionContext extends ParserRuleContext {
		public Token starArg;
		public TerminalNode COUNT() { return getToken(APPDSLParser.COUNT, 0); }
		public FunctionArgContext functionArg() {
			return getRuleContext(FunctionArgContext.class,0);
		}
		public AggregateWindowedFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregateWindowedFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterAggregateWindowedFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitAggregateWindowedFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitAggregateWindowedFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregateWindowedFunctionContext aggregateWindowedFunction() throws RecognitionException {
		AggregateWindowedFunctionContext _localctx = new AggregateWindowedFunctionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_aggregateWindowedFunction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			match(COUNT);
			setState(121);
			match(T__3);
			setState(126);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				{
				setState(122);
				((AggregateWindowedFunctionContext)_localctx).starArg = match(T__0);
				}
				break;
			case T__4:
			case ID:
				{
				setState(124);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ID) {
					{
					setState(123);
					functionArg();
					}
				}

				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(128);
			match(T__4);
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

	public static class FunctionArgContext extends ParserRuleContext {
		public ColumnNameContext columnName() {
			return getRuleContext(ColumnNameContext.class,0);
		}
		public FunctionArgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionArg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterFunctionArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitFunctionArg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitFunctionArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionArgContext functionArg() throws RecognitionException {
		FunctionArgContext _localctx = new FunctionArgContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_functionArg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			columnName();
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

	public static class FullColumnNameContext extends ParserRuleContext {
		public ColumnNameContext columnName() {
			return getRuleContext(ColumnNameContext.class,0);
		}
		public FullColumnNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fullColumnName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterFullColumnName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitFullColumnName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitFullColumnName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FullColumnNameContext fullColumnName() throws RecognitionException {
		FullColumnNameContext _localctx = new FullColumnNameContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_fullColumnName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			columnName();
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

	public static class FunctionCallContext extends ParserRuleContext {
		public AggregateWindowedFunctionContext aggregateWindowedFunction() {
			return getRuleContext(AggregateWindowedFunctionContext.class,0);
		}
		public FunctionCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_functionCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			aggregateWindowedFunction();
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

	public static class ServiceNameContext extends ParserRuleContext {
		public Token tmpName;
		public TerminalNode ID() { return getToken(APPDSLParser.ID, 0); }
		public ServiceNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_serviceName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterServiceName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitServiceName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitServiceName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ServiceNameContext serviceName() throws RecognitionException {
		ServiceNameContext _localctx = new ServiceNameContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_serviceName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			((ServiceNameContext)_localctx).tmpName = match(ID);
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

	public static class ColumnNameContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(APPDSLParser.ID, 0); }
		public ColumnNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterColumnName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitColumnName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitColumnName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnNameContext columnName() throws RecognitionException {
		ColumnNameContext _localctx = new ColumnNameContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_columnName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			match(ID);
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

	public static class UidContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(APPDSLParser.ID, 0); }
		public UidContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uid; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).enterUid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof APPDSLListener ) ((APPDSLListener)listener).exitUid(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof APPDSLVisitor ) return ((APPDSLVisitor<? extends T>)visitor).visitUid(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UidContext uid() throws RecognitionException {
		UidContext _localctx = new UidContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_uid);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			match(ID);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\27\u0091\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\2\5\2\62"+
		"\n\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\5\4;\n\4\3\5\3\5\3\5\3\5\3\5\5\5B\n\5"+
		"\3\5\5\5E\n\5\3\6\3\6\5\6I\n\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\7\bR\n\b\f"+
		"\b\16\bU\13\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\5\n_\n\n\3\n\3\n\3\n\3\n"+
		"\3\n\5\nf\n\n\3\13\3\13\3\f\3\f\3\r\3\r\5\rn\n\r\3\16\3\16\3\17\3\17\3"+
		"\20\3\20\5\20v\n\20\3\20\5\20y\n\20\3\21\3\21\3\21\3\21\5\21\177\n\21"+
		"\5\21\u0081\n\21\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26"+
		"\3\26\3\27\3\27\3\27\2\2\30\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \""+
		"$&(*,\2\2\2\u0088\2\61\3\2\2\2\4\63\3\2\2\2\6\66\3\2\2\2\b<\3\2\2\2\n"+
		"H\3\2\2\2\fJ\3\2\2\2\16M\3\2\2\2\20V\3\2\2\2\22Z\3\2\2\2\24g\3\2\2\2\26"+
		"i\3\2\2\2\30m\3\2\2\2\32o\3\2\2\2\34q\3\2\2\2\36s\3\2\2\2 z\3\2\2\2\""+
		"\u0084\3\2\2\2$\u0086\3\2\2\2&\u0088\3\2\2\2(\u008a\3\2\2\2*\u008c\3\2"+
		"\2\2,\u008e\3\2\2\2.\62\5\b\5\2/\62\5\4\3\2\60\62\5\6\4\2\61.\3\2\2\2"+
		"\61/\3\2\2\2\61\60\3\2\2\2\62\3\3\2\2\2\63\64\7\20\2\2\64\65\5(\25\2\65"+
		"\5\3\2\2\2\66\67\7\21\2\2\67:\7\22\2\289\7\23\2\29;\5\34\17\2:8\3\2\2"+
		"\2:;\3\2\2\2;\7\3\2\2\2<=\7\n\2\2=>\5\n\6\2>?\7\13\2\2?A\5(\25\2@B\5\f"+
		"\7\2A@\3\2\2\2AB\3\2\2\2BD\3\2\2\2CE\5\22\n\2DC\3\2\2\2DE\3\2\2\2E\t\3"+
		"\2\2\2FI\7\3\2\2GI\5\36\20\2HF\3\2\2\2HG\3\2\2\2I\13\3\2\2\2JK\7\f\2\2"+
		"KL\5\16\b\2L\r\3\2\2\2MS\5\20\t\2NO\5\24\13\2OP\5\20\t\2PR\3\2\2\2QN\3"+
		"\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2T\17\3\2\2\2US\3\2\2\2VW\5$\23\2W"+
		"X\5\26\f\2XY\5\30\r\2Y\21\3\2\2\2Ze\7\16\2\2[\\\5\32\16\2\\]\7\4\2\2]"+
		"_\3\2\2\2^[\3\2\2\2^_\3\2\2\2_`\3\2\2\2`f\5\32\16\2ab\5\32\16\2bc\7\17"+
		"\2\2cd\5\32\16\2df\3\2\2\2e^\3\2\2\2ea\3\2\2\2f\23\3\2\2\2gh\7\r\2\2h"+
		"\25\3\2\2\2ij\7\5\2\2j\27\3\2\2\2kn\5\34\17\2ln\5\32\16\2mk\3\2\2\2ml"+
		"\3\2\2\2n\31\3\2\2\2op\7\26\2\2p\33\3\2\2\2qr\7\25\2\2r\35\3\2\2\2sx\5"+
		"&\24\2tv\7\b\2\2ut\3\2\2\2uv\3\2\2\2vw\3\2\2\2wy\5,\27\2xu\3\2\2\2xy\3"+
		"\2\2\2y\37\3\2\2\2z{\7\t\2\2{\u0080\7\6\2\2|\u0081\7\3\2\2}\177\5\"\22"+
		"\2~}\3\2\2\2~\177\3\2\2\2\177\u0081\3\2\2\2\u0080|\3\2\2\2\u0080~\3\2"+
		"\2\2\u0081\u0082\3\2\2\2\u0082\u0083\7\7\2\2\u0083!\3\2\2\2\u0084\u0085"+
		"\5*\26\2\u0085#\3\2\2\2\u0086\u0087\5*\26\2\u0087%\3\2\2\2\u0088\u0089"+
		"\5 \21\2\u0089\'\3\2\2\2\u008a\u008b\7\24\2\2\u008b)\3\2\2\2\u008c\u008d"+
		"\7\24\2\2\u008d+\3\2\2\2\u008e\u008f\7\24\2\2\u008f-\3\2\2\2\17\61:AD"+
		"HS^emux~\u0080";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}