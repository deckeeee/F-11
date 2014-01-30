/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/DefaultGraphPropertyModel.java,v 1.16.2.7 2007/07/11 07:47:18 frdm Exp $
 * $Revision: 1.16.2.7 $
 * $Date: 2007/07/11 07:47:18 $
 * 
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.F11.scada.applet.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * �f�t�H���g�� GraphPropertyModel �����N���X�ł��B
 * 
 * @author Hideaki Maekawa <frdm@users.sourceforge.jp>
 */
public class DefaultGraphPropertyModel extends AbstractGraphPropertyModel {
	/** �c�X�P�[���̖ڐ���̐� */
	private int verticalScaleCount;
	/** �c�X�P�[���̖ڐ������̃s�N�Z���� */
	private int verticalScaleHeight;
	/** ���X�P�[���������̖ڐ���̐� */
	private int horizontalScaleCount;
	/** ���X�P�[���������̖ڐ������̎��ԁi�_�b�j */
	private long horizontalScaleWidth;
	/** �O���t�r���[�ȊO�R���|�[�l���g�̗]�������̃C���Z�b�c */
	private Insets insets;
	/** �O���t�r���[�R���|�[�l���g�̗]�������̃C���Z�b�c */
	private Insets graphiViewInsets;
	/** �O���t�E�}��� Color �I�u�W�F�N�g�̔z�� */
	private Color[] colors;
	/** ���݈ʒu�Â����Ă���O���[�v */
	private int group;
	/** ���݈ʒu�Â����Ă���n���h���[���C���f�b�N�X�i�g�p����n���h���[���C���f�b�N�X�j */
	private int handlerIndex;
	/** �ݒ肳��Ă���n���h���[�� */
	private String[] handlerName;
	/** �܂�Ԃ��� */
	private int foldCount;
	/** ���݃N���b�N����Ă���̎Q�Ɠ��� */
	private Timestamp referenceTime;
	/** �\�������O���t�̉��s�N�Z���� */
	private int horizontalPixcelWidth;
	/** X���X�P�[���̖ڐ��蕝 */
	private int scaleOneHeightPixel;
	/** �Q�ƕ\���̕����F */
	private Color explanatoryColor;
	/** �Q�ƕ\���̃t�H���g */
	private Font explanatoryFont;
	/**  1�i�ڂ̓����t�H�[�}�b�g������ */
	private String firstFormat;
	/**  2�i�ڂ̓����t�H�[�}�b�g������ */
	private String secondFormat;
	/** �c�X�P�[���̃v���p�e�B�[ */
	private VerticallyScaleProperty verticallyScaleProperty;

	/**
	 * �S�O���[�v�̃V���[�Y�v���p�e�B�E���X�g
	 * ���̃��X�g�ɂ́AGraphSeriesProperty ���i�[����Ă��܂��B
	 */
	private List seriesPropertyList;

	/**
	 * �R���X�g���N�^
	 */
	public DefaultGraphPropertyModel(
			int verticalScaleCount,
			int verticalScaleHeight,
			int horizontalScaleCount,
			long horizontalScaleWidth,
			Insets insets,
			Insets graphiViewInsets,
			Color[] colors,
			String[] handlerName,
			int foldCount,
			int horizontalPixcelWidth,
			int scaleOneHeightPixel,
			Color explanatoryColor,
			Font explanatoryFont,
			String firstFormat,
			String secondFormat,
			VerticallyScaleProperty verticallyScaleProperty
			) {

		super();
		
		this.verticalScaleCount = verticalScaleCount;
		this.verticalScaleHeight = verticalScaleHeight;
		this.horizontalScaleCount = horizontalScaleCount;
		this.horizontalScaleWidth = horizontalScaleWidth;
		this.insets = insets;
		this.graphiViewInsets = graphiViewInsets;
		this.colors = colors;
		this.handlerName = handlerName;
		this.foldCount = foldCount;
		this.horizontalPixcelWidth = horizontalPixcelWidth;
		this.scaleOneHeightPixel = scaleOneHeightPixel;
		if (explanatoryColor == null) {
			this.explanatoryColor = Color.BLACK;
		} else {
			this.explanatoryColor = explanatoryColor;
		}
		if (explanatoryFont == null) {
			this.explanatoryFont = new Font("dialog", Font.BOLD, 14);
		} else {
			this.explanatoryFont = explanatoryFont;
		}
		this.firstFormat = firstFormat;
		this.secondFormat = secondFormat;
		this.verticallyScaleProperty = verticallyScaleProperty; 
		
		seriesPropertyList = new ArrayList();
	}

