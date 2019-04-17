// Generated from B:/workspaces/IntellijIdeaGithubWorkspaces/github/boracay/dsl/resource\APPDSL.g4 by ANTLR 4.7.2

    package com.hex.bigdata.udsp.dsl.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link APPDSLParser}.
 */
public interface APPDSLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(APPDSLParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(APPDSLParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#describeServiceStatement}.
	 * @param ctx the parse tree
	 */
	void enterDescribeServiceStatement(APPDSLParser.DescribeServiceStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#describeServiceStatement}.
	 * @param ctx the parse tree
	 */
	void exitDescribeServiceStatement(APPDSLParser.DescribeServiceStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#showServicesStatement}.
	 * @param ctx the parse tree
	 */
	void enterShowServicesStatement(APPDSLParser.ShowServicesStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#showServicesStatement}.
	 * @param ctx the parse tree
	 */
	void exitShowServicesStatement(APPDSLParser.ShowServicesStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectStatement(APPDSLParser.SelectStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#selectStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectStatement(APPDSLParser.SelectStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#selectElements}.
	 * @param ctx the parse tree
	 */
	void enterSelectElements(APPDSLParser.SelectElementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#selectElements}.
	 * @param ctx the parse tree
	 */
	void exitSelectElements(APPDSLParser.SelectElementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void enterWhereClause(APPDSLParser.WhereClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void exitWhereClause(APPDSLParser.WhereClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#logicExpressions}.
	 * @param ctx the parse tree
	 */
	void enterLogicExpressions(APPDSLParser.LogicExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#logicExpressions}.
	 * @param ctx the parse tree
	 */
	void exitLogicExpressions(APPDSLParser.LogicExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#logicExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicExpression(APPDSLParser.LogicExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#logicExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicExpression(APPDSLParser.LogicExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#limitClause}.
	 * @param ctx the parse tree
	 */
	void enterLimitClause(APPDSLParser.LimitClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#limitClause}.
	 * @param ctx the parse tree
	 */
	void exitLimitClause(APPDSLParser.LimitClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOperator(APPDSLParser.LogicalOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#logicalOperator}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOperator(APPDSLParser.LogicalOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(APPDSLParser.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(APPDSLParser.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(APPDSLParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(APPDSLParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#decimalLiteral}.
	 * @param ctx the parse tree
	 */
	void enterDecimalLiteral(APPDSLParser.DecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#decimalLiteral}.
	 * @param ctx the parse tree
	 */
	void exitDecimalLiteral(APPDSLParser.DecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#textLiteral}.
	 * @param ctx the parse tree
	 */
	void enterTextLiteral(APPDSLParser.TextLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#textLiteral}.
	 * @param ctx the parse tree
	 */
	void exitTextLiteral(APPDSLParser.TextLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#selectElement}.
	 * @param ctx the parse tree
	 */
	void enterSelectElement(APPDSLParser.SelectElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#selectElement}.
	 * @param ctx the parse tree
	 */
	void exitSelectElement(APPDSLParser.SelectElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#aggregateWindowedFunction}.
	 * @param ctx the parse tree
	 */
	void enterAggregateWindowedFunction(APPDSLParser.AggregateWindowedFunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#aggregateWindowedFunction}.
	 * @param ctx the parse tree
	 */
	void exitAggregateWindowedFunction(APPDSLParser.AggregateWindowedFunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#functionArg}.
	 * @param ctx the parse tree
	 */
	void enterFunctionArg(APPDSLParser.FunctionArgContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#functionArg}.
	 * @param ctx the parse tree
	 */
	void exitFunctionArg(APPDSLParser.FunctionArgContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#fullColumnName}.
	 * @param ctx the parse tree
	 */
	void enterFullColumnName(APPDSLParser.FullColumnNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#fullColumnName}.
	 * @param ctx the parse tree
	 */
	void exitFullColumnName(APPDSLParser.FullColumnNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(APPDSLParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(APPDSLParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#serviceName}.
	 * @param ctx the parse tree
	 */
	void enterServiceName(APPDSLParser.ServiceNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#serviceName}.
	 * @param ctx the parse tree
	 */
	void exitServiceName(APPDSLParser.ServiceNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#columnName}.
	 * @param ctx the parse tree
	 */
	void enterColumnName(APPDSLParser.ColumnNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#columnName}.
	 * @param ctx the parse tree
	 */
	void exitColumnName(APPDSLParser.ColumnNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link APPDSLParser#uid}.
	 * @param ctx the parse tree
	 */
	void enterUid(APPDSLParser.UidContext ctx);
	/**
	 * Exit a parse tree produced by {@link APPDSLParser#uid}.
	 * @param ctx the parse tree
	 */
	void exitUid(APPDSLParser.UidContext ctx);
}