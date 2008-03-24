/*
 * 作成日: 2005/10/27
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.io.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * 
 * TODO この生成された型コメントのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java - コード・スタイル -
 * コード・テンプレート
 */
public class Logging {
    private Map tasks = new HashMap();

    public void addTask(Task task) {
        tasks.put(task.getName(), task);
    }

    public Task getTask(String name) {
        return (Task) tasks.get(name);
    }

    public boolean containsKey(String name) {
    	return tasks.containsKey(name);
    }
}