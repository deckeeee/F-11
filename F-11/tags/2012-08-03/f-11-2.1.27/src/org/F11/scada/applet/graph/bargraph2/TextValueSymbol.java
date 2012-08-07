/*
 * 作成日: 2008/08/11 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.applet.graph.bargraph2;

import org.F11.scada.applet.symbol.SymbolProperty;
import org.F11.scada.applet.symbol.TextSymbol;

public class TextValueSymbol extends TextSymbol {
	private static final long serialVersionUID = -8022215208025151868L;
	private String value;

	public TextValueSymbol(SymbolProperty property) {
		super(property);
	}

	public void setTextValue(String value) {
		this.value = value;
	}

	/**
	 * 文字列を設定します。
	 */
	protected void setFormatedString() {
		this.setText(value);
		setAlign();
	}

}
