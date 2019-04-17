package com.hex.bigdata.udsp.dsl;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * 错误描述的监听器
 */
public class DescriptiveErrorListener extends BaseErrorListener {

    public static DescriptiveErrorListener INSTANCE = new DescriptiveErrorListener ();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e) {
        String sourceInfo = "";
        String sourceName = recognizer.getInputStream ().getSourceName ();
        if (sourceName != null && !"".equals (sourceName.trim ()) && !"<unknown>".equals (sourceName)) {
            sourceInfo = String.format ("%s:%d:%d: ", sourceName, line, charPositionInLine);
        }
        //System.err.println (sourceInfo + "line " + line + ":" + charPositionInLine + " " + msg);
        throw new RuntimeException (sourceInfo + "line " + line + ":" + charPositionInLine + " " + msg);
    }
}