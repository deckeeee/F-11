/*
 * 作成日: 2008/02/15 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.server.formula.dto;

public class ItemFormulaDto {
	public static final String TABLE = "item_formula_table";

	private Long id;
	private String holder;
	private String formula;

	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getHolder() {
		return holder;
	}
	public void setHolder(String holder) {
		this.holder = holder;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		return "id=" + id + ", holder=" + holder + ", formula=" + formula;
	}

}
