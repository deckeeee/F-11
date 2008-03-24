/*
 * 作成日: 2005/10/27
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.io.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Administrator
 * 
 * TODO この生成された型コメントのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java - コード・スタイル -
 * コード・テンプレート
 */
public class Task {
    private List columns = new ArrayList();
    private String name;

    /**
     *  
     */
    public Task() {
        super();
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public Collection getColumns() {
        return columns;
    }

    /**
     * @return name を戻します。
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            name を設定。
     */
    public void setName(String name) {
        this.name = name;
    }
}