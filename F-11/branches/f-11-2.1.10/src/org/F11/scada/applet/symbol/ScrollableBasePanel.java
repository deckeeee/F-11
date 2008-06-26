package org.F11.scada.applet.symbol;

/*
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

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * �X�N���[���\�ȃx�[�X�摜�̃N���X�ł��B
 * @author Youhei Horikawa <hori@users.sourceforge.jp>
 */
public class ScrollableBasePanel extends JPanel implements Scrollable {
	private static final long serialVersionUID = 6253397121389064492L;

	/** ���{�^���̃X�N���[���ʂł��B */
	private int maxUnitIncrement = 10;

	private static final int MAX_WIDTH = 1028;
	private static final int MAX_HEIGHT = 780;


	public ScrollableBasePanel(LayoutManager l) {
		super(l);
		this.setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
	}

	// �ȉ��̃��\�b�h�� Scrollable �C���^�[�t�F�C�X���������Ă��܂��B

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
										  int orientation,
										  int direction) {
		//Get the current position.
		int currentPosition = 0;
		if (orientation == SwingConstants.HORIZONTAL)
			currentPosition = visibleRect.x;
		else
			currentPosition = visibleRect.y;

		//Return the number of pixels between currentPosition
		//and the nearest tick mark in the indicated direction.
		if (direction < 0) {
			int newPosition = currentPosition -
							 (currentPosition / maxUnitIncrement) *
							  maxUnitIncrement;
			return (newPosition == 0) ? maxUnitIncrement : newPosition;
		} else {
			return ((currentPosition / maxUnitIncrement) + 1) *
				   maxUnitIncrement - currentPosition;
		}
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
										   int orientation,
										   int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return visibleRect.width - maxUnitIncrement;
		else
			return visibleRect.height - maxUnitIncrement;
	}

	/**
	 * @return ��� false ��Ԃ��܂��B
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * @return ��� false ��Ԃ��܂��B
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public void setMaxUnitIncrement(int pixels) {
		maxUnitIncrement = pixels;
	}
}
