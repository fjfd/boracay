// Generated from B:/workspaces/IntellijIdeaGithubWorkspaces/github/boracay/dsl/resource\DSLSQL.g4 by ANTLR 4.7.2

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
public class DSLSQLParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, AS=9, 
		SELECT=10, FROM=11, MAX=12, SUM=13, AVG=14, MIN=15, COUNT=16, DISTINCT=17, 
		WHERE=18, GROUP=19, BY=20, ORDER=21, NOT=22, IS=23, BETWEEN=24, AND=25, 
		IN=26, NULL=27, OR=28, ASC=29, DESC=30, LIMIT=31, OFFSET=32, LIKE=33, 
		DESCRIBE=34, SHOW=35, SERVICES=36, ID=37, TEXT_STRING=38, DECIMAL_LITERAL=39, 
		WS=40;
	public static final int
		RULE_statement = 0, RULE_describeServiceStatement = 1, RULE_showServicesStatement = 2, 
		RULE_selectStatement = 3, RULE_selectElements = 4, RULE_whereClause = 5, 
		RULE_logicExpressions = 6, RULE_logicExpression = 7, RULE_groupByCaluse = 8, 
		RULE_orderByClause = 9, RULE_limitClause = 10, RULE_orderByExpression = 11, 
		RULE_groupByItem = 12, RULE_logicalOperator = 13, RULE_comparisonOperator = 14, 
		RULE_value = 15, RULE_decimalLiteral = 16, RULE_textLiteral = 17, RULE_selectElement = 18, 
		RULE_fullColumnName = 19, RULE_functionCall = 20, RULE_aggregateWindowedFunction = 21, 
		RULE_functionArg = 22, RULE_functionArgs = 23, RULE_serviceName = 24, 
		RULE_columnName = 25, RULE_uid = 26;
	private static String[] makeRuleNames() {
		return new String[] {
			"statement", "describeServiceStatement", "showServicesStatement", "selectStatement", 
			"selectElements", "whereClause", "logicExpressions", "logicExpression", 
			"groupByCaluse", "orderByClause", "limitClause", "orderByExpression", 
			"groupByItem", "logicalOperator", "comparisonOperator", "value", "decimalLiteral", 
			"textLiteral", "selectElement", "fullColumnName", "functionCall", "aggregateWindowedFunction", 
			"functionArg", "functionArgs", "serviceName", "columnName", "uid"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'*'", "','", "'('", "')'", "'='", "'>'", "'<'", "'!'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, "AS", "SELECT", 
			"FROM", "MAX", "SUM", "AVG", "MIN", "COUNT", "DISTINCT", "WHERE", "GROUP", 
			"BY", "ORDER", "NOT", "IS", "BETWEEN", "AND", "IN", "NULL", "OR", "ASC", 
			"DESC", "LIMIT", "OFFSET", "LIKE", "DESCRIBE", "SHOW", "SERVICES", "ID", 
			"TEXT_STRING", "DECIMAL_LITERAL", "WS"
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
	public String getGrammarFileName() { return "DSLSQL.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DSLSQLParser(TokenStream input) {
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
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_statement);
		try {
			setState(57);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT:
				enterOuterAlt(_localctx, 1);
				{
				setState(54);
				selectStatement();
				}
				break;
			case DESCRIBE:
				enterOuterAlt(_localctx, 2);
				{
				setState(55);
				describeServiceStatement();
				}
				break;
			case SHOW:
				enterOuterAlt(_localctx, 3);
				{
				setState(56);
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
		public TerminalNode DESCRIBE() { return getToken(DSLSQLParser.DESCRIBE, 0); }
		public ServiceNameContext serviceName() {
			return getRuleContext(ServiceNameContext.class,0);
		}
		public DescribeServiceStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_describeServiceStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterDescribeServiceStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitDescribeServiceStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitDescribeServiceStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DescribeServiceStatementContext describeServiceStatement() throws RecognitionException {
		DescribeServiceStatementContext _localctx = new DescribeServiceStatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_describeServiceStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			match(DESCRIBE);
			setState(60);
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
		public TerminalNode SHOW() { return getToken(DSLSQLParser.SHOW, 0); }
		public TerminalNode SERVICES() { return getToken(DSLSQLParser.SERVICES, 0); }
		public TerminalNode LIKE() { return getToken(DSLSQLParser.LIKE, 0); }
		public TextLiteralContext textLiteral() {
			return getRuleContext(TextLiteralContext.class,0);
		}
		public ShowServicesStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_showServicesStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterShowServicesStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitShowServicesStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitShowServicesStatement(this);
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
			setState(62);
			match(SHOW);
			setState(63);
			match(SERVICES);
			setState(66);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIKE) {
				{
				setState(64);
				match(LIKE);
				setState(65);
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
		public TerminalNode SELECT() { return getToken(DSLSQLParser.SELECT, 0); }
		public SelectElementsContext selectElements() {
			return getRuleContext(SelectElementsContext.class,0);
		}
		public TerminalNode FROM() { return getToken(DSLSQLParser.FROM, 0); }
		public ServiceNameContext serviceName() {
			return getRuleContext(ServiceNameContext.class,0);
		}
		public WhereClauseContext whereClause() {
			return getRuleContext(WhereClauseContext.class,0);
		}
		public GroupByCaluseContext groupByCaluse() {
			return getRuleContext(GroupByCaluseContext.class,0);
		}
		public OrderByClauseContext orderByClause() {
			return getRuleContext(OrderByClauseContext.class,0);
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
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterSelectStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitSelectStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitSelectStatement(this);
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
			setState(68);
			match(SELECT);
			setState(69);
			selectElements();
			setState(70);
			match(FROM);
			setState(71);
			serviceName();
			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WHERE) {
				{
				setState(72);
				whereClause();
				}
			}

			setState(76);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==GROUP) {
				{
				setState(75);
				groupByCaluse();
				}
			}

			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ORDER) {
				{
				setState(78);
				orderByClause();
				}
			}

			setState(82);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIMIT) {
				{
				setState(81);
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
		public List<SelectElementContext> selectElement() {
			return getRuleContexts(SelectElementContext.class);
		}
		public SelectElementContext selectElement(int i) {
			return getRuleContext(SelectElementContext.class,i);
		}
		public SelectElementsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectElements; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterSelectElements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitSelectElements(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitSelectElements(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectElementsContext selectElements() throws RecognitionException {
		SelectElementsContext _localctx = new SelectElementsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_selectElements);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				{
				setState(84);
				((SelectElementsContext)_localctx).star = match(T__0);
				}
				break;
			case MAX:
			case SUM:
			case AVG:
			case MIN:
			case COUNT:
			case ID:
				{
				setState(85);
				selectElement();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(88);
				match(T__1);
				setState(89);
				selectElement();
				}
				}
				setState(94);
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

	public static class WhereClauseContext extends ParserRuleContext {
		public TerminalNode WHERE() { return getToken(DSLSQLParser.WHERE, 0); }
		public LogicExpressionsContext logicExpressions() {
			return getRuleContext(LogicExpressionsContext.class,0);
		}
		public WhereClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whereClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterWhereClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitWhereClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitWhereClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final WhereClauseContext whereClause() throws RecognitionException {
		WhereClauseContext _localctx = new WhereClauseContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_whereClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(WHERE);
			setState(96);
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
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterLogicExpressions(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitLogicExpressions(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitLogicExpressions(this);
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
			setState(98);
			logicExpression();
			setState(104);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND || _la==OR) {
				{
				{
				setState(99);
				logicalOperator();
				setState(100);
				logicExpression();
				}
				}
				setState(106);
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
		public List<ValueContext> value() {
			return getRuleContexts(ValueContext.class);
		}
		public ValueContext value(int i) {
			return getRuleContext(ValueContext.class,i);
		}
		public TerminalNode BETWEEN() { return getToken(DSLSQLParser.BETWEEN, 0); }
		public TerminalNode AND() { return getToken(DSLSQLParser.AND, 0); }
		public TerminalNode IN() { return getToken(DSLSQLParser.IN, 0); }
		public TerminalNode NOT() { return getToken(DSLSQLParser.NOT, 0); }
		public LogicExpressionsContext logicExpressions() {
			return getRuleContext(LogicExpressionsContext.class,0);
		}
		public LogicExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logicExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterLogicExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitLogicExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitLogicExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicExpressionContext logicExpression() throws RecognitionException {
		LogicExpressionContext _localctx = new LogicExpressionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_logicExpression);
		int _la;
		try {
			setState(137);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(107);
				fullColumnName();
				setState(108);
				comparisonOperator();
				setState(109);
				value();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(111);
				fullColumnName();
				setState(112);
				match(BETWEEN);
				setState(113);
				value();
				setState(114);
				match(AND);
				setState(115);
				value();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(117);
				fullColumnName();
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(118);
					match(NOT);
					}
				}

				setState(121);
				match(IN);
				setState(122);
				match(T__2);
				setState(123);
				value();
				setState(128);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__1) {
					{
					{
					setState(124);
					match(T__1);
					setState(125);
					value();
					}
					}
					setState(130);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(131);
				match(T__3);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(133);
				match(T__2);
				setState(134);
				logicExpressions();
				setState(135);
				match(T__3);
				}
				break;
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

	public static class GroupByCaluseContext extends ParserRuleContext {
		public TerminalNode GROUP() { return getToken(DSLSQLParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(DSLSQLParser.BY, 0); }
		public List<GroupByItemContext> groupByItem() {
			return getRuleContexts(GroupByItemContext.class);
		}
		public GroupByItemContext groupByItem(int i) {
			return getRuleContext(GroupByItemContext.class,i);
		}
		public GroupByCaluseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupByCaluse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterGroupByCaluse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitGroupByCaluse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitGroupByCaluse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupByCaluseContext groupByCaluse() throws RecognitionException {
		GroupByCaluseContext _localctx = new GroupByCaluseContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_groupByCaluse);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139);
			match(GROUP);
			setState(140);
			match(BY);
			setState(141);
			groupByItem();
			setState(146);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(142);
				match(T__1);
				setState(143);
				groupByItem();
				}
				}
				setState(148);
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

	public static class OrderByClauseContext extends ParserRuleContext {
		public TerminalNode ORDER() { return getToken(DSLSQLParser.ORDER, 0); }
		public TerminalNode BY() { return getToken(DSLSQLParser.BY, 0); }
		public List<OrderByExpressionContext> orderByExpression() {
			return getRuleContexts(OrderByExpressionContext.class);
		}
		public OrderByExpressionContext orderByExpression(int i) {
			return getRuleContext(OrderByExpressionContext.class,i);
		}
		public OrderByClauseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderByClause; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterOrderByClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitOrderByClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitOrderByClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrderByClauseContext orderByClause() throws RecognitionException {
		OrderByClauseContext _localctx = new OrderByClauseContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_orderByClause);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			match(ORDER);
			setState(150);
			match(BY);
			setState(151);
			orderByExpression();
			setState(156);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(152);
				match(T__1);
				setState(153);
				orderByExpression();
				}
				}
				setState(158);
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

	public static class LimitClauseContext extends ParserRuleContext {
		public DecimalLiteralContext offset;
		public DecimalLiteralContext limit;
		public TerminalNode LIMIT() { return getToken(DSLSQLParser.LIMIT, 0); }
		public TerminalNode OFFSET() { return getToken(DSLSQLParser.OFFSET, 0); }
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
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterLimitClause(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitLimitClause(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitLimitClause(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LimitClauseContext limitClause() throws RecognitionException {
		LimitClauseContext _localctx = new LimitClauseContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_limitClause);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(LIMIT);
			setState(170);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(163);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
				case 1:
					{
					setState(160);
					((LimitClauseContext)_localctx).offset = decimalLiteral();
					setState(161);
					match(T__1);
					}
					break;
				}
				setState(165);
				((LimitClauseContext)_localctx).limit = decimalLiteral();
				}
				break;
			case 2:
				{
				setState(166);
				((LimitClauseContext)_localctx).limit = decimalLiteral();
				setState(167);
				match(OFFSET);
				setState(168);
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

	public static class OrderByExpressionContext extends ParserRuleContext {
		public Token order;
		public FullColumnNameContext fullColumnName() {
			return getRuleContext(FullColumnNameContext.class,0);
		}
		public TerminalNode ASC() { return getToken(DSLSQLParser.ASC, 0); }
		public TerminalNode DESC() { return getToken(DSLSQLParser.DESC, 0); }
		public OrderByExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderByExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterOrderByExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitOrderByExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitOrderByExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrderByExpressionContext orderByExpression() throws RecognitionException {
		OrderByExpressionContext _localctx = new OrderByExpressionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_orderByExpression);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			fullColumnName();
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASC || _la==DESC) {
				{
				setState(173);
				((OrderByExpressionContext)_localctx).order = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==ASC || _la==DESC) ) {
					((OrderByExpressionContext)_localctx).order = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
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

	public static class GroupByItemContext extends ParserRuleContext {
		public FullColumnNameContext fullColumnName() {
			return getRuleContext(FullColumnNameContext.class,0);
		}
		public GroupByItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_groupByItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterGroupByItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitGroupByItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitGroupByItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GroupByItemContext groupByItem() throws RecognitionException {
		GroupByItemContext _localctx = new GroupByItemContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_groupByItem);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			fullColumnName();
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
		public TerminalNode AND() { return getToken(DSLSQLParser.AND, 0); }
		public TerminalNode OR() { return getToken(DSLSQLParser.OR, 0); }
		public LogicalOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logicalOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterLogicalOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitLogicalOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitLogicalOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicalOperatorContext logicalOperator() throws RecognitionException {
		LogicalOperatorContext _localctx = new LogicalOperatorContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_logicalOperator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			_la = _input.LA(1);
			if ( !(_la==AND || _la==OR) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class ComparisonOperatorContext extends ParserRuleContext {
		public TerminalNode LIKE() { return getToken(DSLSQLParser.LIKE, 0); }
		public ComparisonOperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_comparisonOperator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterComparisonOperator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitComparisonOperator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitComparisonOperator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComparisonOperatorContext comparisonOperator() throws RecognitionException {
		ComparisonOperatorContext _localctx = new ComparisonOperatorContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_comparisonOperator);
		try {
			setState(190);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(180);
				match(T__4);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(181);
				match(T__5);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(182);
				match(T__6);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(183);
				match(T__6);
				setState(184);
				match(T__4);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(185);
				match(T__5);
				setState(186);
				match(T__4);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(187);
				match(T__7);
				setState(188);
				match(T__4);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(189);
				match(LIKE);
				}
				break;
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
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_value);
		try {
			setState(194);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case TEXT_STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(192);
				textLiteral();
				}
				break;
			case DECIMAL_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(193);
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
		public TerminalNode DECIMAL_LITERAL() { return getToken(DSLSQLParser.DECIMAL_LITERAL, 0); }
		public DecimalLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decimalLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterDecimalLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitDecimalLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitDecimalLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DecimalLiteralContext decimalLiteral() throws RecognitionException {
		DecimalLiteralContext _localctx = new DecimalLiteralContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_decimalLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196);
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
		public TerminalNode TEXT_STRING() { return getToken(DSLSQLParser.TEXT_STRING, 0); }
		public TextLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_textLiteral; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterTextLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitTextLiteral(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitTextLiteral(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TextLiteralContext textLiteral() throws RecognitionException {
		TextLiteralContext _localctx = new TextLiteralContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_textLiteral);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(198);
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
		public FullColumnNameContext fullColumnName() {
			return getRuleContext(FullColumnNameContext.class,0);
		}
		public UidContext uid() {
			return getRuleContext(UidContext.class,0);
		}
		public TerminalNode AS() { return getToken(DSLSQLParser.AS, 0); }
		public FunctionCallContext functionCall() {
			return getRuleContext(FunctionCallContext.class,0);
		}
		public SelectElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterSelectElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitSelectElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitSelectElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectElementContext selectElement() throws RecognitionException {
		SelectElementContext _localctx = new SelectElementContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_selectElement);
		int _la;
		try {
			setState(214);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(200);
				fullColumnName();
				setState(205);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS || _la==ID) {
					{
					setState(202);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==AS) {
						{
						setState(201);
						match(AS);
						}
					}

					setState(204);
					uid();
					}
				}

				}
				break;
			case MAX:
			case SUM:
			case AVG:
			case MIN:
			case COUNT:
				enterOuterAlt(_localctx, 2);
				{
				setState(207);
				functionCall();
				setState(212);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==AS || _la==ID) {
					{
					setState(209);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==AS) {
						{
						setState(208);
						match(AS);
						}
					}

					setState(211);
					uid();
					}
				}

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
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterFullColumnName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitFullColumnName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitFullColumnName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FullColumnNameContext fullColumnName() throws RecognitionException {
		FullColumnNameContext _localctx = new FullColumnNameContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_fullColumnName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(216);
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
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterFunctionCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitFunctionCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitFunctionCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionCallContext functionCall() throws RecognitionException {
		FunctionCallContext _localctx = new FunctionCallContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_functionCall);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
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

	public static class AggregateWindowedFunctionContext extends ParserRuleContext {
		public Token starArg;
		public Token aggregator;
		public FunctionArgContext functionArg() {
			return getRuleContext(FunctionArgContext.class,0);
		}
		public TerminalNode AVG() { return getToken(DSLSQLParser.AVG, 0); }
		public TerminalNode MAX() { return getToken(DSLSQLParser.MAX, 0); }
		public TerminalNode MIN() { return getToken(DSLSQLParser.MIN, 0); }
		public TerminalNode SUM() { return getToken(DSLSQLParser.SUM, 0); }
		public TerminalNode COUNT() { return getToken(DSLSQLParser.COUNT, 0); }
		public FunctionArgsContext functionArgs() {
			return getRuleContext(FunctionArgsContext.class,0);
		}
		public TerminalNode DISTINCT() { return getToken(DSLSQLParser.DISTINCT, 0); }
		public AggregateWindowedFunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggregateWindowedFunction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterAggregateWindowedFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitAggregateWindowedFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitAggregateWindowedFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggregateWindowedFunctionContext aggregateWindowedFunction() throws RecognitionException {
		AggregateWindowedFunctionContext _localctx = new AggregateWindowedFunctionContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_aggregateWindowedFunction);
		int _la;
		try {
			setState(240);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(220);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MAX) | (1L << SUM) | (1L << AVG) | (1L << MIN))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(221);
				match(T__2);
				setState(222);
				functionArg();
				setState(223);
				match(T__3);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(225);
				match(COUNT);
				setState(226);
				match(T__2);
				setState(231);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__0:
					{
					setState(227);
					((AggregateWindowedFunctionContext)_localctx).starArg = match(T__0);
					}
					break;
				case T__3:
				case ID:
					{
					setState(229);
					_errHandler.sync(this);
					_la = _input.LA(1);
					if (_la==ID) {
						{
						setState(228);
						functionArg();
						}
					}

					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(233);
				match(T__3);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(234);
				match(COUNT);
				setState(235);
				match(T__2);
				setState(236);
				((AggregateWindowedFunctionContext)_localctx).aggregator = match(DISTINCT);
				setState(237);
				functionArgs();
				setState(238);
				match(T__3);
				}
				break;
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
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterFunctionArg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitFunctionArg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitFunctionArg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionArgContext functionArg() throws RecognitionException {
		FunctionArgContext _localctx = new FunctionArgContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_functionArg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(242);
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

	public static class FunctionArgsContext extends ParserRuleContext {
		public List<ColumnNameContext> columnName() {
			return getRuleContexts(ColumnNameContext.class);
		}
		public ColumnNameContext columnName(int i) {
			return getRuleContext(ColumnNameContext.class,i);
		}
		public FunctionArgsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionArgs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterFunctionArgs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitFunctionArgs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitFunctionArgs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionArgsContext functionArgs() throws RecognitionException {
		FunctionArgsContext _localctx = new FunctionArgsContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_functionArgs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			columnName();
			setState(249);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(245);
				match(T__1);
				setState(246);
				columnName();
				}
				}
				setState(251);
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

	public static class ServiceNameContext extends ParserRuleContext {
		public Token tmpName;
		public TerminalNode ID() { return getToken(DSLSQLParser.ID, 0); }
		public ServiceNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_serviceName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterServiceName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitServiceName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitServiceName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ServiceNameContext serviceName() throws RecognitionException {
		ServiceNameContext _localctx = new ServiceNameContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_serviceName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(252);
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
		public TerminalNode ID() { return getToken(DSLSQLParser.ID, 0); }
		public ColumnNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_columnName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterColumnName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitColumnName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitColumnName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ColumnNameContext columnName() throws RecognitionException {
		ColumnNameContext _localctx = new ColumnNameContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_columnName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(254);
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
		public TerminalNode ID() { return getToken(DSLSQLParser.ID, 0); }
		public UidContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uid; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).enterUid(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DSLSQLListener ) ((DSLSQLListener)listener).exitUid(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof DSLSQLVisitor ) return ((DSLSQLVisitor<? extends T>)visitor).visitUid(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UidContext uid() throws RecognitionException {
		UidContext _localctx = new UidContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_uid);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(256);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3*\u0105\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\2\5\2<\n\2\3\3\3\3\3\3\3\4\3"+
		"\4\3\4\3\4\5\4E\n\4\3\5\3\5\3\5\3\5\3\5\5\5L\n\5\3\5\5\5O\n\5\3\5\5\5"+
		"R\n\5\3\5\5\5U\n\5\3\6\3\6\5\6Y\n\6\3\6\3\6\7\6]\n\6\f\6\16\6`\13\6\3"+
		"\7\3\7\3\7\3\b\3\b\3\b\3\b\7\bi\n\b\f\b\16\bl\13\b\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\tz\n\t\3\t\3\t\3\t\3\t\3\t\7\t\u0081\n"+
		"\t\f\t\16\t\u0084\13\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u008c\n\t\3\n\3\n\3"+
		"\n\3\n\3\n\7\n\u0093\n\n\f\n\16\n\u0096\13\n\3\13\3\13\3\13\3\13\3\13"+
		"\7\13\u009d\n\13\f\13\16\13\u00a0\13\13\3\f\3\f\3\f\3\f\5\f\u00a6\n\f"+
		"\3\f\3\f\3\f\3\f\3\f\5\f\u00ad\n\f\3\r\3\r\5\r\u00b1\n\r\3\16\3\16\3\17"+
		"\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00c1\n\20"+
		"\3\21\3\21\5\21\u00c5\n\21\3\22\3\22\3\23\3\23\3\24\3\24\5\24\u00cd\n"+
		"\24\3\24\5\24\u00d0\n\24\3\24\3\24\5\24\u00d4\n\24\3\24\5\24\u00d7\n\24"+
		"\5\24\u00d9\n\24\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\5\27\u00e8\n\27\5\27\u00ea\n\27\3\27\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\5\27\u00f3\n\27\3\30\3\30\3\31\3\31\3\31\7\31\u00fa\n\31\f\31"+
		"\16\31\u00fd\13\31\3\32\3\32\3\33\3\33\3\34\3\34\3\34\2\2\35\2\4\6\b\n"+
		"\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\66\2\5\3\2\37 \4\2\33"+
		"\33\36\36\3\2\16\21\2\u010e\2;\3\2\2\2\4=\3\2\2\2\6@\3\2\2\2\bF\3\2\2"+
		"\2\nX\3\2\2\2\fa\3\2\2\2\16d\3\2\2\2\20\u008b\3\2\2\2\22\u008d\3\2\2\2"+
		"\24\u0097\3\2\2\2\26\u00a1\3\2\2\2\30\u00ae\3\2\2\2\32\u00b2\3\2\2\2\34"+
		"\u00b4\3\2\2\2\36\u00c0\3\2\2\2 \u00c4\3\2\2\2\"\u00c6\3\2\2\2$\u00c8"+
		"\3\2\2\2&\u00d8\3\2\2\2(\u00da\3\2\2\2*\u00dc\3\2\2\2,\u00f2\3\2\2\2."+
		"\u00f4\3\2\2\2\60\u00f6\3\2\2\2\62\u00fe\3\2\2\2\64\u0100\3\2\2\2\66\u0102"+
		"\3\2\2\28<\5\b\5\29<\5\4\3\2:<\5\6\4\2;8\3\2\2\2;9\3\2\2\2;:\3\2\2\2<"+
		"\3\3\2\2\2=>\7$\2\2>?\5\62\32\2?\5\3\2\2\2@A\7%\2\2AD\7&\2\2BC\7#\2\2"+
		"CE\5$\23\2DB\3\2\2\2DE\3\2\2\2E\7\3\2\2\2FG\7\f\2\2GH\5\n\6\2HI\7\r\2"+
		"\2IK\5\62\32\2JL\5\f\7\2KJ\3\2\2\2KL\3\2\2\2LN\3\2\2\2MO\5\22\n\2NM\3"+
		"\2\2\2NO\3\2\2\2OQ\3\2\2\2PR\5\24\13\2QP\3\2\2\2QR\3\2\2\2RT\3\2\2\2S"+
		"U\5\26\f\2TS\3\2\2\2TU\3\2\2\2U\t\3\2\2\2VY\7\3\2\2WY\5&\24\2XV\3\2\2"+
		"\2XW\3\2\2\2Y^\3\2\2\2Z[\7\4\2\2[]\5&\24\2\\Z\3\2\2\2]`\3\2\2\2^\\\3\2"+
		"\2\2^_\3\2\2\2_\13\3\2\2\2`^\3\2\2\2ab\7\24\2\2bc\5\16\b\2c\r\3\2\2\2"+
		"dj\5\20\t\2ef\5\34\17\2fg\5\20\t\2gi\3\2\2\2he\3\2\2\2il\3\2\2\2jh\3\2"+
		"\2\2jk\3\2\2\2k\17\3\2\2\2lj\3\2\2\2mn\5(\25\2no\5\36\20\2op\5 \21\2p"+
		"\u008c\3\2\2\2qr\5(\25\2rs\7\32\2\2st\5 \21\2tu\7\33\2\2uv\5 \21\2v\u008c"+
		"\3\2\2\2wy\5(\25\2xz\7\30\2\2yx\3\2\2\2yz\3\2\2\2z{\3\2\2\2{|\7\34\2\2"+
		"|}\7\5\2\2}\u0082\5 \21\2~\177\7\4\2\2\177\u0081\5 \21\2\u0080~\3\2\2"+
		"\2\u0081\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085"+
		"\3\2\2\2\u0084\u0082\3\2\2\2\u0085\u0086\7\6\2\2\u0086\u008c\3\2\2\2\u0087"+
		"\u0088\7\5\2\2\u0088\u0089\5\16\b\2\u0089\u008a\7\6\2\2\u008a\u008c\3"+
		"\2\2\2\u008bm\3\2\2\2\u008bq\3\2\2\2\u008bw\3\2\2\2\u008b\u0087\3\2\2"+
		"\2\u008c\21\3\2\2\2\u008d\u008e\7\25\2\2\u008e\u008f\7\26\2\2\u008f\u0094"+
		"\5\32\16\2\u0090\u0091\7\4\2\2\u0091\u0093\5\32\16\2\u0092\u0090\3\2\2"+
		"\2\u0093\u0096\3\2\2\2\u0094\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095\23"+
		"\3\2\2\2\u0096\u0094\3\2\2\2\u0097\u0098\7\27\2\2\u0098\u0099\7\26\2\2"+
		"\u0099\u009e\5\30\r\2\u009a\u009b\7\4\2\2\u009b\u009d\5\30\r\2\u009c\u009a"+
		"\3\2\2\2\u009d\u00a0\3\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f"+
		"\25\3\2\2\2\u00a0\u009e\3\2\2\2\u00a1\u00ac\7!\2\2\u00a2\u00a3\5\"\22"+
		"\2\u00a3\u00a4\7\4\2\2\u00a4\u00a6\3\2\2\2\u00a5\u00a2\3\2\2\2\u00a5\u00a6"+
		"\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00ad\5\"\22\2\u00a8\u00a9\5\"\22\2"+
		"\u00a9\u00aa\7\"\2\2\u00aa\u00ab\5\"\22\2\u00ab\u00ad\3\2\2\2\u00ac\u00a5"+
		"\3\2\2\2\u00ac\u00a8\3\2\2\2\u00ad\27\3\2\2\2\u00ae\u00b0\5(\25\2\u00af"+
		"\u00b1\t\2\2\2\u00b0\u00af\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\31\3\2\2"+
		"\2\u00b2\u00b3\5(\25\2\u00b3\33\3\2\2\2\u00b4\u00b5\t\3\2\2\u00b5\35\3"+
		"\2\2\2\u00b6\u00c1\7\7\2\2\u00b7\u00c1\7\b\2\2\u00b8\u00c1\7\t\2\2\u00b9"+
		"\u00ba\7\t\2\2\u00ba\u00c1\7\7\2\2\u00bb\u00bc\7\b\2\2\u00bc\u00c1\7\7"+
		"\2\2\u00bd\u00be\7\n\2\2\u00be\u00c1\7\7\2\2\u00bf\u00c1\7#\2\2\u00c0"+
		"\u00b6\3\2\2\2\u00c0\u00b7\3\2\2\2\u00c0\u00b8\3\2\2\2\u00c0\u00b9\3\2"+
		"\2\2\u00c0\u00bb\3\2\2\2\u00c0\u00bd\3\2\2\2\u00c0\u00bf\3\2\2\2\u00c1"+
		"\37\3\2\2\2\u00c2\u00c5\5$\23\2\u00c3\u00c5\5\"\22\2\u00c4\u00c2\3\2\2"+
		"\2\u00c4\u00c3\3\2\2\2\u00c5!\3\2\2\2\u00c6\u00c7\7)\2\2\u00c7#\3\2\2"+
		"\2\u00c8\u00c9\7(\2\2\u00c9%\3\2\2\2\u00ca\u00cf\5(\25\2\u00cb\u00cd\7"+
		"\13\2\2\u00cc\u00cb\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce"+
		"\u00d0\5\66\34\2\u00cf\u00cc\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d9\3"+
		"\2\2\2\u00d1\u00d6\5*\26\2\u00d2\u00d4\7\13\2\2\u00d3\u00d2\3\2\2\2\u00d3"+
		"\u00d4\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d7\5\66\34\2\u00d6\u00d3\3"+
		"\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\u00d9\3\2\2\2\u00d8\u00ca\3\2\2\2\u00d8"+
		"\u00d1\3\2\2\2\u00d9\'\3\2\2\2\u00da\u00db\5\64\33\2\u00db)\3\2\2\2\u00dc"+
		"\u00dd\5,\27\2\u00dd+\3\2\2\2\u00de\u00df\t\4\2\2\u00df\u00e0\7\5\2\2"+
		"\u00e0\u00e1\5.\30\2\u00e1\u00e2\7\6\2\2\u00e2\u00f3\3\2\2\2\u00e3\u00e4"+
		"\7\22\2\2\u00e4\u00e9\7\5\2\2\u00e5\u00ea\7\3\2\2\u00e6\u00e8\5.\30\2"+
		"\u00e7\u00e6\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00ea\3\2\2\2\u00e9\u00e5"+
		"\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00eb\3\2\2\2\u00eb\u00f3\7\6\2\2\u00ec"+
		"\u00ed\7\22\2\2\u00ed\u00ee\7\5\2\2\u00ee\u00ef\7\23\2\2\u00ef\u00f0\5"+
		"\60\31\2\u00f0\u00f1\7\6\2\2\u00f1\u00f3\3\2\2\2\u00f2\u00de\3\2\2\2\u00f2"+
		"\u00e3\3\2\2\2\u00f2\u00ec\3\2\2\2\u00f3-\3\2\2\2\u00f4\u00f5\5\64\33"+
		"\2\u00f5/\3\2\2\2\u00f6\u00fb\5\64\33\2\u00f7\u00f8\7\4\2\2\u00f8\u00fa"+
		"\5\64\33\2\u00f9\u00f7\3\2\2\2\u00fa\u00fd\3\2\2\2\u00fb\u00f9\3\2\2\2"+
		"\u00fb\u00fc\3\2\2\2\u00fc\61\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fe\u00ff"+
		"\7\'\2\2\u00ff\63\3\2\2\2\u0100\u0101\7\'\2\2\u0101\65\3\2\2\2\u0102\u0103"+
		"\7\'\2\2\u0103\67\3\2\2\2\36;DKNQTX^jy\u0082\u008b\u0094\u009e\u00a5\u00ac"+
		"\u00b0\u00c0\u00c4\u00cc\u00cf\u00d3\u00d6\u00d8\u00e7\u00e9\u00f2\u00fb";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}