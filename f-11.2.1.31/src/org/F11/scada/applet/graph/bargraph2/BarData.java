/*
 * �쐬��: 2008/08/11 TODO ���̐������ꂽ�t�@�C���̃e���v���[�g��ύX����ɂ͎��փW�����v: �E�B���h�E - �ݒ� - Java -
 * �R�[�h�E�X�^�C�� - �R�[�h�E�e���v���[�g
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
