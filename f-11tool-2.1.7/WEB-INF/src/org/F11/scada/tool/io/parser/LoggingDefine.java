/*
 * 作成日: 2005/10/27
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.io.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * @author Administrator
 * 
 * TODO この生成された型コメントのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java - コード・スタイル -
 * コード・テンプレート
 */
public class LoggingDefine {
    private static final String DEFAULT_DEFINE_FILE = "/resources/Logging.xml";

    private Logging logging;

    /**
     *  
     */
    public LoggingDefine() throws IOException, SAXException {
        this(DEFAULT_DEFINE_FILE);
    }

    public LoggingDefine(String definefile) throws IOException, SAXException {
        Digester digester = new Digester();
        digester.push(this);

        addPageRule(digester);

        URL xml = getClass().getResource(definefile);
        if (xml == null) {
            throw new IllegalStateException(definefile + " not found.");
        }
        InputStream is = null;
        try {
            is = xml.openStream();
            digester.parse(is);
            is.close();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void addPageRule(Digester digester) {
        digester.addObjectCreate("f11:logging", Logging.class);
        digester.addSetNext("f11:logging", "setLogging");

        digester.addObjectCreate("f11:logging/f11:task", Task.class);
        digester.addSetNext("f11:logging/f11:task", "addTask");
        digester.addSetProperties("f11:logging/f11:task");

        digester.addObjectCreate("f11:logging/f11:task/f11:column",
                Column.class);
        digester.addSetNext("f11:logging/f11:task/f11:column", "addColumn");
        digester.addSetProperties("f11:logging/f11:task/f11:column");
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
    }

    public Collection getTaskColumns(String taskname) {
        if (null != logging && logging.containsKey(taskname)) {
            Task task = logging.getTask(taskname);
            return task.getColumns();
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}