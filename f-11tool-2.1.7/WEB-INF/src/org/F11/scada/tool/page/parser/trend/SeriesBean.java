/*
 * �쐬��: 2004/06/14
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package org.F11.scada.tool.page.parser.trend;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.F11.scada.tool.page.trend.PropertyForm;
import org.apache.struts.validator.DynaValidatorForm;

/**
 * @author hori
 * 
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽�� �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public class SeriesBean implements Serializable {
	private static final long serialVersionUID = 7493063705281835112L;
	/** �n���h���� */
	private String handlerName;
	/** �O���[�vID */
	private int Id;
	/** �O���[�v���� */
	private String seriesName;
	/** �O���[�v�T�C�Y */
	private int seriesSize;
	/** �v���p�e�B���X�g */
	private List propertyList;

	/**
	 * @return
	 */
	public int getId() {
		return Id;
	}

	/**
	 * @param i
	 */
	public void setId(int i) {
		Id = i;
	}

	/**
	 * @return
	 */
	public String getSeriesName() {
		return seriesName;
	}

	/**
	 * @return
	 */
	public int getSeriesSize() {
		return seriesSize;
	}

	/**
	 * @param string
	 */
	public void setSeriesName(String string) {
		seriesName = string;
	}

	/**
	 * @param i
	 */
	public void setSeriesSize(int i) {
		seriesSize = i;
	}

	/**
	 * @return
	 */
	public String getHandlerName() {
		return handlerName;
	}

	/**
	 * @param string
	 */
	public void setHandlerName(String string) {
		handlerName = string;
	}

	/**
	 * @return
	 */
	public List getPropertyList() {
		return propertyList;
	}

	/**
	 * @param list
	 */
	public void setPropertyList(List list) {
		propertyList = list;
	}

	public void setValues(DynaValidatorForm actionForm) {
		Integer[] point = (Integer[]) actionForm.get("point");
		Double[] minimums = (Double[]) actionForm.get("minimums");
		Double[] maximums = (Double[]) actionForm.get("maximums");
		Double[] inputminimums = (Double[]) actionForm.get("inputminimums");
		Double[] inputmaximums = (Double[]) actionForm.get("inputmaximums");
		// String[] provider = (String[]) actionForm.get("provider");
		String[] holder = (String[]) actionForm.get("holder");
		Iterator it = propertyList.iterator();
		for (int i = 0; i < point.length && it.hasNext() && i < seriesSize; i++) {
			PropertyForm property = (PropertyForm) it.next();
			property.setPoint(point[i].intValue());
			property.setMinimums(minimums[i].doubleValue());
			property.setMaximums(maximums[i].doubleValue());
			property.setInputminimums(inputminimums[i].doubleValue());
			property.setInputmaximums(inputmaximums[i].doubleValue());
			// property.setProvider(provider[i]);
			property.setHolder(holder[i]);
		}
	}
}
