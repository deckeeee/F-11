/*
 * �쐬��: 2008/08/11 TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 * �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
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
	 * �������ݒ肵�܂��B
	 */
	protected void setFormatedString() {
		this.setText(value);
		setAlign();
	}

}
