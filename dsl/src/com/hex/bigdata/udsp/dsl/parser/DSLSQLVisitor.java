// Generated from B:/workspaces/IntellijIdeaGithubWorkspaces/github/boracay/dsl/resource\DSLSQL.g4 by ANTLR 4.7.2

    package com.hex.bigdata.udsp.dsl.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DSLSQLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DSLSQLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(DSLSQLParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#describeServiceStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDescribeServiceStatement(DSLSQLParser.DescribeServiceStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#showServicesStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShowServicesStatement(DSLSQLParser.ShowServicesStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#selectStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectStatement(DSLSQLParser.SelectStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#selectElements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectElements(DSLSQLParser.SelectElementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#whereClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhereClause(DSLSQLParser.WhereClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#logicExpressions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicExpressions(DSLSQLParser.LogicExpressionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#logicExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicExpression(DSLSQLParser.LogicExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#groupByCaluse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupByCaluse(DSLSQLParser.GroupByCaluseContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#orderByClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrderByClause(DSLSQLParser.OrderByClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#limitClause}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimitClause(DSLSQLParser.LimitClauseContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#orderByExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrderByExpression(DSLSQLParser.OrderByExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#groupByItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupByItem(DSLSQLParser.GroupByItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#logicalOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalOperator(DSLSQLParser.LogicalOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(DSLSQLParser.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(DSLSQLParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#decimalLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimalLiteral(DSLSQLParser.DecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#textLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTextLiteral(DSLSQLParser.TextLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#selectElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectElement(DSLSQLParser.SelectElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#fullColumnName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFullColumnName(DSLSQLParser.FullColumnNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(DSLSQLParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#aggregateWindowedFunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggregateWindowedFunction(DSLSQLParser.AggregateWindowedFunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#functionArg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArg(DSLSQLParser.FunctionArgContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#functionArgs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionArgs(DSLSQLParser.FunctionArgsContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#serviceName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitServiceName(DSLSQLParser.ServiceNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#columnName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumnName(DSLSQLParser.ColumnNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link DSLSQLParser#uid}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUid(DSLSQLParser.UidContext ctx);
}