	/**
	 * �C���X�^���X���f�B�[�v�R�s�[���܂��B
	 * @return GraphPropertyModel �f�B�[�v�R�s�[���ꂽ�C���X�^���X�B
	 */
	public GraphPropertyModel deepCopy() {
		return new DefaultGraphPropertyModel(this);
	}

	/**
	 * �R�s�[�R���X�g���N�^
	 * @param src �\�[�X�I�u�W�F�N�g
	 */
	private DefaultGraphPropertyModel(DefaultGraphPropertyModel src) {
		super();
		group = src.group;
		handlerIndex = src.handlerIndex;
		verticalScaleCount = src.verticalScaleCount;
		verticalScaleHeight = src.verticalScaleHeight;
		horizontalScaleCount = src.horizontalScaleCount;
		horizontalScaleWidth = src.horizontalScaleWidth;
		insets =
			new Insets(
				src.insets.top,
				src.insets.left,
				src.insets.bottom,
				src.insets.right);
		graphiViewInsets =
			new Insets(
				src.graphiViewInsets.top,
				src.graphiViewInsets.left,
				src.graphiViewInsets.bottom,
				src.graphiViewInsets.right);
		colors = new Color[src.colors.length];
		System.arraycopy(src.colors, 0, colors, 0, colors.length);
		handlerName = new String[src.handlerName.length];
		System.arraycopy(src.handlerName, 0, handlerName, 0, handlerName.length);

		seriesPropertyList = new ArrayList(src.seriesPropertyList.size());
		for (Iterator i = src.seriesPropertyList.iterator(); i.hasNext();) {
			GraphSeriesProperty item = (GraphSeriesProperty) i.next();
			seriesPropertyList.add(new GraphSeriesProperty(item));
		}
		foldCount = src.foldCount;
		referenceTime = src.referenceTime;
		horizontalPixcelWidth = src.horizontalPixcelWidth;
		scaleOneHeightPixel = src.scaleOneHeightPixel;
		explanatoryColor = src.explanatoryColor;
		explanatoryFont = src.explanatoryFont;
		firstFormat = src.firstFormat;
		secondFormat = src.secondFormat;
		verticallyScaleProperty = src.verticallyScaleProperty;
	}

	/**
	 * �c�X�P�[���̖ڐ���̐���Ԃ��܂��B
	 * @return �c�X�P�[���̖ڐ���̐�
	 */
	public int getVerticalScaleCount() {
		return verticalScaleCount;
	}

	/**
	 * �c�X�P�[���̖ڐ������̃s�N�Z������Ԃ��܂��B
	 * @return �c�X�P�[���̖ڐ������̃s�N�Z����
	 */
	public int getVerticalScaleHeight() {
		return verticalScaleHeight;
	}

	/**
	 * ���X�P�[���������̖ڐ���̐���Ԃ��܂��B
	 * @return ���X�P�[���������̖ڐ���̐�
	 */
	public int getHorizontalScaleCount() {
		return horizontalScaleCount;
	}

	/**
	 * ���X�P�[���������̖ڐ������̎��ԁi�_�b�j��Ԃ��܂��B
	 * @return ���X�P�[���������̖ڐ������̎��ԁi�_�b�j
	 */
	public long getHorizontalScaleWidth() {
		return horizontalScaleWidth;
	}

	/**
	 * ���X�P�[���������̖ڐ���̐���ݒ肵�܂��B
	 * @param horizontalScaleCount ���X�P�[���������̖ڐ���̐�
	 */
	public void setHorizontalScaleCount(int horizontalScaleCount) {
		Object old = deepCopy();
		this.horizontalScaleCount = horizontalScaleCount;
		firePropertyChange(X_SCALE_CHANGE_EVENT, old, this);
	}

	/**
	 * ���X�P�[���������̖ڐ������̎��ԁi�_�b�j��ݒ肵�܂��B
	 * @param horizontalScaleWidth ���X�P�[���������̖ڐ������̎��ԁi�_�b�j
	 */
	public void setHorizontalScaleWidth(long horizontalScaleWidth) {
		Object old = deepCopy();
		this.horizontalScaleWidth = horizontalScaleWidth;
		firePropertyChange(X_SCALE_CHANGE_EVENT, old, this);
	}

