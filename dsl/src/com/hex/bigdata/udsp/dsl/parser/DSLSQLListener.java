// Generated from B:/workspaces/IntellijIdeaGithubWorkspaces/github/boracay/dsl/resource\DSLSQL.g4 by ANTLR 4.7.2

    package com.hex.bigdata.udsp.dsl.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DSLSQLParser}.
 */
public interface DSLSQLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(DSLSQLParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(DSLSQLParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#describeServiceStatement}.
	 * @param ctx the parse tree
	 */
	void enterDescribeServiceStatement(DSLSQLParser.DescribeServiceStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#describeServiceStatement}.
	 * @param ctx the parse tree
	 */
	void exitDescribeServiceStatement(DSLSQLParser.DescribeServiceStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#showServicesStatement}.
	 * @param ctx the parse tree
	 */
	void enterShowServicesStatement(DSLSQLParser.ShowServicesStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#showServicesStatement}.
	 * @param ctx the parse tree
	 */
	void exitShowServicesStatement(DSLSQLParser.ShowServicesStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectStatement(DSLSQLParser.SelectStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectStatement(DSLSQLParser.SelectStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#selectElements}.
	 * @param ctx the parse tree
	 */
	void enterSelectElements(DSLSQLParser.SelectElementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#selectElements}.
	 * @param ctx the parse tree
	 */
	void exitSelectElements(DSLSQLParser.SelectElementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void enterWhereClause(DSLSQLParser.WhereClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void exitWhereClause(DSLSQLParser.WhereClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#logicExpressions}.
	 * @param ctx the parse tree
	 */
	void enterLogicExpressions(DSLSQLParser.LogicExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#logicExpressions}.
	 * @param ctx the parse tree
	 */
	void exitLogicExpressions(DSLSQLParser.LogicExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#logicExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicExpression(DSLSQLParser.LogicExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#logicExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicExpression(DSLSQLParser.LogicExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#groupByCaluse}.
	 * @param ctx the parse tree
	 */
	void enterGroupByCaluse(DSLSQLParser.GroupByCaluseContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#groupByCaluse}.
	 * @param ctx the parse tree
	 */
	void exitGroupByCaluse(DSLSQLParser.GroupByCaluseContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#orderByClause}.
	 * @param ctx the parse tree
	 */
	void enterOrderByClause(DSLSQLParser.OrderByClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#orderByClause}.
	 * @param ctx the parse tree
	 */
	void exitOrderByClause(DSLSQLParser.OrderByClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#limitClause}.
	 * @param ctx the parse tree
	 */
	void enterLimitClause(DSLSQLParser.LimitClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#limitClause}.
	 * @param ctx the parse tree
	 */
	void exitLimitClause(DSLSQLParser.LimitClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#orderByExpression}.
	 * @param ctx the parse tree
	 */
	void enterOrderByExpression(DSLSQLParser.OrderByExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#orderByExpression}.
	 * @param ctx the parse tree
	 */
	void exitOrderByExpression(DSLSQLParser.OrderByExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#groupByItem}.
	 * @param ctx the parse tree
	 */
	void enterGroupByItem(DSLSQLParser.GroupByItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#groupByItem}.
	 * @param ctx the parse tree
	 */
	void exitGroupByItem(DSLSQLParser.GroupByItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOperator(DSLSQLParser.LogicalOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOperator(DSLSQLParser.LogicalOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(DSLSQLParser.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(DSLSQLParser.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(DSLSQLParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(DSLSQLParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#decimalLiteral}.
	 * @param ctx the parse tree
	 */
	void enterDecimalLiteral(DSLSQLParser.DecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#decimalLiteral}.
	 * @param ctx the parse tree
	 */
	void exitDecimalLiteral(DSLSQLParser.DecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#textLiteral}.
	 * @param ctx the parse tree
	 */
	void enterTextLiteral(DSLSQLParser.TextLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#textLiteral}.
	 * @param ctx the parse tree
	 */
	void exitTextLiteral(DSLSQLParser.TextLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#selectElement}.
	 * @param ctx the parse tree
	 */
	void enterSelectElement(DSLSQLParser.SelectElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#selectElement}.
	 * @param ctx the parse tree
	 */
	void exitSelectElement(DSLSQLParser.SelectElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#fullColumnName}.
	 * @param ctx the parse tree
	 */
	void enterFullColumnName(DSLSQLParser.FullColumnNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#fullColumnName}.
	 * @param ctx the parse tree
	 */
	void exitFullColumnName(DSLSQLParser.FullColumnNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(DSLSQLParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(DSLSQLParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#aggregateWindowedFunction}.
	 * @param ctx the parse tree
	 */
	void enterAggregateWindowedFunction(DSLSQLParser.AggregateWindowedFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#aggregateWindowedFunction}.
	 * @param ctx the parse tree
	 */
	void exitAggregateWindowedFunction(DSLSQLParser.AggregateWindowedFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#functionArg}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArg(DSLSQLParser.FunctionArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#functionArg}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArg(DSLSQLParser.FunctionArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#functionArgs}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArgs(DSLSQLParser.FunctionArgsContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#functionArgs}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArgs(DSLSQLParser.FunctionArgsContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#serviceName}.
	 * @param ctx the parse tree
	 */
	void enterServiceName(DSLSQLParser.ServiceNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#serviceName}.
	 * @param ctx the parse tree
	 */
	void exitServiceName(DSLSQLParser.ServiceNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#columnName}.
	 * @param ctx the parse tree
	 */
	void enterColumnName(DSLSQLParser.ColumnNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#columnName}.
	 * @param ctx the parse tree
	 */
	void exitColumnName(DSLSQLParser.ColumnNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link DSLSQLParser#uid}.
	 * @param ctx the parse tree
	 */
	void enterUid(DSLSQLParser.UidContext ctx);
	/**
	 * Exit a parse tree produced by {@link DSLSQLParser#uid}.
	 * @param ctx the parse tree
	 */
	void exitUid(DSLSQLParser.UidContext ctx);
}