/*
 * �쐬��: 2004/06/25
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package org.F11.scada.tool.page.bar;

import org.apache.struts.validator.ValidatorForm;

/**
 * @author hori
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class BarPointForm extends ValidatorForm {
	/** �|�C���g */
	private int point;
	private String unit;
	private String name;
	private String unit_mark;
	private long minimums;
	private long maximums;
	
	public void setValues(BarPointForm form) {
		this.point = form.point;
		this.unit = form.unit;
		this.name = form.name;
		this.unit_mark = form.unit_mark;
		this.minimums = form.minimums;
		this.maximums = form.maximums;
	}
	
	/**
	 * @return
	 */
	public long getMaximums() {
		return maximums;
	}

	/**
	 * @return
	 */
	public long getMinimums() {
		return minimums;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public int getPoint() {
		return point;
	}

	/**
	 * @return
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @return
	 */
	public String getUnit_mark() {
		return unit_mark;
	}

	/**
	 * @param l
	 */
	public void setMaximums(long l) {
		maximums = l;
	}

	/**
	 * @param l
	 */
	public void setMinimums(long l) {
		minimums = l;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param i
	 */
	public void setPoint(int i) {
		point = i;
	}

	/**
	 * @param string
	 */
	public void setUnit(String string) {
		unit = string;
	}

	/**
	 * @param string
	 */
	public void setUnit_mark(String string) {
		unit_mark = string;
	}

}
