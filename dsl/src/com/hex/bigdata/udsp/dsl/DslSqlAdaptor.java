package com.hex.bigdata.udsp.dsl;


import com.hex.bigdata.udsp.dsl.constant.AggregateFunction;
import com.hex.bigdata.udsp.dsl.constant.ComparisonOperator;
import com.hex.bigdata.udsp.dsl.constant.LogicalOperator;
import com.hex.bigdata.udsp.dsl.constant.OrderExpression;
import com.hex.bigdata.udsp.dsl.model.*;
import com.hex.bigdata.udsp.dsl.parser.DSLSQLLexer;
import com.hex.bigdata.udsp.dsl.parser.DSLSQLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DSLSQL的适配器
 */
public class DslSqlAdaptor {

    public static DSLSQLParser getDSLSQLParser(String sql) throws RuntimeException {
        // 新建一个输入流，从标准输入读取数据
        ANTLRInputStream input = new ANTLRInputStream (sql);
        // 新建一个词法分析器，处理输入的字符流
        DSLSQLLexer lexer = new DSLSQLLexer (input);
        lexer.addErrorListener (DescriptiveErrorListener.INSTANCE); // 设置错误处理的监听类
        // 新建一个词法符号的缓冲区，用于储存词法分析器将生成的词法符号
        CommonTokenStream tokens = new CommonTokenStream (lexer);
        // 新建一个语法分析器，处理词法符号缓冲区中的内容
        DSLSQLParser parser = new DSLSQLParser (tokens);
//        parser.setErrorHandler(new BailErrorStrategy ()); // 设置错误处理的策略类
        parser.addErrorListener (DescriptiveErrorListener.INSTANCE); // 设置错误处理的监听类
        return parser;
    }

    private static String textLiteralToValue(String textLiteral) {
        return textLiteral.substring (1, textLiteral.length () - 1); // 去除首尾的单引号
    }

    public static String textLiteralContextToValue(DSLSQLParser.TextLiteralContext textLiteralContext) {
        return textLiteralToValue (textLiteralContext.getText ());
    }

    private static String valueContextToValue(DSLSQLParser.ValueContext valueContext) {
        String value = null;
        DSLSQLParser.TextLiteralContext textLiteralContext = valueContext.textLiteral ();
        if (textLiteralContext != null) {
            value = textLiteralContextToValue (textLiteralContext);
        } else {
            value = valueContext.decimalLiteral ().getText ();
        }
        return value;
    }

    private static ComparisonOperator transComparisonOperator(String operator) {
        operator = operator.toUpperCase ();
        ComparisonOperator comparison = ComparisonOperator.EQ;
        if (ComparisonOperator.EQ.getValue ().equals (operator)) {
            comparison = ComparisonOperator.EQ;
        } else if (ComparisonOperator.NE.getValue ().equals (operator)) {
            comparison = ComparisonOperator.NE;
        } else if (ComparisonOperator.GE.getValue ().equals (operator)) {
            comparison = ComparisonOperator.GE;
        } else if (ComparisonOperator.LE.getValue ().equals (operator)) {
            comparison = ComparisonOperator.LE;
        } else if (ComparisonOperator.GT.getValue ().equals (operator)) {
            comparison = ComparisonOperator.GT;
        } else if (ComparisonOperator.LT.getValue ().equals (operator)) {
            comparison = ComparisonOperator.LT;
        } else if (ComparisonOperator.LIKE.getValue ().equals (operator)) {
            comparison = ComparisonOperator.LIKE;
        } else if (ComparisonOperator.IN.getValue ().equals (operator)) {
            comparison = ComparisonOperator.IN;
        } else if (ComparisonOperator.NOT_IN.getValue ().equals (operator)) {
            comparison = ComparisonOperator.NOT_IN;
        } else if (ComparisonOperator.BETWEEN_AND.getValue ().equals (operator)) {
            comparison = ComparisonOperator.BETWEEN_AND;
        }
        return comparison;
    }

    private static OrderExpression transOrderExpression(String order) {
        OrderExpression expression = OrderExpression.ASC;
        if (OrderExpression.DESC.getValue ().equals (order)) {
            expression = OrderExpression.DESC;
        }
        return expression;
    }

