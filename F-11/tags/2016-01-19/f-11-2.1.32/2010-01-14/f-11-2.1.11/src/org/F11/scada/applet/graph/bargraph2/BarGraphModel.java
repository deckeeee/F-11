/*
 * Projrct F-11 - Web SCADA for Java Copyright (C) 2002 Freedom, Inc. All Rights
 * Reserved. This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 */
package org.F11.scada.applet.graph.bargraph2;

import java.rmi.RemoteException;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.F11.scada.Service;

public interface BarGraphModel extends Service {
	/**
	 * �o�[�O���t�R���|�[�l���g��Ԃ��܂��B
	 * @return
	 */
	public JComponent getJComponent();
	/**
	 * �\���|�C���g��ύX���܂��B
	 * @param series
	 */
	public void changePoint(BarSeries series) throws RemoteException;
	/**
	 * �\���f�[�^�̎Q�ƈʒu��ύX���܂��B
	 */
	public void changePeriod(int offset);
	/**
	 * ���X�P�[���P�P�ʒ��̃o�[�̖{����Ԃ��܂��B
	 * @return
	 */
	public int getBarCount();
	/**
	 * ���X�P�[���̐���Ԃ��܂��B
	 * @return
	 */
	public int getBlockCount();
	/**
	 * �\���f�[�^�̃}�g���b�N�X��Ԃ��܂��B
	 * @return
	 */
	public BarData[][] getDataMatrix();

	/**
	 * ���g�̃C���f�b�N�X��ݒ肵�܂��B<br>
	 * �V���[�Y���̃|�C���g���擾���鎞�̃C���f�b�N�X�Ƃ��Ďg�p���܂��B
	 * @param barIndex
	 */
	public void setBarGraphModelIndex(int barIndex);
	/**
	 * toString()�̕Ԃ�l��ݒ肵�܂��B
	 * @param text
	 */
	public void setText(String text);
	/**
	 * �R���|�[�l���g�̃o�b�N�O���E���h�C���[�W��ݒ肵�܂��B
	 * @param icon
	 */
	public void setIcon(Icon icon);
	/**
	 * �Q�Ƃ���e�[�u������ݒ肵�܂��B
	 * @param handlerNames
	 */
	public void setHandlerNames(String[] handlerNames);
	/**
	 * ���X�P�[���P�P�ʒ��̃o�[�̖{����ݒ肵�܂��B
	 * @param count
	 */
	public void setBarCount(int count);
	/**
	 * �f�[�^�̎Q�Ɠ��𑀍삷��N���X��ݒ肵�܂��B
	 * @param refDate
	 */
	public void setReferenceDate(ReferenceDate refDate);
	/**
	 * �o�[��`�悷��R���|�[�l���g��ݒ肵�܂��B
	 * @param view
	 */
	public void setBarGraph2View(BarGraph2View view);
	/**
	 * �c�X�P�[�����Ǘ�����N���X��ǉ����܂��B
	 * @param symbol
	 */
	public void setVerticallyScaleModel(VerticallyScaleModel vertical);
	/**
	 * �R���|�[�l���g�ɒP�ʕ\���p�e�L�X�g�V���{����ǉ����܂��B
	 * @param symbol
	 */
	public void addUnitsSymbol(TextUnitsSymbol symbol);
}
