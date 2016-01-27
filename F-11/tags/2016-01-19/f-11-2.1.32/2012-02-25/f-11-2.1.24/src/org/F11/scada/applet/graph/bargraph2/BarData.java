/*
 * 作成日: 2008/08/11 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.applet.graph.bargraph2;

import java.util.Date;

import org.apache.commons.collections.primitives.DoubleList;

public class BarData {
	private Date date;
	private DoubleList values;

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getValue(int colmun) {
		return values.get(colmun);
	}
	public void setValues(DoubleList values) {
		this.values = values;
	}

}