    /**
     * DSLSQLParser.LogicExpressionsContext 转 Component
     *
     * @param logicExpressionsContext
     * @return
     */
    public static Component logicExpressionsContextToComponent(DSLSQLParser.LogicExpressionsContext logicExpressionsContext) {
        int count = logicExpressionsContext.getChildCount ();
        // 拆分OR逻辑操作
        Map<Integer, Integer> map = new HashMap<> ();
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < count; i++) {
            if (i % 2 == 1) { // 逻辑操作符
                DSLSQLParser.LogicalOperatorContext logicalOperatorContext = (DSLSQLParser.LogicalOperatorContext) logicExpressionsContext.getChild (i);
                if (logicalOperatorContext.OR () != null) {
                    endIndex = i - 1;
                    map.put (startIndex, endIndex);
                    startIndex = i + 1;
                }
            }
        }
        map.put (startIndex, count - 1);
        // 处理每个拆分的逻辑操作
        List<Component> components = new ArrayList<> ();
        for (Map.Entry<Integer, Integer> entry : map.entrySet ()) {
            Component component = logicExpressionsContextToComponent (logicExpressionsContext, entry.getKey (), entry.getValue ());
            components.add (component);
        }
        if (components.size () == 1) {
            return components.get (0);
        } else {
            return new Composite (LogicalOperator.OR, components);
        }
    }

    private static Component logicExpressionsContextToComponent(DSLSQLParser.LogicExpressionsContext logicExpressionsContext, int startIndex, int endIndex) {
        List<Component> components = new ArrayList<> ();
        for (int i = startIndex; i <= endIndex; i++) {
            if (i % 2 == 0) { // 逻辑操作
                DSLSQLParser.LogicExpressionContext logicExpressionContext = (DSLSQLParser.LogicExpressionContext) logicExpressionsContext.getChild (i);
                Component component = logicExpressionContextToComponent (logicExpressionContext);
                components.add (component);
            }
        }
        if (components.size () == 1) {
            return components.get (0);
        } else {
            return new Composite (LogicalOperator.AND, components);
        }
    }

    /**
     * DSLSQLParser.LogicExpressionContext 转 Component
     *
     * @param logicExpressionContext
     * @return
     */
    private static Component logicExpressionContextToComponent(DSLSQLParser.LogicExpressionContext logicExpressionContext) {
        Dimension dimension = null;
        ComparisonOperator comparison = null;
        List<String> values = new ArrayList<> ();
        DSLSQLParser.LogicExpressionsContext logicExpressionsContext = logicExpressionContext.logicExpressions ();
        if (logicExpressionsContext != null) { // '(' logicExpressions ')'
            return logicExpressionsContextToComponent (logicExpressionsContext);
        } else { // 非'(' logicExpressions ')'
            String name = logicExpressionContext.fullColumnName ().getText ();
            List<DSLSQLParser.ValueContext> valueContexts = logicExpressionContext.value ();
            if (logicExpressionContext.comparisonOperator () != null) { // fullColumnName comparisonOperator value
                String operator = logicExpressionContext.comparisonOperator ().getText ();
                comparison = transComparisonOperator (operator);
                DSLSQLParser.ValueContext valueContext = valueContexts.get (0);
                values.add (valueContextToValue (valueContext));
                dimension = new Dimension (name, comparison, values);
            } else if (logicExpressionContext.BETWEEN () != null && logicExpressionContext.AND () != null) { // fullColumnName BETWEEN value AND value
                DSLSQLParser.ValueContext startValueContext = valueContexts.get (0);
                DSLSQLParser.ValueContext endValueContext = valueContexts.get (1);
                values.add (valueContextToValue (startValueContext));
                values.add (valueContextToValue (endValueContext));
                dimension = new Dimension (name, ComparisonOperator.BETWEEN_AND, values);
            } else if (logicExpressionContext.IN () != null) { // fullColumnName NOT? IN '(' value (',' value)*  ')'
                for (DSLSQLParser.ValueContext valueContext : valueContexts) {
                    values.add (valueContextToValue (valueContext));
                }
                if (logicExpressionContext.NOT () != null) { // NOT IN
                    comparison = ComparisonOperator.NOT_IN;
                } else { // IN
                    comparison = ComparisonOperator.IN;
                }
                dimension = new Dimension (name, comparison, values);
            }
        }
        return dimension;
    }

    public static Limit limitClauseContextToLimit(DSLSQLParser.LimitClauseContext limitClauseContext) {
        int offset = 0;
        int limit = 1;
        DSLSQLParser.DecimalLiteralContext limitContext = limitClauseContext.limit;
        DSLSQLParser.DecimalLiteralContext offsetContext = limitClauseContext.offset;
        if (limitContext != null) {
            limit = Integer.valueOf (limitContext.getText ());
        }
        if (offsetContext != null) {
            offset = Integer.valueOf (offsetContext.getText ());
        }
        return new Limit (limit, offset);
    }

    public static List<String> groupByCaluseContextToGroupBy(DSLSQLParser.GroupByCaluseContext groupByCaluseContext) {
        List<String> list = new ArrayList<> ();
        List<DSLSQLParser.GroupByItemContext> groupByItemContexts = groupByCaluseContext.groupByItem ();
        for (DSLSQLParser.GroupByItemContext groupByItemContext : groupByItemContexts) {
            list.add (groupByItemContext.fullColumnName ().columnName ().getText ());
        }
        return list;
    }

    public static List<Order> orderByClauseContextToOrderBy(DSLSQLParser.OrderByClauseContext orderByClauseContext) {
        List<Order> list = new ArrayList<> ();
        List<DSLSQLParser.OrderByExpressionContext> orderByExpressionContexts = orderByClauseContext.orderByExpression ();
        for (DSLSQLParser.OrderByExpressionContext orderByExpressionContext : orderByExpressionContexts) {
            String name = orderByExpressionContext.fullColumnName ().columnName ().getText ();
            OrderExpression expression = OrderExpression.ASC;
            if (orderByExpressionContext.order != null) {
                expression = transOrderExpression (orderByExpressionContext.order.getText ());
            }
            list.add (new Order (name, expression));
        }
        return list;
    }

    public static List<Column> selectElementsContextToSelect(DSLSQLParser.SelectElementsContext selectElementsContext) {
        List<Column> list = new ArrayList<> ();
        if (selectElementsContext.star != null) {
            list.add (new Column ("*"));
        } else {
            List<DSLSQLParser.SelectElementContext> selectElementContexts = selectElementsContext.selectElement ();
            for (DSLSQLParser.SelectElementContext selectElementContext : selectElementContexts) {
                String alias = null;
                if (selectElementContext.AS () != null || selectElementContext.uid () != null) {
                    alias = selectElementContext.uid ().getText ();
                }
                DSLSQLParser.FullColumnNameContext fullColumnNameContext = selectElementContext.fullColumnName ();
                Aggregate aggregate = null;
                if (fullColumnNameContext != null) {
                    String name = fullColumnNameContext.columnName ().getText ();
                    aggregate = new Aggregate (name);
                } else {
                    DSLSQLParser.AggregateWindowedFunctionContext aggregateWindowedFunctionContext =
                            selectElementContext.functionCall ().aggregateWindowedFunction ();
                    String name = null;
                    AggregateFunction function = null;
                    if (aggregateWindowedFunctionContext.functionArg () != null) {
                        name = aggregateWindowedFunctionContext.functionArg ().columnName ().getText ();
                    }
                    if (aggregateWindowedFunctionContext.AVG () != null) { // AVG
                        function = AggregateFunction.AVG;
                    } else if (aggregateWindowedFunctionContext.MAX () != null) { // MAX
                        function = AggregateFunction.MAX;
                    } else if (aggregateWindowedFunctionContext.MIN () != null) { // MIN
                        function = AggregateFunction.MIN;
                    } else if (aggregateWindowedFunctionContext.SUM () != null) { // SUM
                        function = AggregateFunction.SUM;
                    } else if (aggregateWindowedFunctionContext.COUNT () != null) {
                        if (aggregateWindowedFunctionContext.DISTINCT () != null) { // COUNT_DISTINCT
                            function = AggregateFunction.COUNT_DISTINCT;
                        } else { // COUNT
                            if (aggregateWindowedFunctionContext.starArg != null) {
                                name = "*";
                            }
                            function = AggregateFunction.COUNT;
                        }
                    }
                    aggregate = new Aggregate (name, function);
                }
                list.add (new Column (alias, aggregate));
            }
        }
        return list;
    }

    /**
     * select SQL 解析成 DslRequest对象
     *
     * @param sql
     * @return
     */
    public static DslRequest selectSqlToDslRequest(String sql) {
        DSLSQLParser parser = DslSqlAdaptor.getDSLSQLParser (sql);
        DSLSQLParser.SelectStatementContext selectStatementContext = parser.selectStatement ();
        // serviceName
        DSLSQLParser.ServiceNameContext serviceNameContext = selectStatementContext.serviceName ();
        String serviceName = serviceNameContext.getText ();
        // select
        List<Column> select = null;
        DSLSQLParser.SelectElementsContext selectElementsContext = selectStatementContext.selectElements ();
        if (selectElementsContext != null) {
            select = DslSqlAdaptor.selectElementsContextToSelect (selectElementsContext);
        }
        // where
        Component where = null;
        DSLSQLParser.WhereClauseContext whereClauseContext = selectStatementContext.whereClause ();
        if (whereClauseContext != null) {
            DSLSQLParser.LogicExpressionsContext logicExpressionsContext = whereClauseContext.logicExpressions ();
            where = DslSqlAdaptor.logicExpressionsContextToComponent (logicExpressionsContext);
        }
        // group by
        List<String> groupBy = null;
        DSLSQLParser.GroupByCaluseContext groupByCaluseContext = selectStatementContext.groupByCaluse ();
        if (groupByCaluseContext != null) {
            groupBy = DslSqlAdaptor.groupByCaluseContextToGroupBy (groupByCaluseContext);
        }
        // order by
        List<Order> orderBy = null;
        DSLSQLParser.OrderByClauseContext orderByClauseContext = selectStatementContext.orderByClause ();
        if (orderByClauseContext != null) {
            orderBy = DslSqlAdaptor.orderByClauseContextToOrderBy (orderByClauseContext);
        }
        // limit
        Limit limit = null;
        DSLSQLParser.LimitClauseContext limitClauseContext = selectStatementContext.limitClause ();
        if (limitClauseContext != null) {
            limit = DslSqlAdaptor.limitClauseContextToLimit (limitClauseContext);
        }

        DslRequest dslRequest = new DslRequest ();
        dslRequest.setName (serviceName);
        dslRequest.setSelect (select);
        dslRequest.setWhere (where);
        dslRequest.setGroupBy (groupBy);
        dslRequest.setOrderBy (orderBy);
        dslRequest.setLimit (limit);

        return dslRequest;
    }
}