	/**
	 * �R���|�[�l���g�̗]�������̃C���Z�b�c��Ԃ��܂��B
	 * @return �R���|�[�l���g�̗]�������̃C���Z�b�c
	 */
	public Insets getInsets() {
		return insets;
	}

	/**
	 * �O���t�r���[�ȊO�̗]�������̃C���Z�b�c��Ԃ��܂��B
	 * @return �R���|�[�l���g�̗]�������̃C���Z�b�c
	 */
	public Insets getGraphiViewInsets() {
		return graphiViewInsets;
	}

	/**
	 * �O���t�E�}��� Color �I�u�W�F�N�g�̔z���Ԃ��܂��B
	 * @return �O���t�E�}��� Color �I�u�W�F�N�g�̔z��
	 */
	public Color[] getColors() {
		return colors;
	}

	/**
	 * ���̃v���p�e�B�Őݒ肳��Ă���A�O���[�v�̃T�C�Y��Ԃ��܂��B
	 * @return �O���[�v�̃T�C�Y
	 */
	public int getGroupSize() {
		return seriesPropertyList.size();
	}

	/**
	 * ���ݐݒ肳��Ă���O���[�v��Ԃ��܂��B
	 * @return ���ݐݒ肳��Ă���O���[�v
	 */
	public int getGroup() {
		return group;
	}
	
	/**
	 * ���ݐݒ肳��Ă���O���[�v����Ԃ��܂��B
	 * @return ���ݐݒ肳��Ă���O���[�v��
	 */
	public String getGroupName() {
		return getGraphSeriesPropertyModel(getGroup()).getSeriesName();
	}

	/**
	 * �I���O���[�v��ݒ肵�܂�
	 * @param group �O���[�v
	 */
	public void setGroup(int group) {
		if (group < 0 || group >= getGroupSize()) {
			throw new IllegalArgumentException("group : " + group);
		}
		logger.debug("group : " + group);
		Object old = deepCopy();
		this.group = group;
		firePropertyChange(GROUP_CHANGE_EVENT, old, this);
	}

	public void nextGroup() {
		if (group < (getGroupSize() - 1)) {
			Object old = deepCopy();
			group++;
			firePropertyChange(GROUP_CHANGE_EVENT, old, this);
		}
	}
	
	public void prevGroup() {
		if (0 < group) {
			Object old = deepCopy();
			group--;
			firePropertyChange(GROUP_CHANGE_EVENT, old, this);
		}
	}

	private GraphSeriesProperty getGraphSeriesPropertyModel(int group) {
		if (group < 0 || group >= getGroupSize()) {
			throw new IllegalArgumentException("group : " + group);
		}
		return (GraphSeriesProperty) seriesPropertyList.get(group);
	}

	/**
	 * �ݒ肳��Ă���V���[�Y�̃T�C�Y��Ԃ��܂��B
	 * @return �ݒ肳��Ă���V���[�Y�̃T�C�Y�i�O���[�v���̃V���[�Y���j
	 */
	public int getSeriesSize() {
		return getGraphSeriesPropertyModel(group).getSeriesSize();
	}

	/**
	 * �c�X�P�[���̍ŏ��l��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �c�X�P�[���̍ŏ��l
	 */
	public double getVerticalMinimum(int series) {
		return getGraphSeriesPropertyModel(group).getVerticalMinimum(series);
	}

	/**
	 * �c�X�P�[���̍ő�l��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �c�X�P�[���̍ő�l
	 */
	public double getVerticalMaximum(int series) {
		return getGraphSeriesPropertyModel(group).getVerticalMaximum(series);
	}

	/**
	 * �c�X�P�[���̍ŏ��l��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param verticalMinimum �c�X�P�[���̍ŏ��l
	 */
	public void setVerticalMinimum(int series, double verticalMinimum) {
		Object old = deepCopy();
		getGraphSeriesPropertyModel(group).setVerticalMinimum(
			series,
			verticalMinimum);
		firePropertyChange(old, this);
	}

	/**
	 * �c�X�P�[���̍ő�l��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param verticalMaximum �c�X�P�[���̍ő�l
	 */
	public void setVerticalMaximum(int series, double verticalMaximum) {
		Object old = deepCopy();
		getGraphSeriesPropertyModel(group).setVerticalMaximum(
			series,
			verticalMaximum);
		firePropertyChange(old, this);
	}

