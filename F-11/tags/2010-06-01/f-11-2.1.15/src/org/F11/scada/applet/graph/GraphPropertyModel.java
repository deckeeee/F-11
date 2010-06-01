/*
 * $Header: /cvsroot/f-11/F-11/src/org/F11/scada/applet/graph/GraphPropertyModel.java,v 1.13.2.5 2007/07/11 07:47:18 frdm Exp $
 * $Revision: 1.13.2.5 $
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
import java.beans.PropertyChangeListener;
import java.sql.Timestamp;
import java.util.Collection;

public interface GraphPropertyModel {
    /** �O���[�v�ύX�C�x���g�̃v���p�e�B�[�� */
    String GROUP_CHANGE_EVENT =
        GraphPropertyModel.class.getName() + "_GROUP_CHANGE_EVENT";
    /** �����ύX�C�x���g�̃v���p�e�B�[�� */
    String X_SCALE_CHANGE_EVENT =
    	GraphPropertyModel.class.getName() + "_X_SCALE_CHANGE_EVENT";

	/**
	 * �C���X�^���X���f�B�[�v�R�s�[���܂��B
	 * @return GraphPropertyModel �f�B�[�v�R�s�[���ꂽ�C���X�^���X�B
	 */
	public GraphPropertyModel deepCopy();

	/**
	 * ���X�i�[��ǉ����܂��B
	 * @param l PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener l);

	/**
	 * ���X�i�[���폜���܂��B
	 * @param l PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener l);

	/**
	 * �c�X�P�[���̖ڐ���̐���Ԃ��܂��B
	 * @return �c�X�P�[���̖ڐ���̐�
	 */
	public int getVerticalScaleCount();

	/**
	 * �c�X�P�[���̖ڐ������̃s�N�Z������Ԃ��܂��B
	 * @return �c�X�P�[���̖ڐ������̃s�N�Z����
	 */
	public int getVerticalScaleHeight();

	/**
	 * ���X�P�[���������̖ڐ���̐���Ԃ��܂��B
	 * @return ���X�P�[���������̖ڐ���̐�
	 */
	public int getHorizontalScaleCount();

	/**
	 * ���X�P�[���������̖ڐ������̎��ԁi�_�b�j��Ԃ��܂��B
	 * @return ���X�P�[���������̖ڐ������̎��ԁi�_�b�j
	 */
	public long getHorizontalScaleWidth();

	/**
	 * ���X�P�[���������̖ڐ���̐���ݒ肵�܂��B
	 * @param horizontalScaleCount ���X�P�[���������̖ڐ���̐�
	 */
	public void setHorizontalScaleCount(int horizontalScaleCount);

	/**
	 * ���X�P�[���������̖ڐ������̎��ԁi�_�b�j��ݒ肵�܂��B
	 * @param horizontalScaleWidth ���X�P�[���������̖ڐ������̎��ԁi�_�b�j
	 */
	public void setHorizontalScaleWidth(long horizontalScaleWidth);

	/**
	 * �O���t�r���[�ȊO�̃R���|�[�l���g�̗]�������̃C���Z�b�c��Ԃ��܂��B
	 * @return �R���|�[�l���g�̗]�������̃C���Z�b�c
	 */
	public Insets getInsets();

	/**
	 * �O���t�r���[�ȊO�̗]�������̃C���Z�b�c��Ԃ��܂��B
	 * @return �R���|�[�l���g�̗]�������̃C���Z�b�c
	 */
	public Insets getGraphiViewInsets();

	/**
	 * �O���t�E�}��� Color �I�u�W�F�N�g�̔z���Ԃ��܂��B
	 * @return �O���t�E�}��� Color �I�u�W�F�N�g�̔z��
	 */
	public Color[] getColors();

	/**
	 * ���̃v���p�e�B�Őݒ肳��Ă���A�O���[�v�̃T�C�Y��Ԃ��܂��B
	 * @return �O���[�v�̃T�C�Y
	 */
	public int getGroupSize();

	/**
	 * ���ݐݒ肳��Ă���O���[�v��Ԃ��܂��B
	 * @return ���ݐݒ肳��Ă���O���[�v
	 */
	public int getGroup();

	/**
	 * ���ݐݒ肳��Ă���O���[�v����Ԃ��܂��B
	 * @return ���ݐݒ肳��Ă���O���[�v��
	 */
	public String getGroupName();

	/**
	 * �I���O���[�v��ݒ肵�܂�
	 * @param group �O���[�v
	 */
	public void setGroup(int group);

	/**
	 * ���̃O���[�v�Ɉړ����܂��B
	 */
	void nextGroup();
	
	/**
	 * �O�̃O���[�v�Ɉړ����܂��B
	 */
	void prevGroup();

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�V���[�Y�T�C�Y��Ԃ��܂��B
	 * @return ���݈ʒu�Â����Ă���O���[�v�́A�V���[�Y�T�C�Y
	 */
	public int getSeriesSize();

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�c�X�P�[���̍ŏ��l��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �c�X�P�[���̍ŏ��l
	 */
	public double getVerticalMinimum(int series);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�c�X�P�[���̍ő�l��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �c�X�P�[���̍ő�l
	 */
	public double getVerticalMaximum(int series);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�c�X�P�[���̍ŏ��l��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param verticalMinimum �c�X�P�[���̍ŏ��l
	 */
	public void setVerticalMinimum(int series, double verticalMinimum);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�c�X�P�[���̍ő�l��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param verticalMaximum �c�X�P�[���̍ő�l
	 */
	public void setVerticalMaximum(int series, double verticalMaximum);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�f�[�^�v���o�C�_����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �f�[�^�v���o�C�_��
	 */
	public String getDataProviderName(int series);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�f�[�^�z���_����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �f�[�^�z���_��
	 */
	public String getDataHolderName(int series);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�ƒl��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @return �Q�ƒl
	 */
	public double getReferenceValue(int series, int fold);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�ƒl��Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �Q�ƒl
	 */
	public double getReferenceValue(int series);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�ƒl��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @param referenceValue �Q�ƒl
	 */
	public void setReferenceValue(int series, int fold, double referenceValue);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�ƒl��ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param referenceValue �Q�ƒl
	 */
	public void setReferenceValue(int series, double referenceValue);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�Ǝ�����ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @param referenceTime �Q�Ǝ���
	 */
	public void setReferenceTime(int series, int fold, Timestamp referenceTime);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�Ǝ�����ݒ肵�܂��B
	 * @param series �V���[�Y
	 * @param referenceTime �Q�Ǝ���
	 */
	public void setReferenceTime(int series, Timestamp referenceTime);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�Ǝ�����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @param fold �܂�Ԃ��ʒu
	 * @return �Q�Ǝ���
	 */
	public Timestamp getReferenceTime(int series, int fold);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�Q�Ǝ�����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return �Q�Ǝ���
	 */
	public Timestamp getReferenceTime(int series);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A��C���f�b�N�X��Ԃ��܂��B
	 * @return ��C���f�b�N�X�̔z��
	 */
	public int[] getGroupColumnIndex();
	
	/**
	 * �V���[�Y�v���p�e�B(�O���[�v�̐ݒ�)��ǉ����܂��B
	 * @param property �V���[�Y�v���p�e�B
	 */
	public void addSeriesProperty(GraphSeriesProperty property);
	
	/**
	 * ���ݕ\�����Ă���n���h���[���̃C���f�b�N�X��ݒ肵�܂��B
	 * @param name �n���h���[��
	 */
	public void setListHandlerIndex(int index);
	
	/**
	 * ���݃C���f�b�N�X�Ŏw�肳��Ă���n���h���[����Ԃ��܂��B
	 * @return ���݃C���f�b�N�X�Ŏw�肳��Ă���n���h���[��
	 */
	public String getListHandlerName();
	
	/**
	 * �܂�Ԃ��񐔂�Ԃ��܂��B
	 * @return �܂�Ԃ���
	 */
	public int getFoldCount();

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�|�C���g���̂�Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return ���݈ʒu�Â����Ă���O���[�v�́A��C���f�b�N�X��Ԃ��܂��B
	 */
	public String getPointName(int series);

	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A�P�ʋL����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return ���݈ʒu�Â����Ă���O���[�v�́A�P�ʋL����Ԃ��܂��B
	 */
	public String getPointMark(int series);
	
	/**
	 * ���݈ʒu�Â����Ă���O���[�v�́A���ݒl�\���V���{����Ԃ��܂��B
	 * @param series �V���[�Y
	 * @return ���݈ʒu�Â����Ă���O���[�v�́A���ݒl�\���V���{����Ԃ��܂��B
	 */
	public ExplanatoryNotesText getSymbol(int series);
	
	/**
	 * ���݃N���b�N����Ă���Q�Ɠ�����Ԃ��܂�
	 * @return ���݃N���b�N����Ă���Q�Ɠ�����Ԃ��܂�
	 */
	public Timestamp getReferenceTime();
	
	/**
	 * ���݃N���b�N����Ă���Q�Ɠ�����ݒ肵�܂�
	 * @param time ���݃N���b�N����Ă���Q�Ɠ�����ݒ肵�܂�
	 */
	public void setReferenceTime(Timestamp time);
	
	/**
	 * �\�������O���t�̉��s�N�Z������Ԃ��܂��B
	 * @return �\�������O���t�̉��s�N�Z������Ԃ��܂��B
	 */
	public int getHorizontalPixcelWidth();
	
	/**
	 * X���X�P�[���̖ڐ��蕝��Ԃ��܂��B
	 * @return X���X�P�[���̖ڐ��蕝��Ԃ��܂��B
	 */
	public int getScaleOneHeightPixel();
	
	/**
	 * �Q�ƕ\���̕����F��Ԃ��܂�
	 * @return �Q�ƕ\���̕����F��Ԃ��܂�
	 */
	public Color getExplanatoryColor();

	/**
	 * �Q�ƕ\���̃t�H���g��Ԃ��܂�
	 * @return �Q�ƕ\���̃t�H���g��Ԃ��܂�
	 */
	public Font getExplanatoryFont();

    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener);

    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener);


	/**
	 * ���ݐݒ肳��Ă���O���[�v����Ԃ��܂��B
	 * @return ���ݐݒ肳��Ă���O���[�v��
	 */
	public Collection getGroupNames();
	
	/**
	 * 1��ڂ̃t�H�[�}�b�g�������Ԃ��܂�
	 * @return 1��ڂ̃t�H�[�}�b�g�������Ԃ��܂�
	 */
	public String getFirstFormat();

	/**
	 * 1��ڂ̃t�H�[�}�b�g�������ݒ肵�܂�
	 * @param format 1��ڂ̃t�H�[�}�b�g�������ݒ肵�܂�
	 */
	public void setFirstFormat(String format);
	
	/**
	 * 2��ڂ̃t�H�[�}�b�g�������Ԃ��܂�
	 * @return 2��ڂ̃t�H�[�}�b�g�������Ԃ��܂�
	 */
	public String getSecondFormat();
	
	/**
	 * 2��ڂ̃t�H�[�}�b�g�������ݒ肵�܂�
	 * @param format 2��ڂ̃t�H�[�}�b�g�������ݒ肵�܂�
	 */
	public void setSecondFormat(String format);
	
	/**
	 * �c�X�P�[���̃v���p�e�B�[��Ԃ��܂��B
	 * @return �c�X�P�[���̃v���p�e�B�[��Ԃ��܂�
	 */
	public VerticallyScaleProperty getVerticallyScaleProperty();
}
