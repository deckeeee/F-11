/*
 * 作成日: 2005/09/21 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.emailgroup.attribute;

/**
 * @author hori TODO この生成された型コメントのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 *         コード・スタイル - コード・テンプレート
 */
public class EmailAttributeBean {
	private Integer attribute_id;
	private String attribute_name;
	private String email_address;

	public Integer getAttribute_id() {
		if (attribute_id == null)
			attribute_id = new Integer(0);
		return attribute_id;
	}
	public void setAttribute_id(Integer attribute_id) {
		this.attribute_id = attribute_id;
	}
	public String getAttribute_name() {
		if (attribute_name == null)
			attribute_name = "";
		return attribute_name;
	}
	public void setAttribute_name(String attribute_name) {
		this.attribute_name = attribute_name;
	}
	public String getEmail_address() {
		if (email_address == null)
			email_address = "";
		return email_address;
	}
	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}
}