	/**
	 * �f�[�^�v���o�C�_����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �f�[�^�v���o�C�_��
	 */
	public String getDataProviderName(int series) {
		return getGraphSeriesPropertyModel(group).getDataProviderName(series);
	}

	/**
	 * �f�[�^�z���_����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �f�[�^�z���_��
	 */
	public String getDataHolderName(int series) {
		return getGraphSeriesPropertyModel(group).getDataHolderName(series);
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�ƒl��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @return �Q�ƒl
	 */
	public double getReferenceValue(int series, int fold) {
		return getGraphSeriesPropertyModel(group).getReferenceValue(series, fold);
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�ƒl��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �Q�ƒl
	 */
	public double getReferenceValue(int series) {
		return getReferenceValue(series, 0);
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�ƒl��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @param referenceValue �Q�ƒl
	 */
	public void setReferenceValue(int series, int fold, double referenceValue) {
		Object old = deepCopy();
		getGraphSeriesPropertyModel(group).setReferenceValue(
			series,
			fold,
			referenceValue);
		firePropertyChange(old, this);
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�ƒl��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param referenceValue �Q�ƒl
	 */
	public void setReferenceValue(int series, double referenceValue) {
		setReferenceValue(series, 0, referenceValue);
	}
	
	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�Ǝ�����ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @param referenceTime �Q�Ǝ���
	 */
	public void setReferenceTime(int series, int fold, Timestamp referenceTime) {
		Object old = deepCopy();
		getGraphSeriesPropertyModel(group).setReferenceTime(
			series,
			fold,
			referenceTime);
		firePropertyChange(old, this);
	}
	
	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�Ǝ�����ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param referenceTime �Q�Ǝ���
	 */
	public void setReferenceTime(int series, Timestamp referenceTime) {
		setReferenceTime(series, 0, referenceTime);
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�Ǝ�����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @return �Q�Ǝ���
	 */
	public Timestamp getReferenceTime(int series, int fold) {
		return getGraphSeriesPropertyModel(getGroup()).getReferenceTime(series, fold);
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�Ǝ�����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �Q�Ǝ���
	 */
	public Timestamp getReferenceTime(int series) {
		return getReferenceTime(series, 0);
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A��C���f�b�N�X��Ԃ��܂��B
	 * @return ��C���f�b�N�X�̔z��
	 */
	public int[] getGroupColumnIndex() {
		return new int[0];
	}

	/**
	 * �V���[�Y�v���p�e�B(�O���[�v�̐ݒ�)��ǉ����܂��B
	 * @param property �V���[�Y�v���p�e�B
	 */
	public void addSeriesProperty(GraphSeriesProperty property) {
		Object old = deepCopy();
		seriesPropertyList.add(property);
		firePropertyChange(old, this);
	}
	
	/**
	 * ���ݕ\�����Ă���n���h���[���̃C���f�b�N�X��ݒ肵�܂��B
	 * @param name �n���h���[��
	 */
	public void setListHandlerIndex(int index) {
		if (index < 0 || index >= handlerName.length) {
			throw new IllegalArgumentException("index : " + index);
		}
		Object old = deepCopy();
		handlerIndex = index;
		firePropertyChange(X_SCALE_CHANGE_EVENT, old, this);
	}
	
	/**
	 * ���݃C���f�b�N�X�Ŏw�肳��Ă���n���h���[����Ԃ��܂��B
	 * @return ���݃C���f�b�N�X�Ŏw�肳��Ă���n���h���[��
	 */
	public String getListHandlerName() {
		return handlerName[handlerIndex];
	}
	
	/**
	 * �܂�Ԃ��񐔂�Ԃ��܂��B
	 * @return �܂�Ԃ���
	 */
	public int getFoldCount() {
		return foldCount;
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�|�C���g���̂�Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return ���݈ʒu�Â����Ă���O���[�v�́A��C���f�b�N�X��Ԃ��܂��B
	 */
	public String getPointName(int series) {
		return getGraphSeriesPropertyModel(getGroup()).getPointName(series);
	}


	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�P�ʋL����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return ���݈ʒu�Â����Ă���O���[�v�́A�P�ʋL����Ԃ��܂��B
	 */
	public String getPointMark(int series) {
		return getGraphSeriesPropertyModel(getGroup()).getPointMark(series);
	}

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A���ݒl�\���V���{����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return ���݈ʒu�Â����Ă���O���[�v�́A���ݒl�\���V���{����Ԃ��܂��B
	 */
	public ExplanatoryNotesText getSymbol(int series) {
		return getGraphSeriesPropertyModel(getGroup()).getSymbol(series);
	}
	
	/**
	 * ���݃N���b�N����Ă���Q�Ɠ�����Ԃ��܂�
	 * @return ���݃N���b�N����Ă���Q�Ɠ�����Ԃ��܂�
	 */
	public Timestamp getReferenceTime() {
		return referenceTime;
	}
	
	/**
	 * ���݃N���b�N����Ă���Q�Ɠ�����ݒ肵�܂�
	 * @param time ���݃N���b�N����Ă���Q�Ɠ�����ݒ肵�܂�
	 */
	public void setReferenceTime(Timestamp time) {
		Timestamp old = null;
		if (referenceTime != null) {
			old = (Timestamp) referenceTime.clone();
		}
		referenceTime = time;
		firePropertyChange(old, time);
	}

	/**
	 * �\�������O���t�̉��s�N�Z������Ԃ��܂��B
	 * @return �\�������O���t�̉��s�N�Z������Ԃ��܂��B
	 */
	public int getHorizontalPixcelWidth() {
		return horizontalPixcelWidth;
	}
	
	/**
	 * X���X�P�[���̖ڐ��蕝��Ԃ��܂��B
	 * @return X���X�P�[���̖ڐ��蕝��Ԃ��܂��B
	 */
	public int getScaleOneHeightPixel() {
		return scaleOneHeightPixel;
	}

	/**
	 * �Q�ƕ\���̕����F��Ԃ��܂�
	 * @return �Q�ƕ\���̕����F��Ԃ��܂�
	 */
	public Color getExplanatoryColor() {
		return explanatoryColor;
	}

	/**
	 * �Q�ƕ\���̃t�H���g��Ԃ��܂�
	 * @return �Q�ƕ\���̃t�H���g��Ԃ��܂�
	 */
	public Font getExplanatoryFont() {
		return explanatoryFont;
	}

	
    public Collection getGroupNames() {
        ArrayList groupNames = new ArrayList(seriesPropertyList.size());
        int index = 0;
        for (Iterator i = seriesPropertyList.iterator(); i.hasNext(); index++) {
            GraphSeriesProperty gp = (GraphSeriesProperty) i.next();
            groupNames.add(String.format("%03d : %s", index, gp.getSeriesName()));
        }
        return groupNames;
    }

    public String getFirstFormat() {
        return firstFormat;
    }
    public String getSecondFormat() {
        return secondFormat;
    }
    public void setFirstFormat(String format) {
        if (format == null) {
            format = "MM/dd";
        }
		Object old = deepCopy();
        firstFormat = format;
		firePropertyChange(X_SCALE_CHANGE_EVENT, old, this);
    }
    public void setSecondFormat(String format) {
        if (format == null) {
            format = "HH:mm";
        }
		Object old = deepCopy();
        secondFormat = format;
		firePropertyChange(X_SCALE_CHANGE_EVENT, old, this);
    }

	public VerticallyScaleProperty getVerticallyScaleProperty() {
		return verticallyScaleProperty;
	}

    /**
	 * ���̃I�u�W�F�N�g�̕�����\����Ԃ��܂��B
	 * �����v���p�e�B�𕶎���\�����ĕԂ��܂��B���A���̃��\�b�h�͊J���i�K�Ŏg�p���ׂ��ł���A
	 * �e�X�g�ړI�ȊO�Ŏg�p���Ȃ��ł��������B�����A�Ԃ������e���ύX����邱�Ƃ�����܂��B
	 * @return �I�u�W�F�N�g�̕�����\��
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("verticalScaleCount:" + verticalScaleCount);
		buffer.append(",verticalScaleHeight:" + verticalScaleHeight);
		buffer.append(",horizontalScaleCount:" + horizontalScaleCount);
		buffer.append(",horizontalScaleWidth:" + horizontalScaleWidth);
		buffer.append(",insets:" + insets);
		for (int i = 0; i < colors.length; i++) {
			buffer.append(",colors:" + colors[i]);
		}
		List handler = Arrays.asList(handlerName);
		buffer.append("handlerName:" + handler);
		buffer.append(",group:" + group);
		buffer.append("seriesPropertyList:" + seriesPropertyList);
		buffer.append(",foldCount:" + foldCount);
		buffer.append(",referenceTime:").append(referenceTime)
		.append(",horizontalPixcelWidth:").append(horizontalPixcelWidth)
		.append(",scaleOneHeightPixel:").append(scaleOneHeightPixel)
		.append(",firstFormat:").append(firstFormat)
		.append(",secondFormat:").append(secondFormat)
		.append("verticalScaleCount:").append(verticallyScaleProperty);
		return buffer.toString();
	}

	/**
	 * �ʂ̃I�u�W�F�N�g���A���̃I�u�W�F�N�g�Ɠ��������ׂ܂��B
	 * ���ʂ͈����� null �łȂ��A���̃I�u�W�F�N�g�Ɠ����e�v���p�e�B�[�i�t�B�[���h�j�̒l������
	 * �I�u�W�F�N�g�ł���ꍇ�� true ��Ԃ��܂��B
	 * @param obj ���� DefaultGraphPropertyModel �Ɠ��������ǂ��������肳���I�u�W�F�N�g
	 * @return �I�u�W�F�N�g�������ł���ꍇ�� true�A�����łȂ��ꍇ�� false
	 */
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DefaultGraphPropertyModel)) {
			return false;
		}
		DefaultGraphPropertyModel pm = (DefaultGraphPropertyModel) obj;
		return pm.group == this.group
			&& pm.handlerIndex == this.handlerIndex
			&& pm.verticalScaleCount == this.verticalScaleCount
			&& pm.verticalScaleHeight == this.verticalScaleHeight
			&& pm.horizontalScaleCount == this.horizontalScaleCount
			&& pm.horizontalScaleWidth == this.horizontalScaleWidth
			&& pm.insets.equals(this.insets)
			&& pm.graphiViewInsets.equals(this.graphiViewInsets)
			&& Arrays.equals(pm.colors, this.colors)
			&& Arrays.equals(pm.handlerName, this.handlerName)
			&& pm.seriesPropertyList.equals(this.seriesPropertyList)
			&& pm.foldCount == this.foldCount
			&& ((pm.referenceTime == null && this.referenceTime == null) || pm.referenceTime.equals(this.referenceTime))
			&& pm.horizontalPixcelWidth == this.horizontalPixcelWidth
			&& pm.scaleOneHeightPixel == this.scaleOneHeightPixel
			&& ((pm.explanatoryColor == null && this.explanatoryColor == null) || pm.explanatoryColor.equals(this.explanatoryColor))
			&& ((pm.explanatoryFont == null && this.explanatoryFont == null) || pm.explanatoryFont.equals(this.explanatoryFont))
			&& pm.firstFormat.equals(this.firstFormat)
			&& pm.secondFormat.equals(this.secondFormat)
			&& pm.verticallyScaleProperty.equals(verticallyScaleProperty);
	}

	/**
	 * ���� DefaultGraphPropertyModel �̃n�b�V���R�[�h���v�Z���܂��B
	 * @return ���� DefaultGraphPropertyModel �̃n�b�V���R�[�h�l
	 */
	public int hashCode() {
		int result = 17;
		result = 37 * result + group;
		result = 37 * result + handlerIndex;
		result = 37 * result + verticalScaleCount;
		result = 37 * result + verticalScaleHeight;
		result = 37 * result + horizontalScaleCount;
		result =
			37 * result
				+ (int) (horizontalScaleWidth ^ (horizontalScaleWidth >>> 32));
		result = 37 * result + insets.hashCode();
		result = 37 * result + graphiViewInsets.hashCode();
		for (int i = 0; i < colors.length; i++) {
			result = 37 * result + colors[i].hashCode();
		}
		for (int i = 0; i < handlerName.length; i++) {
			result = 37 * result + handlerName[i].hashCode();
		}
		result = 37 * result + seriesPropertyList.hashCode();
		result = 37 * result + foldCount;
		if (referenceTime != null) {
			result = 37 * result + referenceTime.hashCode();
		}
		result = 37 * result + horizontalPixcelWidth;
		result = 37 * result + scaleOneHeightPixel;
		if (explanatoryColor != null)
		    result = 37 * result + explanatoryColor.hashCode();
		if (explanatoryFont != null)
		    result = 37 * result + explanatoryFont.hashCode();
		result = 37 * result + firstFormat.hashCode();
		result = 37 * result + secondFormat.hashCode();
		result = 37 * result + verticallyScaleProperty.hashCode();
		return result;
	}
}
