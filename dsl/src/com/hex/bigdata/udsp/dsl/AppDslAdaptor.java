package com.hex.bigdata.udsp.dsl;

import com.hex.bigdata.udsp.dsl.parser.APPDSLLexer;
import com.hex.bigdata.udsp.dsl.parser.APPDSLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * Created by JunjieM on 2019-1-2.
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

}
