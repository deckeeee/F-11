/*
 * Project F-11 - Web SCADA for Java
 * Copyright (C) 2002-2008 Freedom, Inc. All Rights Reserved.
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

package org.F11.scada.applet.ngraph.draw;


import java.awt.Graphics;

import org.F11.scada.applet.ngraph.LogData;


/**
 * �g�����h�O���t�̃h���[�G���W���ł��B
 * 
 * @author maekawa
 * 
 */
public interface GraphDraw {
	/**
	 * �V���[�Y�f�[�^��`�悵�܂�
	 * 
	 * @param g �O���t�B�b�N�R���e�L�X�g
	 * @param currentIndex TODO
	 * @param displayDatas TODO
	 * @param isAllSpanDisplayMode TODO
	 */
	void drawSeries(
			Graphics g,
			int currentIndex,
			LogData[] displayDatas,
			boolean isAllSpanDisplayMode);

	/**
	 * �P�ʋL����`�悵�܂�
	 * 
	 * @param g �O���t�B�b�N�R���e�L�X�g
	 * @param top ��]��
	 * @param x x���W
	 * @param drawSeriesIndex �P�ʕ`�悷��V���[�Y�C���f�b�N�X
	 */
	void drawUnitMark(Graphics g, int top, int x, int drawSeriesIndex);

	/**
	 * �c�X�P�[���̖ڐ��������`�悵�܂�
	 * 
	 * @param g �O���t�B�b�N�R���e�L�X�g
	 * @param top ��]��
	 * @param x x���W
	 * @param y y���W
	 * @param i �ڐ����̃C���f�b�N�X
	 * @param drawSeriesIndex �P�ʕ`�悷��V���[�Y�C���f�b�N�X
	 */
	void drawVerticalString(
			Graphics g,
			int top,
			int x,
			int y,
			int i,
			int drawSeriesIndex);
}
