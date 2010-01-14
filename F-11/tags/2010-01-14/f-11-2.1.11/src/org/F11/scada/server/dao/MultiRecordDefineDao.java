/*
 * 作成日: 2005/08/19
 *
 * TODO この生成されたファイルのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
package org.F11.scada.server.dao;

import org.F11.scada.server.entity.MultiRecordDefine;

/**
 * @author hori
 *
 * TODO この生成された型コメントのテンプレートを変更するには次へジャンプ:
 * ウィンドウ - 設定 - Java - コード・スタイル - コード・テンプレート
 */
public interface MultiRecordDefineDao {
	public Class BEAN = MultiRecordDefine.class;
	public MultiRecordDefine getMultiRecordDefine(String loggingTableName);
    public static final String getMultiRecordDefine_QUERY = "logging_table_name = ?";
}
