/*
 * �쐬��: 2004/06/14
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package org.F11.scada.tool.page.trend;

import org.apache.struts.validator.ValidatorForm;

/**
 * @author hori
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class PropertyForm extends ValidatorForm {
	private int pos;
	private String color;
	private int point;
	private String unit;
	private String name;
	private String unit_mark;
	private double minimums;
	private double maximums;
	private double inputminimums;
	private double inputmaximums;
//	private String provider;
	private String holder;
	
	public PropertyForm() {
	}

	/**
	 * @return
	 */
	public String getColor() {
		return color;
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
	 * @param string
	 */
	public void setColor(String string) {
		color = string;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
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

	/**
	 * @return
	 */
	public String getHolder() {
		return holder;
	}

	/**
	 * @return
	 */
//	public String getProvider() {
//		return provider;
//	}

	/**
	 * @param string
	 */
	public void setHolder(String string) {
		holder = string;
	}

	/**
	 * @param string
	 */
//	public void setProvider(String string) {
//		provider = string;
//	}

	/**
	 * @return
	 */
	public int getPoint() {
		return point;
	}

	/**
	 * @param i
	 */
	public void setPoint(int i) {
		point = i;
	}

	/**
	 * @return
	 */
	public double getInputmaximums() {
		return inputmaximums;
	}

	/**
	 * @return
	 */
	public double getInputminimums() {
		return inputminimums;
	}

	/**
	 * @return
	 */
	public double getMaximums() {
		return maximums;
	}

	/**
	 * @return
	 */
	public double getMinimums() {
		return minimums;
	}

	/**
	 * @param d
	 */
	public void setInputmaximums(double d) {
		inputmaximums = d;
	}

	/**
	 * @param d
	 */
	public void setInputminimums(double d) {
		inputminimums = d;
	}

	/**
	 * @param d
	 */
	public void setMaximums(double d) {
		maximums = d;
	}

	/**
	 * @param d
	 */
	public void setMinimums(double d) {
		minimums = d;
	}

	/**
	 * @return
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * @param i
	 */
	public void setPos(int i) {
		pos = i;
	}

}
