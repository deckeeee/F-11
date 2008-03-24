/*
 * 作成日: 2005/09/21 TODO この生成されたファイルのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 * コード・スタイル - コード・テンプレート
 */
package org.F11.scada.tool.emailgroup.individual;

/**
 * @author hori TODO この生成された型コメントのテンプレートを変更するには次へジャンプ: ウィンドウ - 設定 - Java -
 *         コード・スタイル - コード・テンプレート
 */
public class EmailIndividualBean {
	private Integer point;
	private String provider;
	private String holder;
	private String p_unit;
	private String p_name;
	private String p_attrname;
	private String email_address;

	public Integer getPoint() {
		if (point == null)
			point = new Integer(0);
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	public String getHolder() {
		if (holder == null)
			holder = "";
		return holder;
	}
	public void setHolder(String holder) {
		this.holder = holder;
	}
	public String getProvider() {
		if (provider == null)
			provider = "";
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getP_name() {
		if (p_name == null)
			p_name = "";
		return p_name;
	}
	public void setP_name(String p_name) {
		this.p_name = p_name;
	}
	public String getP_unit() {
		if (p_unit == null)
			p_unit = "";
		return p_unit;
	}
	public void setP_unit(String p_unit) {
		this.p_unit = p_unit;
	}
	public String getP_attrname() {
		if (p_attrname == null)
			p_attrname = "";
		return p_attrname;
	}
	public void setP_attrname(String p_attrname) {
		this.p_attrname = p_attrname;
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
