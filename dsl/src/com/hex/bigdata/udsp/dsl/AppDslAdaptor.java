package com.hex.bigdata.udsp.dsl;

import com.hex.bigdata.udsp.common.api.model.Page;
import com.hex.bigdata.udsp.dsl.parser.APPDSLLexer;
import com.hex.bigdata.udsp.dsl.parser.APPDSLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APPDSL的适配器
 */
public class AppDslAdaptor {

    public static APPDSLParser getAPPDSLParser(String sql) throws RuntimeException {
        // 新建一个输入流，从标准输入读取数据
        ANTLRInputStream input = new ANTLRInputStream (sql);
        // 新建一个词法分析器，处理输入的字符流
        APPDSLLexer lexer = new APPDSLLexer (input);
        lexer.addErrorListener (DescriptiveErrorListener.INSTANCE); // 设置错误处理的监听类
        // 新建一个词法符号的缓冲区，用于储存词法分析器将生成的词法符号
        CommonTokenStream tokens = new CommonTokenStream (lexer);
        // 新建一个语法分析器，处理词法符号缓冲区中的内容
        APPDSLParser parser = new APPDSLParser (tokens);
//        parser.setErrorHandler(new BailErrorStrategy ()); // 设置错误处理的策略类
        parser.addErrorListener (DescriptiveErrorListener.INSTANCE); // 设置错误处理的监听类
        return parser;
    }

    public static String textLiteralToValue(String textLiteral) {
        return textLiteral.substring (1, textLiteral.length () - 1); // 去除首尾的单引号
    }

    public static String textLiteralContextToValue(APPDSLParser.TextLiteralContext textLiteralContext) {
        return textLiteralToValue (textLiteralContext.getText ());
    }

    public static String valueContextToValue(APPDSLParser.ValueContext valueContext) {
        String value = null;
        APPDSLParser.TextLiteralContext textLiteralContext = valueContext.textLiteral ();
        if (textLiteralContext != null) {
            value = textLiteralContextToValue (textLiteralContext);
        } else {
            value = valueContext.decimalLiteral ().getText ();
        }
        return value;
    }

    public static Map<String, String> whereClauseContextToData(APPDSLParser.WhereClauseContext whereClauseContext) {
        Map<String, String> data = new HashMap<> ();
        APPDSLParser.LogicExpressionsContext logicExpressionsContext = whereClauseContext.logicExpressions ();
        List<APPDSLParser.LogicExpressionContext> logicExpressionContexts = logicExpressionsContext.logicExpression ();
        for (APPDSLParser.LogicExpressionContext logicExpressionContext : logicExpressionContexts) {
            APPDSLParser.FullColumnNameContext fullColumnNameContext = logicExpressionContext.fullColumnName ();
            APPDSLParser.ValueContext valueContext = logicExpressionContext.value ();
            String name = fullColumnNameContext.getText ();
            String value = valueContextToValue (valueContext);
            data.put (name, value);
        }
        return data;
    }

    public static Page limitClauseContextToPage(APPDSLParser.LimitClauseContext limitClauseContext) {
        Page page = new Page ();
        int offset = 0;
        int limit = 1;
        APPDSLParser.DecimalLiteralContext limitContext = limitClauseContext.limit;
        APPDSLParser.DecimalLiteralContext offsetContext = limitClauseContext.offset;
        if (limitContext != null) {
            limit = Integer.valueOf (limitContext.getText ());
        }
        if (offsetContext != null) {
            offset = Integer.valueOf (offsetContext.getText ());
        }
        int pageIndex = offset / limit + 1;
        int pageSize = limit;
        page.setPageIndex (pageIndex);
        page.setPageSize (pageSize);
        return page;
    }
}
