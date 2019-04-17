// Generated from B:/workspaces/IntellijIdeaGithubWorkspaces/github/boracay/dsl/resource\APPDSL.g4 by ANTLR 4.7.2

    package com.hex.bigdata.udsp.dsl.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link APPDSLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface APPDSLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(APPDSLParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#describeServiceStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDescribeServiceStatement(APPDSLParser.DescribeServiceStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#showServicesStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShowServicesStatement(APPDSLParser.ShowServicesStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#selectStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectStatement(APPDSLParser.SelectStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#selectElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectElements(APPDSLParser.SelectElementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#whereClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereClause(APPDSLParser.WhereClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#logicExpressions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicExpressions(APPDSLParser.LogicExpressionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#logicExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicExpression(APPDSLParser.LogicExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#limitClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimitClause(APPDSLParser.LimitClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#logicalOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOperator(APPDSLParser.LogicalOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(APPDSLParser.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(APPDSLParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#decimalLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimalLiteral(APPDSLParser.DecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#textLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextLiteral(APPDSLParser.TextLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#selectElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectElement(APPDSLParser.SelectElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#aggregateWindowedFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregateWindowedFunction(APPDSLParser.AggregateWindowedFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#functionArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArg(APPDSLParser.FunctionArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#fullColumnName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFullColumnName(APPDSLParser.FullColumnNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(APPDSLParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#serviceName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitServiceName(APPDSLParser.ServiceNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#columnName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumnName(APPDSLParser.ColumnNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link APPDSLParser#uid}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUid(APPDSLParser.UidContext ctx);
}